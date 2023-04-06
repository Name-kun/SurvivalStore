package net.nametarou.survivalstore;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.utils.scheduler.S;
import net.nametarou.survivalstore.saisen.SaisenCmd;
import net.nametarou.survivalstore.saisen.SaisenListener;
import net.nametarou.survivalstore.saisen.SasienTab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.nametarou.survivalstore.saisen.SaisenListener.*;

public final class SurvivalStore extends JavaPlugin implements CommandExecutor, TabCompleter {

    public File configFile;
    public FileConfiguration config;

    @Override
    public void onEnable() {
        SaisenListener saisenListener = new SaisenListener();
        getCommand("saisen").setExecutor(new SaisenCmd());
        getServer().getPluginManager().registerEvents(saisenListener, this);
        getCommand("saisen").setTabCompleter(new SasienTab());
        getCommand("store").setExecutor(this);
        createFiles();
        lastModified = configFile.lastModified();
        saisenListener.hologramUpdater();
        try {
            saisenListener.changeHologram();
        } catch (Exception e) {
            getLogger().info("§cエラーが発生しました。");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("store")) {
            if (!sender.hasPermission("survivalstore.admin")) {
                sender.sendMessage("§c権限がありません。");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage("§a/store reload§7: プラグイン全体をリロードします");
                return true;
            } else {
                if (args[0].equalsIgnoreCase("reload")) {
                    createFiles();
                    sender.sendMessage("§aリロードが完了しました。");
                } else {
                    sender.sendMessage("§a/store reload§7: プラグイン全体をリロードします");
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("store")) {
            if (args.length == 1) {
                if (sender.hasPermission("survivalstore.admin")) {
                    tab.add("reload");
                }
            }
        }
        return tab;
    }

    public void createFiles() {
        configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConf() {
        return config;
    }

    public void saveConf() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
