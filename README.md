ScriptBlockPlus [Java11](https://adoptopenjdk.net/?variant=openjdk11) [MC1.9-1.16.x] [![](https://jitpack.io/v/yuttyann/ScriptBlockPlus.svg)](https://jitpack.io/#yuttyann/ScriptBlockPlus)
==========

概要
-----------
[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の機能を引き継ぎ、新機能追加や改善を施したプラグインです。  
また、開発者向けに[API](https://github.com/yuttyann/ScriptBlockPlus/wiki/%5BJP%5D-API-Tutorial)を公開しています。  

導入
-----------
配布サイトから`ScriptBlockPlus`のダウンロードを行ってください。  
その後前提プラグインである[`Vault`](https://dev.bukkit.org/projects/vault)をダウンロードを行い`plugins`フォルダへ保存すれば完了です。  

### Java8版
別の[リポジトリ](https://github.com/yuttyann/ScriptBlockPlus-Java8)にて、**ScriptBlockPlus**の**Java8版**を[公開](https://github.com/yuttyann/ScriptBlockPlus-Java8/releases)しています。  
古いプラットフォームのサーバーで[**Java11**](https://adoptopenjdk.net/?variant=openjdk11)が動作しない場合はご利用ください。  
但し、基本的にはサポートを行っていない事と、近いうちに更新停止することをご了承ください。  

### 連携プラグイン
| Plugin | Description | Version |
|:---|:---|:---:|
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | プレースホルダの拡張 | **ALL** |
| [ScriptEntityPlus](https://github.com/yuttyann/ScriptEntityPlus) | エンティティにスクリプトを設定 | **ALL** |  

**★各バージョンの動作範囲★**
| Plugin | Server | Java |
|:---:|:---:|:---:|
|**`x.x.x-JV8`**|**`1.9.x-1.16.x`**|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|**`2.0.4-2.1.1`**|**`1.9.x-1.16.x`**|**[Java11](https://adoptopenjdk.net/?variant=openjdk11)**|
|`1.8.5-2.0.3`|`1.9.x-1.16.x`|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|`1.6.0-1.8.4`|`1.8.x-1.15.2`|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|`1.4.0-1.5.0`|`1.7.x-1.13.2`|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|`1.0.0-1.3.3`|`1.7.x-1.13.2`|**[Java7](https://jdk.java.net/java-se-ri/7)**|

対応プラットフォーム
-----------
**[`BukkitAPI`](https://hub.spigotmc.org/javadocs/bukkit/overview-summary.html)を実装しているのであれば**基本的に動作します。  
[**`net.minecraft.server`**](https://sodocumentation.net/ja/bukkit/topic/9576/nms)が見つからなかった場合は、**NMS依存の機能が強制的に無効**になります。  
**★以下、動作確認済みのプラットフォーム★**  
・**[Spigot](https://www.spigotmc.org/)**  
・**[Tuinity](https://github.com/Spottedleaf/Tuinity)**  
・**[PaperMC](https://papermc.io/)**  
・**[AkarinPRJ](https://github.com/Akarin-project/Akarin)**  
・**[CatServer](https://github.com/Luohuayu/CatServer)**`(Java11では動作しません)`  

**★バージョン"1.13-1.13.1"の動作について★**  
サーバーが[**Java9**](https://jdk.java.net/java-se-ri/9)以降に対応していないことが原因で、**エラーが発生する不具合が存在します。**  
プラグイン自体は正常に動作するのですが、問題が発生する可能性があるのでなるべく"[**1.13.2**](https://papermc.io/legacy)"を利用してください。  
**強引な解決方法**、`<Server>.jar`内の`org/objectweb/asm/ClassVisitor.class`を[改変](https://pastebin.com/UFBdKXJD)することで動作します。  

リンク
-----------
**[[Github, JP]](https://github.com/yuttyann/ScriptBlockPlus/wiki#%E4%B8%80%E8%88%AC%E3%81%AE%E6%96%B9%E5%90%91%E3%81%91)**  
**[[Dropbox, JP]](https://www.dropbox.com/sh/gj8vunx95785y2d/AABdP-kJhEilLHWMWZtaWgQma)**  
**[[MCPoteton, JP]](https://mcpoteton.com/mcplugin-scriptblockplus)**  
**[[SpigotMC, EN]](https://www.spigotmc.org/resources/1-9-1-15-2-scriptblockplus.78413/)**  
**[[CurseForge, EN]](https://www.curseforge.com/minecraft/bukkit-plugins/scriptblockplus)**  
**[[MCBBS, ZH]](https://www.mcbbs.net/thread-691900-1-1.html)**