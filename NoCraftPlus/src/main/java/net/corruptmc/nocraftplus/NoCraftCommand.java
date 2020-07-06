package net.corruptmc.nocraftplus;

import net.corruptmc.nocraftplus.gui.GUIData;
import net.corruptmc.nocraftplus.gui.GUIHandler;
import net.corruptmc.nocraftplus.filters.FilterHandler;
import net.corruptmc.nocraftplus.util.lang.Lang;
import net.corruptmc.nocraftplus.util.lang.LangHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoCraftCommand implements TabExecutor {

    private NoCraftPlus ncp = JavaPlugin.getPlugin(NoCraftPlus.class);
    private FilterHandler filter = new FilterHandler();
    private GUIHandler gui = GUIHandler.getInstance();

    private String title = Lang.TITLE.toString();
    private GUIData data = new GUIData();
    private LangHandler lang = new LangHandler();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            PluginDescriptionFile pdf = ncp.getDescription();
            sender.sendMessage(title + Lang.CURRENTLY_RUNNING.toString().replaceAll("%p", pdf.getName() + " v" + pdf.getVersion()));
            return true;
        }
        if (!sender.hasPermission("nocraftplus.edit")) {
            sender.sendMessage(title + Lang.NO_PERMISSION.toString());
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("gui")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(title + Lang.PLAYERS_ONLY.toString());
                    return true;
                }
                Player p = (Player) sender;
                Inventory inv = gui.getGUI("menu");
                p.openInventory(inv);
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(title + Lang.SUB_COMMANDS.toString().replaceAll("%c", "list|gui|add|remove <item>"));
                sender.sendMessage(Lang.SUB_COMMANDS_ITEM.toString().replaceAll("%c", "<item>"));
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                ncp.reloadConfig();
                data.reload();
                lang.loadLang();
                gui.loadGUI();
                sender.sendMessage(title + Lang.RELOAD.toString());
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(title + Lang.DISABLED_ITEMS.toString());
                for (String s : filter.getDisabled()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8" + s));
                }
                return true;
            } else {
                sender.sendMessage(title + Lang.INCORRECT_USAGE.toString());
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (filter.addItem(args[1])) {
                    sender.sendMessage(title + Lang.ADD_SUCCESS.toString().replaceAll("%m", args[1]));
                } else {
                    sender.sendMessage(title + Lang.ADD_FAIL.toString().replaceAll("%m", args[1]));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (filter.delItem(args[1])) {
                    sender.sendMessage(title + Lang.DEL_SUCCESS.toString().replaceAll("%m", args[1]));
                } else {
                    sender.sendMessage(title + Lang.DEL_FAIL.toString().replaceAll("%m", args[1]));
                }
                return true;
            } else {
                sender.sendMessage(title + Lang.INCORRECT_USAGE.toString());
                return true;
            }
        } else {
            sender.sendMessage(title + Lang.INCORRECT_USAGE.toString());
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            if (!sender.hasPermission("nocraftplus.edit"))
                return null;
            list.add("add");
            list.add("gui");
            list.add("help");
            list.add("list");
            list.add("reload");
            list.add("remove");
            if (args[0].equals(""))
                return list;
            List<String> newList = new ArrayList<>();
            String input = args[0].toLowerCase();
            for (String s : list) {
                if (s.startsWith(input)) {
                    newList.add(s);
                }
            }
            int size = newList.size();
            if (size == 0)
                return list;
            else
                return newList;
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            if (args[0].equalsIgnoreCase("add")) {
                list.add("<item>");
                return list;
            } else if (args[0].equalsIgnoreCase("remove")) {
                list = filter.getDisabled();
                Collections.sort(list);
                if (args[1].equals(""))
                    return list;
                List<String> newList = new ArrayList<>();
                input = input.toUpperCase();
                for (String s : list) {
                    if (s.startsWith(input)) {
                        newList.add(s);
                    }
                }
                int size = newList.size();
                if (size == 0)
                    return list;
                else
                    return newList;
            }
        }
        return null;
    }

}
