package top.mcocet.spawnPointProtection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

/**
 * Folia 兼容工具类
 * 提供 Paper 和 Folia 之间的兼容性抽象
 */
public class FoliaCompat {
    
    private static final boolean IS_FOLIA;
    
    static {
        // 检测是否为 Folia
        boolean isFolia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
        IS_FOLIA = isFolia;
    }
    
    /**
     * 判断当前服务器是否为 Folia
     * @return true 如果是 Folia
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }
    
    /**
     * 异步传送玩家（兼容 Folia 和 Paper）
     * @param player 玩家
     * @param location 目标位置
     * @return CompletableFuture<Boolean> 传送结果
     */
    public static CompletableFuture<Boolean> teleportAsync(Player player, Location location) {
        if (IS_FOLIA) {
            // Folia - 使用 teleportAsync
            return player.teleportAsync(location);
        } else {
            // Paper/Spigot - 使用同步 teleport，包装为 CompletableFuture
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            boolean result = player.teleport(location);
            future.complete(result);
            return future;
        }
    }
    
    /**
     * 延迟执行任务（兼容 Folia 和 Paper）
     * @param plugin 插件实例
     * @param location 位置（用于 Folia 确定区域）
     * @param delayTicks 延迟刻数
     * @param task 要执行的任务
     */
    public static void runLater(Plugin plugin, Location location, long delayTicks, Runnable task) {
        if (IS_FOLIA) {
            // Folia - 使用区域调度器
            try {
                Bukkit.getRegionScheduler().runDelayed(plugin, location, scheduledTask -> task.run(), delayTicks);
            } catch (Exception e) {
                // 如果失败，尝试全局调度器
                Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delayTicks);
            }
        } else {
            // Paper/Spigot - 使用 BukkitRunnable
            new BukkitRunnable() {
                @Override
                public void run() {
                    task.run();
                }
            }.runTaskLater(plugin, delayTicks);
        }
    }
    
    /**
     * 立即执行任务（兼容 Folia 和 Paper）
     * @param plugin 插件实例
     * @param location 位置（用于 Folia 确定区域）
     * @param task 要执行的任务
     */
    public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
        if (IS_FOLIA) {
            // Folia - 使用区域调度器
            try {
                Bukkit.getRegionScheduler().execute(plugin, location, task);
            } catch (Exception e) {
                // 如果失败，尝试全局调度器
                Bukkit.getGlobalRegionScheduler().execute(plugin, task);
            }
        } else {
            // Paper/Spigot - 使用 BukkitRunnable
            new BukkitRunnable() {
                @Override
                public void run() {
                    task.run();
                }
            }.runTask(plugin);
        }
    }
}
