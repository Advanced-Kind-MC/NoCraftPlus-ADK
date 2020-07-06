package net.corruptmc.nocraftplus.listeners;

import net.corruptmc.nocraftplus.NoCraftPlus;
import net.corruptmc.nocraftplus.filters.FilterHandler;
import net.corruptmc.nocraftplus.util.lang.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CraftListener implements Listener
{
    private NoCraftPlus ncp = JavaPlugin.getPlugin(NoCraftPlus.class);
    private FilterHandler filter = new FilterHandler();

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e)
    {
        Recipe recipe = e.getRecipe();
        if (recipe == null)
            return;
        InventoryView view = e.getView();
        Player p = (Player) view.getPlayer();
        if (p.hasPermission("nocraft.bypass.*"))
            return;
        String output = recipe.getResult().getType().toString();
        String name = output.toLowerCase();
        if(ncp.getConfig().getBoolean("disable_all")){
            if(p.hasPermission("nocraft.bypass." + name))
                return;
            e.getInventory().setResult(null);
            e.getView().getPlayer().sendMessage(Lang.TITLE.toString() + Lang.NO_PERMISSION_CRAFT.toString().replaceAll("%m", output));
            return;
        }
        List<String> disabled = filter.getDisabled();
        if (disabled == null || disabled.size() == 0)
            return;
        if(!disabled.contains(output))
            return;
        for(String s : disabled){
            if(!name.equalsIgnoreCase(s))
                return;
            if(p.hasPermission("nocraft.bypass." + name))
                return;
            e.getInventory().setResult(null);
            e.getView().getPlayer().sendMessage(Lang.TITLE.toString() + Lang.NO_PERMISSION_CRAFT.toString().replaceAll("%m", output));
            return;
        }
    }
}
