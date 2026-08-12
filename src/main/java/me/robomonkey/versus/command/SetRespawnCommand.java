package me.robomonkey.versus.command;

import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetRespawnCommand extends AbstractCommand {

    public SetRespawnCommand() {
        super("setrespawn", "versus.admin");
        setUsage("/versus setrespawn");
        setPlayersOnly(true);
        setPermissionRequired(true);
        setArgumentRequired(false);
        setDescription("设置决斗失败后的复活大厅位置。");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        org.bukkit.Location loc = player.getLocation();
        Settings.getInstance().set("dueling.mechanics.return_losers", "custom");
        Settings.getInstance().set("dueling.mechanics.loser_return_location.x", loc.getX());
        Settings.getInstance().set("dueling.mechanics.loser_return_location.y", loc.getY());
        Settings.getInstance().set("dueling.mechanics.loser_return_location.z", loc.getZ());
        Settings.getInstance().set("dueling.mechanics.loser_return_location.world", loc.getWorld().getName());
        Settings.getInstance().save();
        player.sendMessage(MessageUtil.color("&a已设置复活大厅位置为当前位置!"));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
