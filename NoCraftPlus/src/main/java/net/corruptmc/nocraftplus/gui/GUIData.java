package net.corruptmc.nocraftplus.gui;

import net.corruptmc.nocraftplus.NoCraftPlus;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class GUIData
{
    private NoCraftPlus ncp = JavaPlugin.getPlugin(NoCraftPlus.class);

    File guiFile = new File(ncp.getDataFolder(), "GUIData.yml");
    FileConfiguration guiConfig = YamlConfiguration.loadConfiguration(guiFile);

    public boolean save(){
        if(!guiFile.exists()){
            ncp.saveResource("GUIData.yml", true);
            reload();
            return true;
        }
        try
        {
            guiConfig.save(guiFile);
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public FileConfiguration getConfig(){
        return guiConfig;
    }
    public void reload(){
        try
        {
            guiConfig.load(guiFile);
        } catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}
