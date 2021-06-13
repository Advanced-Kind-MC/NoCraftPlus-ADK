package net.corruptmc.nocraftplus;

import net.corruptmc.nocraftplus.command.*;
import net.corruptmc.nocraftplus.listeners.CraftListener;
import net.corruptmc.nocraftplus.listeners.UpdateListener;
import net.corruptmc.nocraftplus.util.Lang;
import net.corruptmc.nocraftplus.util.Metrics;
import net.corruptmc.nocraftplus.util.UpdateChecker;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class NoCraftPlugin extends JavaPlugin
{
    //todo blacklist/whitelist mode

    private Logger log;

    private List<String> filters;
    private boolean allDisabled;

    private static NoCraftPlugin plugin;

    @Override
    public void onEnable()
    {
        this.plugin = this;

        this.log = getLogger();

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())
        {
            log.info("No config file detected. Creating one now.");
            saveDefaultConfig();
        }

        loadLang();

        checkForUpdates();

        loadFilters();
        registerCommands();
        registerListeners();

        if (getConfig().getBoolean("enable_metrics"))
            loadMetrics();
    }

    @Override
    public void onDisable()
    {
        plugin = null;
    }

    public void checkForUpdates()
    {
        new UpdateChecker(this, 79378, this).getVersion(version ->
        {
            if (this.getDescription().getVersion().equalsIgnoreCase(version))
            {
                log.info("Up to date!");
            } else
            {
                log.info("Update available.");

                if (getConfig().getBoolean("check_for_updates"))
                    getServer().getPluginManager().registerEvents(new UpdateListener(), this);
            }
        });
    }

    public void loadFilters()
    {
        FileConfiguration config = getConfig();
        filters = config.getStringList("disabled_items");
        allDisabled = config.getBoolean("disable_all");
    }

    public void registerListeners()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CraftListener(this), this);
    }

    public void registerCommands()
    {
        CommandHandler handler = new CommandHandler();

        handler.register("nocraftplus", new CmdBase(this));

        handler.register("add", new CmdAdd(this));
        handler.register("help", new CmdHelp());
        handler.register("list", new CmdList(this));
        handler.register("reload", new CmdReload(this));
        handler.register("remove", new CmdRemove(this));

        getCommand("nocraftplus").setExecutor(handler);
        getCommand("nocraftplus").setTabCompleter(new NCPTabCompleter());
    }

    public void loadLang()
    {
        File lang = new File(getDataFolder(), "lang.yml");

        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(lang);
        for (Lang item : Lang.values())
        {
            if (langConfig.getString(item.getPath()) == null)
            {
                langConfig.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(langConfig);
        try
        {
            langConfig.save(lang);
        } catch (IOException e)
        {
            log.info("Could not save language file.");
            log.info("Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadMetrics()
    {
        int pluginID = 7720;
        Metrics metrics = new Metrics(this, pluginID);

        String size;
        if (allDisabled)
            size = "all";
        else
            size = String.valueOf(filters.size());

        metrics.addCustomChart(new Metrics.SimplePie("disabled_items", new Callable<String>()
        {
            @Override
            public String call()
            {
                return size;
            }
        }));
    }

    //Get all current crafting list
    public List<String> getFilters()
    {
        return this.filters;
    }

    //Block an item from crafting
    public void addFilter(Material material)
    {
        FileConfiguration config = getConfig();
        List<String> temp = config.getStringList("disabled_items");
        temp.add(material.toString());
        config.set("disabled_items", temp);
        saveConfig();
        this.filters.add(material.toString());
    }

    //Remove a blocked item
    public void removeFilter(Material material)
    {
        FileConfiguration config = getConfig();
        List<String> temp = config.getStringList("disabled_items");
        temp.remove(material.toString());
        config.set("disabled_items", temp);
        saveConfig();
        this.filters.remove(material.toString());
    }

    //Check if all crafting is blocked
    public boolean isAllDisabled()
    {
        return this.allDisabled;
    }

    //Check if a material is blocked
    public boolean isBlocked(Material type)
    {
        return this.filters.contains(type.toString());
    }

    //for API
    public static NoCraftPlugin getNoCraftPlusPlugin()
    {
        return plugin;
    }
}