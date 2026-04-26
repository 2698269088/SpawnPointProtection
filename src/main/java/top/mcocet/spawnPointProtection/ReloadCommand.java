package top.mcocet.spawnPointProtection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    
    private final SpawnPointProtection plugin;
    
    public ReloadCommand(SpawnPointProtection plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 检查权限
        if (!sender.hasPermission("spp.reload")) {
            sender.sendMessage("§c你没有权限执行此命令！");
            return true;
        }
        
        // 重新加载配置
        plugin.getConfigManager().reload();
        
        sender.sendMessage("§a配置文件已重新加载！");
        
        return true;
    }
}
