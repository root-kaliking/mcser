package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.RootCommand;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.request.RequestManager;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class RootDuelCommand extends RootCommand {

    public RootDuelCommand() {
        super("duel", "versus.duel");
        setPermissionRequired(Settings.is(Setting.PERMISSION_REQUIRED_TO_DUEL));
        setPlayersOnly(true);
        setArgumentRequired(true);
        setUsage("/duel <player>");
        setDescription("向其他玩家发送决斗请求。");
        addBranches(new DenyCommand(),
                new CancelCommand(),
                new AcceptCommand(),
                new GUICommand());
        setAutonomous(true);
        enforcePermissionRulesForChildren();
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        RequestManager requestManager = RequestManager.getInstance();
        Player player = (Player) sender;
        String playerNameRequested = args[0];
        Player requested = Bukkit.getPlayer(playerNameRequested);
        if (requested == null) {
            error(sender, "'" + playerNameRequested + "' 不在线。");
            return;
        }
        if (requested.equals(player)) {
            error(sender, "你不能和自己决斗。");
            return;
        }
        if (DuelManager.getInstance().isDueling(player)) {
            error(sender, "你现在无法决斗。");
            return;
        }
        if (DuelManager.getInstance().isDueling(requested) || requestManager.isQueued(requested)) {
            error(sender, requested.getName() + " 现在无法决斗。");
            return;
        }
        if (requestManager.hasIncomingRequest(player)
                && requestManager.isRequestedBy(requested, player)) {
            try {
                RequestManager.getInstance().acceptSpecificRequest(player, requested);
            } catch (RequestManager.PlayerOfflineException e) {
                error(player, "发起决斗请求的玩家已下线!");
            }
            return;
        }
        if (requestManager.isQueued(player)) {
            error(sender, "你在决斗队列中无法发送决斗请求。输入 /duel cancel 退出队列。");
            return;
        }
        if (requestManager.isRequestedBy(player, requested)) {
            error(sender, "请等待 " + requested.getName() + " 回复你的第一个请求。");
            return;
        }
        requestManager.sendRequest(player, requested);
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> player.getName())
                .collect(Collectors.toList());
    }
}
