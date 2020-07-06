package net.corruptmc.nocraftplus.listeners;

import net.corruptmc.nocraftplus.NoCraftPlus;
import net.corruptmc.nocraftplus.util.lang.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateListener implements Listener {

    NoCraftPlus ncp = JavaPlugin.getPlugin(NoCraftPlus.class);
    boolean upToDate = ncp.isUpToDate();
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(p.hasPermission("nocraftplus.updatecheck")){
            if(!upToDate){
                p.sendMessage(Lang.TITLE.toString() + Lang.UPDATE_AVAILABLE.toString());
            }
        }
    }
}
