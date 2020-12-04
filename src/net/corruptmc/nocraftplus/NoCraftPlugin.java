package net.corruptmc.nocraftplus;

import net.corruptmc.nocraftplus.command.*;
import net.corruptmc.nocraftplus.listeners.CraftListener;
import net.corruptmc.nocraftplus.listeners.UpdateListener;
import net.corruptmc.nocraftplus.util.Lang;
import net.corruptmc.nocraftplus.util.Metrics;
import net.corruptmc.nocraftplus.util.UpdateChecker;
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
    private Logger log;

    private List<String> filters;
    private boolean allDisabled;

    @Override
    public void onEnable()
    {
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
    }

    public void loadLang()
    {
        File lang = new File(getDataFolder(), "lang.yml");
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(lang);
        if (!lang.exists())
        {
            log.info("No language file detected. Creating one now");
            try
            {
                langConfig.save(lang);
            } catch (IOException e)
            {
                e.printStackTrace();
                log.info("Could not create language file.");
                log.info("Disabling plugin.");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
        int i = 0;
        for (Lang item : Lang.values())
        {
            if (langConfig.getString(item.getPath()) == null)
            {
                i++;
                langConfig.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(langConfig);
        try
        {
            langConfig.save(lang);
            if (i > 0)
                log.info("Loaded new language messages.");
        } catch (IOException e)
        {
            log.info("Could not save language file.");
            log.info("Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public List<String> getFilters()
    {
        return this.filters;
    }

    public void addFilter(String material)
    {
        FileConfiguration config = getConfig();
        List<String> temp = config.getStringList("disabled_items");
        temp.add(material);
        config.set("disabled_items", temp);
        saveConfig();
        this.filters.add(material);
    }

    public void removeFilter(String material)
    {
        FileConfiguration config = getConfig();
        List<String> temp = config.getStringList("disabled_items");
        temp.remove(material);
        config.set("disabled_items", temp);
        saveConfig();
        this.filters.remove(material);
    }

    public boolean isAllDisabled()
    {
        return this.allDisabled;
    }

    private void loadMetrics()
    {
        int pluginID = 7720;
        Metrics metrics = new Metrics(this, pluginID);

        String size;
        if (allDisabled)
            size = "all";
        else
            size = Integer.toString(filters.size());

        metrics.addCustomChart(new Metrics.SimplePie("disabled_items", new Callable<String>()
        {
            @Override
            public String call()
            {
                return size;
            }
        }));
    }
}