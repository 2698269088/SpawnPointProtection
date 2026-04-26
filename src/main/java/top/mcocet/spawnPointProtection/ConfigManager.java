package top.mcocet.spawnPointProtection;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {
    
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    
    // 配置项默认值
    private static final int DEFAULT_PROTECTED_RADIUS = 16;
    private static final boolean DEFAULT_CLEAR_EXPLOSIVES = true;
    private static final boolean DEFAULT_ALLOW_MOB_SPAWN = false;
    private static final boolean DEFAULT_ALLOW_PVP = false;
    private static final boolean DEFAULT_DAMAGE_IMMUNITY = true;
    private static final List<String> DEFAULT_PROTECTED_WORLDS = List.of(
        "NORMAL", "NETHER", "THE_END"
    );
    
    // 下界基岩层控制默认值
    private static final boolean DEFAULT_ALLOW_ABOVE_BEDROCK = true;
    private static final int DEFAULT_TELEPORT_THRESHOLD_HEIGHT = 150;
    private static final boolean DEFAULT_TELEPORT_BELOW_BEDROCK = false;
    private static final int DEFAULT_TELEPORT_HEIGHT = 120;
    private static final boolean DEFAULT_ALLOW_BUILD_ABOVE_BEDROCK = true;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    /**
     * 加载配置文件
     */
    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        
        // 如果配置文件不存在，创建默认配置
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // 设置默认值
        setDefaults();
        
        // 保存配置
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存配置文件: " + e.getMessage());
        }
    }
    
    /**
     * 设置默认配置值
     */
    private void setDefaults() {
        config.addDefault("protected-radius", DEFAULT_PROTECTED_RADIUS);
        config.addDefault("clear-explosives", DEFAULT_CLEAR_EXPLOSIVES);
        config.addDefault("allow-mob-spawn", DEFAULT_ALLOW_MOB_SPAWN);
        config.addDefault("allow-pvp", DEFAULT_ALLOW_PVP);
        config.addDefault("damage-immunity", DEFAULT_DAMAGE_IMMUNITY);
        config.addDefault("protected-worlds", DEFAULT_PROTECTED_WORLDS);
        
        // 下界基岩层控制配置
        config.addDefault("nether-bedrock-control.allow-above-bedrock", DEFAULT_ALLOW_ABOVE_BEDROCK);
        config.addDefault("nether-bedrock-control.teleport-threshold-height", DEFAULT_TELEPORT_THRESHOLD_HEIGHT);
        config.addDefault("nether-bedrock-control.teleport-below-bedrock", DEFAULT_TELEPORT_BELOW_BEDROCK);
        config.addDefault("nether-bedrock-control.teleport-height", DEFAULT_TELEPORT_HEIGHT);
        config.addDefault("nether-bedrock-control.allow-build-above-bedrock", DEFAULT_ALLOW_BUILD_ABOVE_BEDROCK);
        
        config.options().copyDefaults(true);
    }
    
    /**
     * 重新加载配置文件
     */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        setDefaults();
    }
    
    /**
     * 获取受保护区域半径（区块）
     * @return 半径值
     */
    public int getProtectedRadius() {
        return config.getInt("protected-radius", DEFAULT_PROTECTED_RADIUS);
    }
    
    /**
     * 是否清理爆炸物
     * @return true为清理，false为不清理
     */
    public boolean isClearExplosives() {
        return config.getBoolean("clear-explosives", DEFAULT_CLEAR_EXPLOSIVES);
    }
    
    /**
     * 是否允许生成怪物
     * @return true为允许，false为禁止
     */
    public boolean isAllowMobSpawn() {
        return config.getBoolean("allow-mob-spawn", DEFAULT_ALLOW_MOB_SPAWN);
    }
    
    /**
     * 是否允许PVP
     * @return true为允许，false为禁止
     */
    public boolean isAllowPvp() {
        return config.getBoolean("allow-pvp", DEFAULT_ALLOW_PVP);
    }
    
    /**
     * 是否免疫伤害
     * @return true为免疫，false为正常受伤
     */
    public boolean isDamageImmunity() {
        return config.getBoolean("damage-immunity", DEFAULT_DAMAGE_IMMUNITY);
    }
    
    /**
     * 获取原始配置文件对象
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {
        return config;
    }
    
    /**
     * 检查世界是否在保护列表中
     * @param world 要检查的世界
     * @return true为受保护，false为不受保护
     */
    public boolean isWorldProtected(World world) {
        if (world == null) return false;
        
        List<String> protectedWorlds = config.getStringList("protected-worlds");
        if (protectedWorlds.isEmpty()) {
            // 如果列表为空，默认保护所有世界
            return true;
        }
        
        String environmentName = world.getEnvironment().name();
        return protectedWorlds.contains(environmentName);
    }
    
    /**
     * 是否允许玩家进入下界基岩层以上（Y > 127）
     * @return true为允许，false为禁止
     */
    public boolean isAllowAboveBedrock() {
        return config.getBoolean("nether-bedrock-control.allow-above-bedrock", DEFAULT_ALLOW_ABOVE_BEDROCK);
    }
    
    /**
     * 获取触发传送的高度阈值
     * @return 触发传送的Y坐标值
     */
    public int getTeleportThresholdHeight() {
        return config.getInt("nether-bedrock-control.teleport-threshold-height", DEFAULT_TELEPORT_THRESHOLD_HEIGHT);
    }
    
    /**
     * 是否将玩家传送到基岩层以下
     * @return true为传送，false为不传送
     */
    public boolean isTeleportBelowBedrock() {
        return config.getBoolean("nether-bedrock-control.teleport-below-bedrock", DEFAULT_TELEPORT_BELOW_BEDROCK);
    }
    
    /**
     * 获取传送目标高度
     * @return 传送高度值
     */
    public int getTeleportHeight() {
        return config.getInt("nether-bedrock-control.teleport-height", DEFAULT_TELEPORT_HEIGHT);
    }
    
    /**
     * 是否允许玩家在基岩层上方放置方块（Y > 127）
     * @return true为允许，false为禁止
     */
    public boolean isAllowBuildAboveBedrock() {
        return config.getBoolean("nether-bedrock-control.allow-build-above-bedrock", DEFAULT_ALLOW_BUILD_ABOVE_BEDROCK);
    }
}
