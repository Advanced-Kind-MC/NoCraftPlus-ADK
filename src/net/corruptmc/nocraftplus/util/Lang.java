package net.corruptmc.nocraftplus.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang
{
    //Plugin info
    TITLE("title", "&3&lNCP » "),
    PLUGIN_INFO("plugin-info", "&8Currently running &cNoCraftPlus v%ver%&8."),

    CRAFTING_DISABLED("crafting-disabled", "&8You are not allowed to craft &c%item%&8."),

    GET_HELP("get-help", "&8Use &c/ncp help &8to view available commands."),
    UPDATE_READY("update-ready", "&8NoCraftPlus update available."),

    //command messages
    FILTER_ADDED("filter-added", "&8Successfully added an item filter for: &c%item%&8."),
    FILTER_REMOVED("filter-removed", "&8Successfully removed item filter for: &c%item%&8."),
    NO_FILTERS("no-filters", "&8No filters detected."),
    CURRENT_FILTERS("current-filters", "&8Current filters: &c%filters%"),
    FILTERS_RELOADED("filters-reloaded", "&8Successfully reloaded filters."),

    //Command errors
    FILTER_EXISTS("filter-exists", "&cThat item already has a filter set."),
    INVALID_FILTER("invalid-filter", "&cThat item does not have a filter."),
    INVALID_ITEM("invalid-item", "&cThat item does not exist."),

    //Unspecific errors
    NO_PERMISSION("no-permission", "&cYou do not have permission to use this feature."),
    INVALID_SUBCOMMAND("invalid-subcommand", "&cInvalid subcommand."),
    USAGE("usage", "&8Usage: &c%cmd%");

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
