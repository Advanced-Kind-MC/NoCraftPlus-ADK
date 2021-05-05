package net.corruptmc.nocraftplus.command;

import net.corruptmc.nocraftplus.NoCraftPlugin;
import net.corruptmc.nocraftplus.util.Lang;
import org.bukkit.command.CommandSender;

public class CmdReload implements CommandInterface
{
    private NoCraftPlugin plugin;

    public CmdReload(NoCraftPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        String title = Lang.TITLE.toString();
        if (args.length != 1)
        {
            sender.sendMessage(title + Lang.USAGE.toString().replaceAll("%cmd%", "/ncp reload"));
        } else
        {
            plugin.reloadConfig();
            plugin.loadFilters();
            plugin.loadLang();
            title = Lang.TITLE.toString();

            sender.sendMessage(title + Lang.FILTERS_RELOADED.toString());
        }
        return true;
    }

}
