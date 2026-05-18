package top.mcocet.spawnPointProtection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class TeleportToSpawnCommand implements CommandExecutor {
    
    private final SpawnPointProtection plugin;
    
    public TeleportToSpawnCommand(SpawnPointProtection plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 检查是否为玩家执行命令
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c此命令只能由玩家执行！");
            return true;
        }
        
        Player player = (Player) sender;
        
        // 获取主世界
        World world = Bukkit.getWorlds().get(0);
        if (world == null) {
            player.sendMessage("§c无法找到主世界！");
            return true;
        }
        
        // 获取世界出生点
        Location spawnLocation = world.getSpawnLocation();
        
        // 获取保护半径（转换为方块）
        int radiusInBlocks = plugin.getConfigManager().getProtectedRadius() * 16;
        
        // 生成随机位置（在保护范围内）
        Random random = new Random();
        double offsetX = (random.nextDouble() * 2 - 1) * radiusInBlocks; // -radius 到 +radius
        double offsetZ = (random.nextDouble() * 2 - 1) * radiusInBlocks; // -radius 到 +radius
        
        // 计算随机位置
        double randomX = spawnLocation.getX() + offsetX;
        double randomZ = spawnLocation.getZ() + offsetZ;
        
        // 创建临时位置用于确定区域
        Location tempLocation = new Location(world, randomX, 0, randomZ);
        
        // 使用 Folia 兼容的方式获取最高方块高度并传送
        FoliaCompat.runAtLocation(plugin, tempLocation, () -> {
            // 获取该位置的最高安全Y坐标
            int y = world.getHighestBlockYAt((int) randomX, (int) randomZ);
            
            // 创建目标位置（保持出生点的yaw和pitch）
            Location targetLocation = new Location(world, randomX, y + 1, randomZ, spawnLocation.getYaw(), spawnLocation.getPitch());
            
            // 使用 Folia 兼容的方式传送玩家
            FoliaCompat.teleportAsync(player, targetLocation).thenAccept(success -> {
                if (success) {
                    player.sendMessage("§a已传送到世界出生点");
                } else {
                    player.sendMessage("§c传送失败！");
                }
            });
        });
        
        return true;
    }
}