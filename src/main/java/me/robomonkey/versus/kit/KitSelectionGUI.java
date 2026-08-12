package me.robomonkey.versus.kit;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.buttons.SGButtonListener;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import com.samjakob.spigui.toolbar.SGToolbarButtonType;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class KitSelectionGUI {

    private KitManager kitManager = KitManager.getInstance();
    private Player viewer;
    private Kit selectedKit;
    private SGMenu mainMenu;
    private static SGButton EMPTY = new SGButton(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name(" ").build());

    public KitSelectionGUI(Player viewer, BiConsumer<Kit, Player> onSelect) {
        this.viewer = viewer;
        mainMenu = Versus.spiGUI.create("物品包 (第{currentPage}页/共{maxPage}页)", 2);
        mainMenu.setAutomaticPaginationEnabled(true);
        mainMenu.setToolbarBuilder((slot, page, type, sgMenu) -> {
            if (type == SGToolbarButtonType.CURRENT_BUTTON) {
                ItemStack confirmIcon = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).amount(1).name("&a&l确认").lore("&7确认你选择的物品包。").build();
                SGButton confirmButton = new SGButton(confirmIcon).withListener(listener -> {
                    if (selectedKit == null) selectedKit = KitManager.getInstance().getDefaultKit();
                    onSelect.accept(selectedKit, viewer);
                    viewer.closeInventory();
                });
                return confirmButton;
            } else if (type == SGToolbarButtonType.UNASSIGNED || sgMenu.getMaxPage() == 1) {
                return EMPTY;
            } else {
                return Versus.spiGUI.getDefaultToolbarBuilder().buildToolbarButton(slot, page, type, sgMenu);
            }
        });
        kitManager.verifyDefaultKit();
        selectedKit = kitManager.getDefaultKit();
        loadKits();
    }

    public void open() {
        viewer.openInventory(mainMenu.getInventory());
    }

    private void toggleSelection(Kit kit) {
        if (kit.equals(selectedKit)) {
            selectedKit = null;
        } else {
            selectedKit = kit;
        }
    }

    public void loadKits() {
        mainMenu.clearAllButStickiedSlots();
        kitManager.getAllKits().stream()
                .forEach(kit -> {
                    boolean selected = kit.equals(selectedKit);
                    mainMenu.addButton(getKitButton(kit, selected));
                });
    }

    private SGButton getKitButton(Kit kit, boolean selected) {
        SGButtonListener listener = (inventoryClickEvent) -> {
            if (inventoryClickEvent.getClick().isLeftClick()) {
                toggleSelection(kit);
                loadKits();
                mainMenu.refreshInventory(viewer);
            } else if (inventoryClickEvent.getClick().isRightClick()) {
                openViewingGUI(kit);
            }
        };
        ItemStack displayItem = kit.getDisplayItem();
        ItemStack kitIcon = new ItemBuilder(displayItem.getType())
                .name(MessageUtil.color("&p" + kit.getName()))
                .lore(selected ? "&a&l已选择" : "&7未选择",
                        "",
                        MessageUtil.color("&p右键点击&s查看。"),
                        MessageUtil.color("&p左键点击&s选择 &p" + kit.getName() + "&s!"))
                .build();
        SGButton kitButton = new SGButton(kitIcon);
        kitButton.withListener(listener);
        return kitButton;
    }

    public void openViewingGUI(Kit kit) {
        SGMenu viewingGUI = Versus.spiGUI.create("查看 " + kit.getName(), 6);
        IntStream.range(45, 54).forEach(index -> {
            viewingGUI.setButton(index, EMPTY);
        });
        ItemStack exitIcon = new ItemBuilder(Material.BARRIER).amount(1)
                .name("&c&l退出")
                .lore("&7返回查看所有物品包。").build();
        SGButton exitButton = new SGButton(exitIcon).withListener(inventoryClickEvent -> this.open());
        viewingGUI.setButton(49, exitButton);
        for (int index = 0; index < kit.getItems().length; index++) {
            SGButton itemButton = new SGButton(kit.getItems()[index]);
            viewingGUI.setButton(index, itemButton);
        }
        viewer.openInventory(viewingGUI.getInventory());
    }
}
