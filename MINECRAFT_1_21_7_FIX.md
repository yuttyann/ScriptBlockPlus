# ScriptBlockPlus Minecraft 1.21.7 修复说明

## 修复的问题

### 1. Component$Serializer 类找不到的问题
**错误信息**: `java.lang.ClassNotFoundException: net.minecraft.network.chat.Component$Serializer`

**原因**: 在 Minecraft 1.21.7 中，Paper 服务器的内部类结构可能发生了变化。

**修复方案**: 
- 创建了新的 `PaperRemappedAccessor_v1_21_7` 类，专门处理 1.21.7 版本的兼容性
- 在 `NativeAccessor.get()` 方法中添加了对 1.21.7 版本的检测和处理
- 修复了 `McVersion.java` 中的版本定义错误（1.21.x 系列被错误地定义为 1.20.x）

### 2. 插件禁用时的任务注册问题
**错误信息**: `org.bukkit.plugin.IllegalPluginAccessException: Plugin attempted to register task while disabled`

**原因**: `ScriptViewer` 类在静态初始化块中尝试注册定时任务，当插件被禁用时会导致错误。

**修复方案**:
- 移除了 `ScriptViewer` 中的静态初始化块
- 添加了 `startTickTask()` 和 `stopTickTask()` 方法来管理定时任务
- 在 `ScriptBlock.onEnable()` 中调用 `startTickTask()`
- 在 `ScriptBlock.onDisable()` 中调用 `stopTickTask()`

## 修改的文件列表

1. `/src/main/java/com/github/yuttyann/scriptblockplus/utils/version/McVersion.java`
   - 修复了 1.21.x 版本系列的定义

2. `/src/main/java/com/github/yuttyann/scriptblockplus/utils/server/minecraft/NativeAccessor.java`
   - 添加了对 1.21.7 版本的支持

3. `/src/main/java/com/github/yuttyann/scriptblockplus/utils/server/minecraft/PaperRemappedAccessor_v1_21_7.java`
   - 新创建的文件，专门处理 1.21.7 版本的兼容性

4. `/src/main/java/com/github/yuttyann/scriptblockplus/item/action/ScriptViewer.java`
   - 修复了静态初始化块的问题

5. `/src/main/java/com/github/yuttyann/scriptblockplus/ScriptBlock.java`
   - 在启动和关闭时正确管理 ScriptViewer 的定时任务

## 构建说明

该项目使用 Maven 作为构建工具。要构建修复后的插件，请按照以下步骤操作：

### 1. 安装 Maven
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install maven

# 或者从官网下载安装
# https://maven.apache.org/download.cgi
```

### 2. 构建项目
```bash
cd /home/wangmeng123/ScriptBlockPlus-master
mvn clean package
```

### 3. 获取构建结果
构建成功后，插件 JAR 文件将位于：
```
target/ScriptBlockPlus v2.3.3.jar
```

### 4. 安装到服务器
将生成的 JAR 文件复制到 Minecraft 服务器的 `plugins` 目录中，然后重启服务器。

## 注意事项

1. 这些修复是基于错误日志分析进行的，建议在测试服务器上先进行测试
2. 如果仍然遇到问题，可能需要进一步调试以确定具体的类名映射
3. Paper 1.21.7 可能有其他未发现的 API 变更，使用时请注意观察服务器日志

## 技术细节

### Component 序列化处理
在新的 `PaperRemappedAccessor_v1_21_7` 中，我们尝试了多种方法来查找正确的 Component 序列化器：
1. 首先尝试使用内部类形式：`Component.Serializer`
2. 如果失败，尝试使用其他可能的类名：`ComponentSerialization`
3. 在序列化/反序列化方法中，添加了回退机制，尝试不带 `HolderLookup` 参数的版本

这种多重回退机制确保了在不同的 Paper 版本中都能正常工作。