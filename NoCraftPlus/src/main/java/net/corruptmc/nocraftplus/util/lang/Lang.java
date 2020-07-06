package net.corruptmc.nocraftplus.util.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang
{
    TITLE("title", "&c&lNCP »&r "),
    CURRENTLY_RUNNING("currently-running", "&aThe server is currently running &6%p"),
    NCP_HELP("ncp-help", "&c&lNCP &eHelp:"),
    RELOAD("reload", "&aSuccessfully reloaded all files."),
    SUB_COMMANDS("sub-commands", "&aAvailable sub-commands: &6/ncp %c"),
    SUB_COMMANDS_ITEM("sub-commands-item", "&6%c &a- Specify an item filter to add or remove."),
    PLAYERS_ONLY("players-only", "&cThis feature is for players only."),
    NO_PERMISSION("no-permission", "&cYou do not have permission to use this feature"),
    NO_PERMISSION_CRAFT("no-permission-craft", "&cYou do not have permission to craft: &4%m"),
    NO_MATERIAL("no-material", "&cPut an item in the inventory."),
    INCORRECT_USAGE("incorrect-usage", "&cIncorrect usage."),
    ADD_FAIL("add-fail", "&cCould not add a filter for item: &4%m"),
    DEL_FAIL("del-fail", "&cCould not delete filter for item: &4%m"),
    ADD_SUCCESS("add-success", "&aSuccessfully created a filter for item: &6%m"),
    DEL_SUCCESS("del-success", "&aSuccessfully deleted filter for item: &6%m"),
    CLICK_REMOVE("click-remove", "&7Click to remove filter"),
    DISABLED_ITEMS("disabled-items", "&c&lDisabled items:"),
    UPDATE_AVAILABLE("update-available", "&cThere is a new update available.");

    private String path;
    private String def;
    private static YamlConfiguration LANG;

    Lang(String path, String start)
    {
        this.path = path;
        this.def = start;
    }

    public static void setFile(YamlConfiguration config)
    {
        LANG = config;
    }

    @Override
    public String toString()
    {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }

    public String getDefault()
    {
        return this.def;
    }

    public String getPath()
    {
        return this.path;
    }
}
