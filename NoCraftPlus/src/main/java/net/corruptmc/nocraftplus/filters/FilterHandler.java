package net.corruptmc.nocraftplus.filters;

import net.corruptmc.nocraftplus.NoCraftPlus;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FilterHandler
{
    private NoCraftPlus ncp = JavaPlugin.getPlugin(NoCraftPlus.class);
    private FileConfiguration config = ncp.getConfig();

    private List<String> disabled = config.getStringList("disabled_items");

    public boolean addItem(String s){
        String upper = s.toUpperCase();
        if(disabled.contains(upper)){
            return false;
        }
        try{
            Material.valueOf(upper);
        } catch (Exception e){
            return false;
        }
        disabled.add(upper);
        config.set("disabled_items", disabled);
        ncp.saveConfig();
        return true;
    }
    public boolean delItem(String s){
        if(disabled == null){
            return false;
        }
        String upper = s.toUpperCase();
        if(!disabled.contains(upper)){
            return false;
        }
        try{
            Material.valueOf(upper);
        } catch (Exception e){
            return false;
        }
        disabled.remove(upper);
        config.set("disabled_items", disabled);
        ncp.saveConfig();
        return true;
    }
    public List<String> getDisabled(){
        return disabled;
    }
}
