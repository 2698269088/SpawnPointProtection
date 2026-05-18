package top.mcocet.spawnPointProtection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import top.mcocet.spawnPointProtection.SpawnPointProtection;

public class ProtectionListener implements Listener {
    
    private final SpawnPointProtection plugin;
    private final ConfigManager configManager;
    
    public ProtectionListener(SpawnPointProtection plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    /**
     * 检查实体是否在保护区域内
     * @param location 要检查的位置
     * @return 是否在保护区域内
     */
    private boolean isInProtectedArea(Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        
        // 检查该世界是否在保护列表中
        if (!configManager.isWorldProtected(world)) {
            return false;
        }
        
        // 获取世界出生点
        Location spawnLocation = world.getSpawnLocation();
        
        // 计算距离（转换为方块距离，1区块=16方块）
        int radiusInBlocks = configManager.getProtectedRadius() * 16;
        double distance = location.distance(spawnLocation);
        
        return distance <= radiusInBlocks;
    }
    
    /**
     * 处理实体爆炸事件 - 清理爆炸物
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!configManager.isClearExplosives()) return;
        
        Location location = event.getLocation();
        if (isInProtectedArea(location)) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理方块爆炸事件 - 清理爆炸物
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!configManager.isClearExplosives()) return;
        
        Location location = event.getBlock().getLocation();
        if (isInProtectedArea(location)) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理爆炸伤害事件 - 防止爆炸伤害
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosionDamage(EntityDamageByEntityEvent event) {
        // 检查是否是爆炸造成的伤害
        if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && 
            event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }
        
        Entity target = event.getEntity();
        if (!(target instanceof Player)) return;
        
        Location location = target.getLocation();
        if (isInProtectedArea(location)) {
            // 取消爆炸伤害
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理生物生成事件 - 控制怪物生成
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (configManager.isAllowMobSpawn()) return;
        
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        
        // 阻止敌对生物和幻翼生成
        if ((entity instanceof Monster || entity instanceof Phantom) && isInProtectedArea(location)) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 处理玩家受伤事件 - 伤害免疫
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!configManager.isDamageImmunity()) return;
        
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        
        Location location = entity.getLocation();
        if (isInProtectedArea(location)) {
            // 设置伤害为0，保留动画效果
            event.setDamage(0.0);
        }
    }
    
    /**
     * 处理PVP事件 - 控制PVP
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (configManager.isAllowPvp()) return;
        
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        
        // 只有当攻击者和目标都是玩家时才阻止
        if (!(damager instanceof Player) || !(target instanceof Player)) return;
        
        Player attacker = (Player) damager;
        Location attackerLocation = attacker.getLocation();
        Location targetLocation = target.getLocation();
        
        // 如果攻击者或目标在保护区域内，阻止PVP
        if (isInProtectedArea(attackerLocation) || isInProtectedArea(targetLocation)) {
            event.setCancelled(true);
        }
    }
}
