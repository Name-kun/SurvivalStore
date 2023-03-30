package net.nametarou.survivalstore.saisen;

import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.nametarou.survivalstore.saisen.SaisenListener.getSaisen;

public class SaisenCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("saisen")) {
            if (!sender.hasPermission("survivalstore.saisen")) {
                sender.sendMessage("§c権限がありません。");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage("§c/saisen give <player> <dollar> <amount>");
                sender.sendMessage("§c/saisen removehologram <name>");
            } else {
                if (args[0].equalsIgnoreCase("give")) {
                    if (args.length == 1) {
                        sender.sendMessage("§cお賽銭を与えるプレイヤーを指定してください。");
                        return true;
                    }
                    if (args.length == 2) {
                        sender.sendMessage("§c何ドル分のお賽銭かを数字で指定してください。");
                        return true;
                    }
                    if (Bukkit.getPlayerExact(args[1]) == null) {
                        sender.sendMessage("そのプレイヤーはオフラインまたは存在しません。");
                        return true;
                    }
                    try {
                        Player target = Bukkit.getPlayerExact(args[1]);
                        double dollar = Double.parseDouble(args[2]);
                        int amount = Integer.parseInt(args[3]);
                        target.getInventory().addItem(getSaisen(dollar, amount));
                        sender.sendMessage("§aお賽銭を指定プレイヤーに付与しました。");
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§c正しい数字を入力してください。");
                    }
                } else if (args[0].equalsIgnoreCase("removehologram")) {
                    if (args.length == 1) {
                        sender.sendMessage("§c削除したいホログラム名を指定してください。");
                        return true;
                    }
                    if (DHAPI.getHologram(args[1]) != null) {
                        DHAPI.removeHologram(args[1]);
                        sender.sendMessage("§aホログラムを正常に削除しました。");
                    } else sender.sendMessage("§cそのようなホログラムは登録されいません。");
                } else {
                    sender.sendMessage("§c/saisen give <player> <dollar> <amount>");
                    sender.sendMessage("§c/saisen removehologram <name>");
                }
            }

        }
        return true;
    }
}
