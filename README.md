# SpawnPointProtection

一个 Minecraft 服务器插件，用于保护出生点区域和下界基岩层，支持 Paper 和 Folia 服务器。

## 功能特性

### 出生点保护
- **爆炸物清理**：自动阻止 TNT、苦力怕等爆炸物在保护区域内爆炸
- **怪物生成控制**：禁止敌对生物在保护区域内生成
- **伤害免疫**：玩家在保护区域内不会受到伤害（保留伤害动画）
- **PVP 控制**：可以禁止或允许保护区域内的玩家对战
- **可配置保护半径**：以区块为单位设置保护范围
- **多维度支持**：可以选择保护哪些维度（主世界、地狱、末地）

### 下界基岩层控制
- **高度限制**：可以禁止玩家进入基岩层以上（Y > 127）
- **传送机制**：超过指定高度阈值时自动传送玩家到安全位置
- **建造控制**：可以禁止在基岩层上方放置方块
- **灵活配置**：支持自定义触发传送的高度和目标高度

### 其他特性
- **Folia 兼容**：完全支持 Folia 服务器的多线程架构
- **热重载**：使用 `/spp reload` 命令无需重启即可重新加载配置
- **权限系统**：支持权限管理插件

## 安装

1. 下载最新的插件 JAR 文件
2. 将 JAR 文件放入服务器的 `plugins` 文件夹
3. 重启服务器
4. 编辑 `plugins/SpawnPointProtection/config.yml` 配置文件
5. 使用 `/spp reload` 重新加载配置

## 配置说明

### 基础配置

```yaml
# 受保护区域半径（单位：区块）
protected-radius: 16

# 是否清理爆炸物（如TNT、苦力怕爆炸等）
clear-explosives: true

# 是否允许生成怪物或其他敌对生物
allow-mob-spawn: false

# 是否允许PVP（玩家对战）
allow-pvp: false

# 是否将玩家收到的伤害设置为0
# 保留伤害动画和效果，但不扣除生命值
damage-immunity: true
```

### 维度保护配置

```yaml
# 受保护的维度列表（World.Environment）
# 可选值: NORMAL(主世界), NETHER(地狱), THE_END(末地)
protected-worlds:
  - "NORMAL"
  - "NETHER"
  - "THE_END"
```

### 下界基岩层控制

```yaml
# 下界基岩层控制（仅在地狱维度生效，不受保护区块距离限制）
nether-bedrock-control:
  # 是否允许玩家进入下界基岩层以上（Y > 127）
  allow-above-bedrock: true
  
  # 触发传送的高度阈值（当玩家Y坐标超过此值时才会被传送）
  teleport-threshold-height: 256
  
  # 是否将玩家传送到基岩层以下（当玩家在基岩层以上时）
  teleport-below-bedrock: true
  
  # 传送目标高度（仅在teleport-below-bedrock为true时生效）
  teleport-height: 80
  
  # 是否允许玩家在基岩层上方放置方块（Y > 127）
  allow-build-above-bedrock: true
```

## 命令

| 命令 | 描述 | 权限 |
|------|------|------|
| `/spp reload` | 重新加载插件配置 | `spp.reload` |
| `/spawnpointprotection reload` | 重新加载插件配置（别名） | `spp.reload` |

## 权限

| 权限节点 | 描述 | 默认 |
|---------|------|------|
| `spp.reload` | 允许重新加载插件配置 | OP |

## 兼容性

- **Paper API**: 1.21+
- **Folia**: 完全支持
- **Java**: 21+

## 编译

需要 Maven 和 Java 21 或更高版本。

```bash
mvn clean package
```

编译后的 JAR 文件将位于 `target` 目录中。

## 开发

### 项目结构

```
SpawnPointProtection/
├── src/main/java/top/mcocet/spawnPointProtection/
│   ├── SpawnPointProtection.java    # 主类
│   ├── ConfigManager.java           # 配置管理器
│   ├── ProtectionListener.java      # 出生点保护监听器
│   ├── NetherBedrockListener.java   # 下界基岩层监听器
│   ├── ReloadCommand.java           # 重载命令
│   └── FoliaCompat.java             # Folia 兼容工具
├── src/main/resources/
│   ├── config.yml                   # 默认配置
│   └── plugin.yml                   # 插件描述
└── pom.xml                          # Maven 配置
```

## 常见问题

### Q: 为什么玩家在基岩层以上没有被传送？
A: 检查以下配置：
- `allow-above-bedrock` 是否为 `false`
- `teleport-below-bedrock` 是否为 `true`
- 如果 `allow-above-bedrock` 为 `true`，确保玩家高度超过了 `teleport-threshold-height`

### Q: 插件在 Folia 上报错怎么办？
A: 本插件已完全兼容 Folia，请确保使用最新版本。如果遇到 `UnsupportedOperationException` 错误，请更新插件。

### Q: 如何只保护主世界？
A: 修改 `protected-worlds` 配置：
```yaml
protected-worlds:
  - "NORMAL"
```

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

- 网站: https://home.mcocet.top
- 作者: MCOCET

## 致谢

感谢所有为本项目做出贡献的开发者和用户！
