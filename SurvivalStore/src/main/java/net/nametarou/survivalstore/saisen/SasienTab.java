package net.nametarou.survivalstore.saisen;

import net.nametarou.survivalstore.SurvivalStore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SasienTab implements TabCompleter {

    SurvivalStore plugin = SurvivalStore.getPlugin(SurvivalStore.class);

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("saisen")) {
            if (args.length == 1) {
                if (sender.hasPermission("survivalstore.saisen")) {
                    tab.add("give");
                    tab.add("removehologram");
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("give"))
                    Bukkit.getOnlinePlayers().forEach(p -> tab.add(p.getDisplayName()));
                if (args[0].equalsIgnoreCase("removehologram"))
                    tab.addAll(plugin.getConf().getConfigurationSection("holograms").getKeys(false));
            }
        }
        return tab;
    }
}
