package net.corruptmc.nocraftplus.listeners;

import net.corruptmc.nocraftplus.gui.GUIData;
import net.corruptmc.nocraftplus.gui.GUIHandler;
import net.corruptmc.nocraftplus.filters.FilterHandler;
import net.corruptmc.nocraftplus.util.lang.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ClickListener implements Listener {
    private GUIData data = new GUIData();
    private GUIHandler guiHandler = GUIHandler.getInstance();
    private FileConfiguration gui = data.getConfig();
    private FilterHandler filter = new FilterHandler();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        boolean enabled = guiHandler.isGuiLoaded();
        if (!enabled)
            return;

        InventoryView view = e.getView();
        Player p = (Player) view.getPlayer();
        if (!p.hasPermission("nocraftplus.edit"))
            return;
        String invTitle = view.getTitle();
        invTitle = ChatColor.stripColor(invTitle);
        Map<String, String> names = guiHandler.getNames();
        if (!names.values().contains(invTitle))
            return;
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta())
            return;
        for (String guiName : names.keySet()) {
            String s = names.get(guiName);
            if (invTitle.replaceAll(" ", "").equalsIgnoreCase(s.replaceAll(" ", ""))) {
                Map<String, ItemStack> btns = guiHandler.getBtns();
                switch (guiName) {
                    case "menu":
                        ItemStack add = btns.get("add");
                        if (clickedItem.equals(add)) {
                            Inventory inv = guiHandler.getGUI("add");
                            p.closeInventory();
                            p.openInventory(inv);
                            e.setCancelled(true);
                            break;
                        }
                        ItemStack remove = btns.get("remove");
                        if (clickedItem.equals(remove)) {
                            guiHandler.loadInventories();
                            Inventory inv = guiHandler.getGUI("remove");
                            p.openInventory(inv);
                            e.setCancelled(true);
                            break;
                        }
                        ItemStack close = btns.get("close");
                        if (clickedItem.equals(close))
                            p.closeInventory();
                        break;
                    case "add":
                        ItemStack confirm = btns.get("confirm");
                        if (clickedItem.equals(confirm)) {
                            Inventory inv = guiHandler.getGUI("add");
                            if (inv.getItem(13) == null || inv.getItem(13).equals(Material.AIR)) {
                                p.sendMessage(Lang.TITLE.toString() + Lang.NO_MATERIAL.toString());
                                e.setCancelled(true);
                                break;
                            }
                            String item = view.getItem(13).getType().toString();
                            if (!filter.addItem(item)) {
                                p.sendMessage(Lang.TITLE.toString() + Lang.ADD_FAIL.toString().replaceAll("%m", item));
                            } else {
                                p.sendMessage(Lang.TITLE.toString() + Lang.ADD_SUCCESS.toString().replaceAll("%m", item));
                                view.setItem(13, new ItemStack(Material.AIR));
                            }
                            e.setCancelled(true);
                            break;
                        }
                        ItemStack back = btns.get("back");
                        if (clickedItem.equals(back)) {
                            Inventory inv = guiHandler.getGUI("menu");
                            p.openInventory(inv);
                            break;
                        }
                        break;
                    case "remove":
                        back = btns.get("back");
                        if (clickedItem.equals(back)) {
                            Inventory inv = guiHandler.getGUI("menu");
                            p.openInventory(inv);
                        }
                        ItemStack filler = btns.get("filler");
                        if (!clickedItem.equals(filler)) {
                            String name = clickedItem.getType().toString();
                            filter.delItem(name);
                            p.sendMessage(Lang.TITLE.toString() + Lang.DEL_SUCCESS.toString().replaceAll("%m", name));
                            int slot = e.getSlot();
                            view.setItem(slot, filler);
                        }
                        e.setCancelled(true);
                        break;
                }
                continue;
            }
        }

    }
}
