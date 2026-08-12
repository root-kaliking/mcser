package me.robomonkey.versus.duel.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.arena.Arena;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.request.RequestManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectGUI {
    private final Player viewer;
    private final Arena selectedArena;

    public PlayerSelectGUI(Player viewer, Arena arena) {
        this.viewer = viewer;
        this.selectedArena = arena;
    }

    public void open() {
        List<Player> onlinePlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.equals(viewer) && !DuelManager.getInstance().isDueling(p)
                    && !RequestManager.getInstance().isQueued(p)) {
                onlinePlayers.add(p);
            }
        }

        int size = Math.max(18, ((onlinePlayers.size() / 9) + 1) * 9);
        if (size > 54) size = 54;

        SGMenu menu = Versus.spiGUI.create("选择对手 - " + selectedArena.getName(), size / 9);

        for (int i = 0; i < onlinePlayers.size() && i < size; i++) {
            Player target = onlinePlayers.get(i);
            menu.setButton(i, getPlayerButton(target));
        }

        ItemStack backIcon = new ItemBuilder(Material.ARROW)
                .name(MessageUtil.color("&c&l返回"))
                .lore(MessageUtil.color("&7返回竞技场列表"))
                .build();
        SGButton backButton = new SGButton(backIcon);
        backButton.withListener(event -> {
            event.setCancelled(true);
            new DuelGUI(viewer).open();
        });
        menu.setButton(size - 1, backButton);

        viewer.openInventory(menu.getInventory());
    }

    private SGButton getPlayerButton(Player target) {
        ItemStack playerHead = new ItemBuilder(Material.PLAYER_HEAD)
                .name(MessageUtil.color("&e&l" + target.getName()))
                .lore(MessageUtil.color("&7点击向该玩家发起决斗挑战!"),
                        MessageUtil.color("&7竞技场: &6" + selectedArena.getName()))
                .build();

        SGButton button = new SGButton(playerHead);
        button.withListener(event -> {
            event.setCancelled(true);
            viewer.closeInventory();

            if (DuelManager.getInstance().isDueling(viewer)) {
                viewer.sendMessage(MessageUtil.color("&c你正在决斗中, 无法发起新的决斗!"));
                return;
            }
            if (DuelManager.getInstance().isDueling(target) || RequestManager.getInstance().isQueued(target)) {
                viewer.sendMessage(MessageUtil.color("&c" + target.getName() + " 当前无法决斗。"));
                return;
            }
            if (RequestManager.getInstance().isQueued(viewer)) {
                viewer.sendMessage(MessageUtil.color("&c你已在决斗队列中, 输入 /duel cancel 退出队列。"));
                return;
            }

            RequestManager.getInstance().sendRequest(viewer, target, selectedArena);
        });
        return button;
    }
}
