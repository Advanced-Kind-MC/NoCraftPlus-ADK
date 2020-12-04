package net.corruptmc.nocraftplus.command;

import net.corruptmc.nocraftplus.util.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CmdBase implements CommandInterface
{
    private String version;
    private String title;

    public CmdBase(JavaPlugin plugin)
    {
        this.version = plugin.getDescription().getVersion();
        this.title = Lang.TITLE.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        String message = Lang.PLUGIN_INFO.toString().replaceAll("%ver%", this.version);
        sender.sendMessage(title + message);
        message = Lang.GET_HELP.toString();
        sender.sendMessage(title + message);
        return true;
    }
}
