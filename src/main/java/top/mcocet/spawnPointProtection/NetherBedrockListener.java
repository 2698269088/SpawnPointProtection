package top.mcocet.spawnPointProtection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class NetherBedrockListener implements Listener {
    
    private final SpawnPointProtection plugin;
    private final ConfigManager configManager;
    
    public NetherBedrockListener(SpawnPointProtection plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    /**
     * 处理玩家移动事件 - 下界基岩层控制
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        // 只在地狱维度生效
        if (world.getEnvironment() != World.Environment.NETHER) return;
        
        Location to = event.getTo();
        if (to == null) return;
        
        double y = to.getY();
        
        boolean shouldRestrict = false;
        int restrictHeight;
        
        if (!configManager.isAllowAboveBedrock()) {
            // 不允许进入基岩层以上，只要超过 Y=127 就限制
            restrictHeight = 127;
            shouldRestrict = y > restrictHeight;
        } else {
            // 允许进入基岩层以上，但超过阈值才限制
            restrictHeight = configManager.getTeleportThresholdHeight();
            shouldRestrict = y > restrictHeight;
        }
        
        // 如果需要限制
        if (shouldRestrict) {
            // 如果需要传送玩家到基岩层以下
            if (configManager.isTeleportBelowBedrock()) {
                int teleportHeight = configManager.getTeleportHeight();
                Location newLocation = to.clone();
                newLocation.setY(teleportHeight);
                
                // 使用 FoliaCompat 进行异步传送（兼容 Folia 和 Paper）
                FoliaCompat.teleportAsync(player, newLocation).thenAccept(success -> {
                    if (success) {
                        player.sendMessage("§c你已被传送到基岩层以下！");
                    }
                });
            } else {
                // 否则阻止移动，将玩家送回原位置
                event.setTo(event.getFrom());
            }
        }
    }
    
    /**
     * 处理方块放置事件 - 下界基岩层上方建造控制
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        // 只在地狱维度生效
        if (world.getEnvironment() != World.Environment.NETHER) return;
        
        // 如果不允许在基岩层上方放置方块（Y > 127）
        if (!configManager.isAllowBuildAboveBedrock()) {
            Location blockLocation = event.getBlock().getLocation();
            if (blockLocation.getY() > 127) {
                event.setCancelled(true);
                player.sendMessage("§c你不能在基岩层上方放置方块！");
            }
        }
    }
}
