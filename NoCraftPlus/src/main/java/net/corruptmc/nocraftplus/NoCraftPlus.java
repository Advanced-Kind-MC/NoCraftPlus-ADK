package net.corruptmc.nocraftplus;

import net.corruptmc.nocraftplus.gui.GUIData;
import net.corruptmc.nocraftplus.listeners.ClickListener;
import net.corruptmc.nocraftplus.listeners.CraftListener;
import net.corruptmc.nocraftplus.listeners.UpdateListener;
import net.corruptmc.nocraftplus.util.UpdateChecker;
import net.corruptmc.nocraftplus.util.bstats.Metrics;
import net.corruptmc.nocraftplus.filters.FilterHandler;
import net.corruptmc.nocraftplus.util.lang.Lang;
import net.corruptmc.nocraftplus.util.lang.LangHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class NoCraftPlus extends JavaPlugin {
    private LangHandler lang;
    private GUIData data;

    public YamlConfiguration LANG;
    public File LANG_FILE;
    private Logger log;

    private File configFile = new File(this.getDataFolder(), "config.yml");

    private boolean upToDate;

    @Override
    public void onLoad() {
        log = getLogger();
    }

    @Override
    public void onEnable() {
        lang = new LangHandler();
        lang.loadLang();
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        data = new GUIData();
        data.save();
        getCommand("nocraftplus").setExecutor(new NoCraftCommand());
        getCommand("nocraftplus").setTabCompleter(new NoCraftCommand());
        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        getServer().getPluginManager().registerEvents(new CraftListener(), this);

        upToDate = true;
        updateCheck();

        loadMetrics();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadMetrics() {
        int pluginID = 7720;
        Metrics metrics = new Metrics(this, pluginID);

        FilterHandler filter = new FilterHandler();
        if (getConfig().getBoolean("disable_all")) {
            String size = "all";
            metrics.addCustomChart(new Metrics.SimplePie("disabled_items", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return size;
                }
            }));
        } else {
            String size = Integer.toString(filter.getDisabled().size());
            metrics.addCustomChart(new Metrics.SimplePie("disabled_items", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return size;
                }
            }));
        }

    }

    private void updateCheck() {
        UpdateChecker.updateConfig();
        if (!getConfig().getBoolean("check_for_updates"))
            return;
        new UpdateChecker(this, 79378).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                log.info(getDescription().getName() + " is up to date!");
            } else {
                String title = String.format("[%s] ", getDescription().getName());
                Bukkit.getConsoleSender().sendMessage(title + Lang.UPDATE_AVAILABLE.toString());
                upToDate = false;
                getServer().getPluginManager().registerEvents(new UpdateListener(), this);
            }
        });
    }

    public YamlConfiguration getLang() {
        return LANG;
    }

    public File getLangFile() {
        return LANG_FILE;
    }

    public boolean isUpToDate() {
        return upToDate;
    }

}
