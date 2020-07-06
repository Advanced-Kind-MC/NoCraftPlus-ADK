package net.corruptmc.nocraftplus.util.lang;

import net.corruptmc.nocraftplus.NoCraftPlus;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LangHandler
{

    private NoCraftPlus ncp = JavaPlugin.getPlugin(NoCraftPlus.class);

    private Logger log = ncp.getLogger();
    private PluginDescriptionFile pdf = ncp.getDescription();

    File lang = new File(ncp.getDataFolder() + File.separator + "lang" + File.separator, "en.yml");
    public void loadLang()
    {
        if (!lang.exists())
        {
            try
            {
                lang.mkdir();
                InputStreamReader defConfigStream = new InputStreamReader(ncp.getResource("en.yml"));
                if (defConfigStream != null)
                {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    defConfig.save(lang);
                    Lang.setFile(defConfig);
                }
            } catch (IOException e)
            {
                log.severe("Couldn't create language file.");
                log.severe("This is a fatal error. Now disabling " + pdf.getName());
                ncp.getServer().getPluginManager().disablePlugin(ncp);
                e.printStackTrace();
            }
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for (Lang item : Lang.values())
        {
            if (conf.getString(item.getPath()) == null)
            {
                conf.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(conf);
        ncp.LANG = conf;
        ncp.LANG_FILE = lang;
        try
        {
            conf.save(ncp.getLangFile());
        } catch (IOException e)
        {
            log.log(Level.WARNING, pdf.getName() + ": Failed to save lang file.");
        }
    }
}
