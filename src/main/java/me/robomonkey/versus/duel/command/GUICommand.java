package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.gui.DuelGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GUICommand extends AbstractCommand {

    public GUICommand() {
        super("gui", "versus.duel");
        setUsage("/duel gui");
        setPlayersOnly(true);
        setPermissionRequired(false);
        setArgumentRequired(false);
        setDescription("打开竞技场选择界面。");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        new DuelGUI(player).open();
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
