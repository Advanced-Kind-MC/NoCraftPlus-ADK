package net.corruptmc.nocraftplus.listeners;

import net.corruptmc.nocraftplus.NoCraftPlugin;
import net.corruptmc.nocraftplus.events.BlockedCraftingEvent;
import net.corruptmc.nocraftplus.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.InventoryView;

public class CraftListener implements Listener
{
    private NoCraftPlugin plugin;
    private boolean alert;

    public CraftListener(NoCraftPlugin plugin)
    {
        this.plugin = plugin;
        this.alert = plugin.getConfig().getBoolean("enable_alert");
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event)
    {
        //Check if the current recipe exists
        if (event.getRecipe() != null)
        {
            InventoryView view = event.getView();
            Player player = (Player) view.getPlayer();

            String type = event.getRecipe().getResult().getType().name();

            boolean allDisabled = plugin.isAllDisabled();
            boolean disabledItem = plugin.getFilters().contains(type);

            //Check whether or not the current recipe is disabled
            if (disabledItem || allDisabled)
            {
                //Check whether or not the player is allowed to use the current recipe
                if (!player.hasPermission("nocraftplus.bypass." + type.toLowerCase()) && !player.hasPermission("nocraftplus.bypass.*"))
                {
                    BlockedCraftingEvent craftEvent = new BlockedCraftingEvent(event);

                    Bukkit.getServer().getPluginManager().callEvent(craftEvent);

                    if (!craftEvent.isCancelled())
                    {
                        event.getInventory().setResult(null);

                        if (alert)
                            player.sendMessage(Lang.TITLE.toString() +
                                    Lang.CRAFTING_DISABLED.toString().replaceAll("%item%", type));
                    }
                }
            }
        }
    }
}
