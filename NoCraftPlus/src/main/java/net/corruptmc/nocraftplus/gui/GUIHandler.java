package net.corruptmc.nocraftplus.gui;

import net.corruptmc.nocraftplus.NoCraftPlus;
import net.corruptmc.nocraftplus.filters.FilterHandler;
import net.corruptmc.nocraftplus.util.lang.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public class GUIHandler {
    private static GUIHandler instance = new GUIHandler();

    private boolean guiLoaded = false;

    private NoCraftPlus ncp = JavaPlugin.getPlugin(NoCraftPlus.class);
    private Logger log = ncp.getLogger();
    private GUIData data = new GUIData();
    private FilterHandler filter = new FilterHandler();
    private FileConfiguration guiConfig = data.getConfig();

    private Map<String, ItemStack> btns = new HashMap<>();
    private Map<String, Inventory> inventories = new HashMap<>();
    private Map<String ,String> inventoryNames = new HashMap<>();

    /*
    TODO: make pages for large amounts of blocked items.
     */

    public Map<String ,String> getNames() {
        return inventoryNames;
    }

    public boolean isGuiLoaded() {
        return guiLoaded;
    }

    private void startTimer() {
        ncp.getServer().getScheduler().scheduleSyncDelayedTask(ncp, new Runnable() {
            @Override
            public void run() {
                btns = null;
                inventories = null;
                inventoryNames = null;
                log.info("Unloaded GUIs to preserve resources.");
            }
        }, 6000L);
    }

    public Inventory getGUI(String gui) {
        if (!guiLoaded) {
            log.info("Loading GUIs...");
            try {
                loadGUI();
            } catch (Exception e) {
                log.info("Error loading GUIs.");
                e.printStackTrace();
                return null;
            }
            startTimer();
            log.info("GUIs loaded.");
        }
        Inventory inv = inventories.get(gui);
        return inv;
    }

    public void loadGUI() {
        loadBtns();
        loadInventories();
        loadNames();
    }

    private void loadBtns() {
        Set<String> keys = guiConfig.getConfigurationSection("buttons").getKeys(false);
        for (String s : keys) {
            String type = guiConfig.getString("buttons." + s + ".item");
            Material mat = Material.matchMaterial(type);
            ItemStack btn = new ItemStack(mat);
            ItemMeta btnMeta = btn.getItemMeta();
            String name = ChatColor.translateAlternateColorCodes('&', guiConfig.getString("buttons." + s + ".name"));
            btnMeta.setDisplayName(name);
            btn.setItemMeta(btnMeta);
            btns.put(s, btn);
        }
    }

    public void loadInventories() {
        Set<String> keys = guiConfig.getConfigurationSection("gui").getKeys(false);
        for (String s : keys) {
            Inventory inv = getInventory(s);
            inventories.put(s, inv);
        }
    }

    private Inventory getInventory(String gui) {
        Inventory inv;
        String name = ChatColor.translateAlternateColorCodes('&', guiConfig.getString("gui." + gui + ".title"));
        switch (gui) {
            case "menu":
            case "add":
                inv = ncp.getServer().createInventory(null, 27, name);
                break;
            case "remove":
                int size = filter.getDisabled().size();
                double dub = (double) size / 9;
                size = (int) Math.ceil(dub);
                size = size + 1;
                size = size * 9;
                if (size > 54)
                    inv = ncp.getServer().createInventory(null, 54, name);
                else
                    inv = ncp.getServer().createInventory(null, size, name);
                break;
            default:
                throw new IllegalStateException("Invalid GUI: " + gui);
        }
        inv = setItems(inv, gui);
        return inv;
    }

    private Inventory setItems(Inventory inv, String gui) {
        inv.clear();
        ItemStack filler = btns.get("filler");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }
        switch (gui) {
            case "menu":
                ItemStack add = btns.get("add");
                ItemStack del = btns.get("remove");
                ItemStack close = btns.get("close");
                inv.setItem(12, add);
                inv.setItem(14, del);
                inv.setItem(26, close);
                break;
            case "add":
                ItemStack back = btns.get("back");
                ItemStack confirm = btns.get("confirm");
                ItemStack air = new ItemStack(Material.AIR);
                inv.setItem(18, back);
                inv.setItem(26, confirm);
                inv.setItem(13, air);
                break;
            case "remove":
                List<String> disabled = filter.getDisabled();
                if (disabled != null && disabled.size() > 0) {
                    for (int i = 0; i < disabled.size(); i++) {
                        Material mat = Material.matchMaterial(disabled.get(i));
                        ItemStack stack = new ItemStack(mat);
                        ItemMeta meta = stack.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        lore.add(Lang.CLICK_REMOVE.toString());
                        meta.setLore(lore);
                        stack.setItemMeta(meta);
                        inv.setItem(i, stack);
                    }
                }
                back = btns.get("back");
                int backSpot = inv.getSize() - 9;
                inv.setItem(backSpot, back);
                break;
        }
        return inv;
    }

    private void loadNames() {
        Set<String> keys = guiConfig.getConfigurationSection("gui").getKeys(false);
        for (String s : keys) {
            String name = guiConfig.getString("gui." + s + ".title");
            name = ChatColor.translateAlternateColorCodes('&', name);
            name = ChatColor.stripColor(name);
            inventoryNames.put(s ,name);
        }
    }

    public Map<String, ItemStack> getBtns() {
        return btns;
    }

    public static GUIHandler getInstance() {
        return instance;
    }
}
