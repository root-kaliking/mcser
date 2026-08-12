package me.robomonkey.versus.kit.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.kit.KitManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveKitCommand extends AbstractCommand {
    public SaveKitCommand() {
        super("savekit", "versus.kit.save");
        setPlayersOnly(true);
        setStaticTabComplete(true);
        this.setMaxArguments(1);
        this.setArgumentRequired(true);
        this.addTabCompletion("<物品包名称>");
        setUsage("/arena savekit <名称>");
        setDescription("使用指定名称保存当前玩家背包为物品包。");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            error(sender, "请提供物品包的名称。");
            return;
        }

        String kitName = args[0];
        if (KitManager.getInstance().isKit(kitName)) {
            error(sender, "物品包 '" + kitName + "' 已存在。");
            return;
        }
        KitManager.getInstance().add(kitName, player.getInventory().getContents());
        sender.sendMessage(MessageUtil.get("&p你已成功保存 &h" + kitName + "&p。使用 &h/arena set&p 绑定到竞技场。"));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
