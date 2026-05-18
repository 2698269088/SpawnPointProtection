package top.mcocet.spawnPointProtection;

import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnPointProtection extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // 初始化配置管理器
        configManager = new ConfigManager(this);
        getLogger().info("配置文件已加载！");
        
        // 检测服务器类型
        if (FoliaCompat.isFolia()) {
            getLogger().info("检测到 Folia 服务器，已启用 Folia 兼容模式");
        } else {
            getLogger().info("运行在 Paper/Spigot 服务器上");
        }
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new NetherBedrockListener(this), this);
        
        // 注册命令
        ReloadCommand reloadCommand = new ReloadCommand(this);
        getCommand("spp").setExecutor(reloadCommand);
        
        TeleportToSpawnCommand teleportCommand = new TeleportToSpawnCommand(this);
        getCommand("tpsp").setExecutor(teleportCommand);
        
        getLogger().info("出生点保护功能已启用！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    /**
     * 获取配置管理器实例
     * @return ConfigManager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
