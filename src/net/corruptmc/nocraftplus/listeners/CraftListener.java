package net.corruptmc.nocraftplus.listeners;

import net.corruptmc.nocraftplus.NoCraftPlugin;
import net.corruptmc.nocraftplus.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.InventoryView;

import java.util.List;

public class CraftListener implements Listener
{
    private NoCraftPlugin plugin;
    private String title;

    public CraftListener(NoCraftPlugin plugin)
    {
        this.plugin = plugin;
        this.title = Lang.TITLE.toString();
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event)
    {
        if (event.getRecipe() != null)
        {
            InventoryView view = event.getView();
            Player player = (Player) view.getPlayer();

            if (!player.hasPermission("nocraftplus.bypass.*"))
            {
                boolean allDisabled = plugin.isAllDisabled();
                String type = event.getRecipe().getResult().getType().name();
                boolean disabledItem = plugin.getFilters().contains(type);
                if (allDisabled)
                {
                    if (!player.hasPermission("nocraftplus.bypass." + type.toLowerCase()))
                    {
                        event.getInventory().setResult(null);
                        player.sendMessage(title + Lang.CRAFTING_DISABLED.toString().replaceAll("%item%", type));
                    }
                } else if (disabledItem)
                {
                    List<String> filters = plugin.getFilters();
                    for (String s : filters)
                    {
                        if (s.equals(type))
                        {
                            if (!player.hasPermission("nocraftplus.bypass." + type.toLowerCase()))
                            {
                                event.getInventory().setResult(null);
                                player.sendMessage(title + Lang.CRAFTING_DISABLED.toString().replaceAll("%item%", type));
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
