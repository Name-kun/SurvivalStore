package net.nametarou.survivalstore.saisen;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.tr7zw.nbtapi.NBTItem;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.nametarou.survivalstore.SurvivalStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SaisenListener implements @NotNull Listener {

    public static long lastModified;

    SurvivalStore plugin = SurvivalStore.getPlugin(SurvivalStore.class);

    public static ItemStack getSaisen(double dollar, int amount) {
        ItemStack item = new ItemStack(Material.SUNFLOWER, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lお賽銭");
        meta.setLore(Arrays.asList("§a外の世界から現れたお賽銭。", "§a一説によると神の加護があるらしい。", "§5賽銭箱に投げ入れると...？"));
        meta.addEnchant(Enchantment.MENDING, 0, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setDouble("Dollar", dollar);
        nbtItem.applyNBT(item);
        return item;
    }

    @EventHandler
    public void onThrow(PlayerDropItemEvent e) {
        e.getItemDrop().getItemStack();
        if (new NBTItem(e.getItemDrop().getItemStack()).hasKey("Dollar"))
            if (getRegions(e.getPlayer()) != null) {
                Player p = e.getPlayer();
                List<String> pRegions = getRegions(p);
                pRegions.forEach(region -> {
                    if (plugin.getConf().getStringList("saisenRegions").contains(region))
                        saisenEvent(p, e.getItemDrop());
                });
            }
    }

    private void saisenEvent(Player p, Item thrownItem) {
        if (p.getTargetBlock((Set<Material>) null, 5).getType().equals(Material.JUKEBOX)) {
            double dollar = new NBTItem(thrownItem.getItemStack()).getDouble("Dollar");
            plugin.getConf().set("currentDollar", plugin.getConf().getDouble("currentDollar") + dollar);
            plugin.saveConf();
            thrownItem.remove();
            Bukkit.broadcastMessage("§7[§eお賽銭§7]§2" + p.getDisplayName() + "§7さんが§6$" + dollar + "§7のお賽銭をしました！");
        }
    }

    public void hologramUpdater() {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.configFile.lastModified() != lastModified) {
                    changeHologram();
                    lastModified = plugin.configFile.lastModified();
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    public void changeHologram() {
        double dollar = plugin.getConf().getDouble("currentDollar");
        List<String> content = new ArrayList<>();
        content.add("§c§m-§6§m-§e§m-§a§m-§b§m-§3§l お賽銭 §b§m-§a§m-§e§m-§6§m-§c§m-");
        content.add("§6§l$" + dollar);
        plugin.getConf().getConfigurationSection("holograms").getKeys(false).forEach(key -> {
            if (DHAPI.getHologram(key) != null) {
                Hologram hologram = DHAPI.getHologram(key);
                DHAPI.setHologramLines(hologram, content);
            } else {
                int x = plugin.getConf().getInt("holograms." + key + ".X");
                int y = plugin.getConf().getInt("holograms." + key + ".Y");
                int z = plugin.getConf().getInt("holograms." + key + ".Z");
                String hologramWorld = plugin.getConf().getString("holograms." + key + ".world");
                World world = Bukkit.getWorld(hologramWorld);
                Location loc = new Location(world, x, y, z);
                DHAPI.createHologram(key, loc, content);
            }
        });
    }

    public static List<String> getRegions(Player p) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(p.getWorld())));
        Location loc = p.getLocation();
        ApplicableRegionSet ars;
        List<String> regionNames = new ArrayList<>();
        if (regions != null) {
            ars = regions.getApplicableRegions(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            for (ProtectedRegion region : ars.getRegions()) {
                regionNames.add(region.getId());
            }
            return regionNames;
        }
        return null;
    }
}
