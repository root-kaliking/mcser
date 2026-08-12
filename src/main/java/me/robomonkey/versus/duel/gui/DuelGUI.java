package me.robomonkey.versus.duel.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.arena.Arena;
import me.robomonkey.versus.arena.ArenaManager;
import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DuelGUI {
    private final Player viewer;
    private final ArenaManager arenaManager = ArenaManager.getInstance();

    public DuelGUI(Player viewer) {
        this.viewer = viewer;
    }

    public void open() {
        List<Arena> arenas = arenaManager.getAllArenas();
        int size = Math.max(18, ((arenas.size() / 9) + 1) * 9);
        if (size > 54) size = 54;

        SGMenu menu = Versus.spiGUI.create("竞技场列表", size / 9);

        for (int i = 0; i < arenas.size() && i < size; i++) {
            Arena arena = arenas.get(i);
            menu.setButton(i, getArenaButton(arena));
        }

        viewer.openInventory(menu.getInventory());
    }

    private SGButton getArenaButton(Arena arena) {
        boolean isInUse = !arena.isAvailable();
        boolean isEnabled = arena.isEnabled();

        Material material;
        String statusText;
        String lore;

        if (!isEnabled) {
            material = Material.BARRIER;
            statusText = "&c&l未启用";
            lore = "&7该竞技场尚未配置完成。";
        } else if (isInUse) {
            material = Material.RED_WOOL;
            statusText = "&c&l使用中";
            List<Duel> duels = arena.getActiveDuels();
            StringBuilder players = new StringBuilder();
            for (Duel duel : duels) {
                for (Player p : duel.getPlayers()) {
                    players.append("&7- &e").append(p.getName()).append("\n");
                }
            }
            lore = "&7当前正在决斗的玩家:\n" + players.toString() + "\n&a点击观战!";
        } else {
            material = Material.GREEN_WOOL;
            statusText = "&a&l空闲";
            lore = "&7该竞技场空闲。\n&a点击选择对手开始决斗!";
        }

        ItemStack icon = new ItemBuilder(material)
                .name(MessageUtil.color("&6&l" + arena.getName()))
                .lore(MessageUtil.color(statusText), "", MessageUtil.color(lore))
                .build();

        SGButton button = new SGButton(icon);
        button.withListener(event -> {
            event.setCancelled(true);
            if (!isEnabled) {
                viewer.sendMessage(MessageUtil.color("&c该竞技场尚未启用。"));
                return;
            }
            if (isInUse) {
                if (DuelManager.getInstance().isDueling(viewer)) {
                    viewer.sendMessage(MessageUtil.color("&c你正在决斗中, 无法观战!"));
                    return;
                }
                List<Duel> duels = arena.getActiveDuels();
                if (!duels.isEmpty()) {
                    Duel duel = duels.get(0);
                    duel.spectate(viewer);
                }
                viewer.closeInventory();
            } else {
                if (DuelManager.getInstance().isDueling(viewer)) {
                    viewer.sendMessage(MessageUtil.color("&c你正在决斗中, 无法发起新的决斗!"));
                    return;
                }
                new PlayerSelectGUI(viewer, arena).open();
            }
        });
        return button;
    }
}
