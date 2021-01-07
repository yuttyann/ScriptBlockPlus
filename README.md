ScriptBlockPlus [Java11](https://jdk.java.net/) [MC1.9-1.16.x] [![](https://jitpack.io/v/yuttyann/ScriptBlockPlus.svg)](https://jitpack.io/#yuttyann/ScriptBlockPlus)
==========
概要
-----------
[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の機能を引き継ぎ、新機能追加や改善を施したプラグインです。<br>
また、開発者向けに[API](https://github.com/yuttyann/ScriptBlockPlus/wiki/%5BJP%5D-API-Tutorial)を公開しています。<br>

導入
-----------
[Releases](https://github.com/yuttyann/ScriptBlockPlus/releases)または[Yuttyann Files](https://file.yuttyann44581.net/)から`ScriptBlockPlus`のダウンロードを行ってください。<br>
その後前提プラグインである[`Vault`](https://dev.bukkit.org/projects/vault)(v1.7.1以降推奨)をダウンロードを行い`plugins`フォルダへ保存すれば完了です。<br>
### 連携プラグイン
| Plugin | Description | Version |
|:---|:---|:---:|
| [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)  | パケットの補助 | **ALL** |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | プレースホルダの拡張 | **ALL** |
| [ScriptEntityPlus](https://www.spigotmc.org/resources/placeholderapi.6245/) | エンティティにスクリプトを設定 | **ALL** |

対応プラットフォーム
-----------
**[`BukkitAPI`](https://hub.spigotmc.org/javadocs/bukkit/overview-summary.html)を実装しているのであれば**基本的に動作します。  
**★以下動作を確認済みのプラットフォーム一覧★**<br>
・**[CraftBukkit](https://www.spigotmc.org/)**<br>
・**[Spigot](https://www.spigotmc.org/)**<br>
・**[PaperMC](https://papermc.io/)**<br>
・**[CatServer](http://catserver.moe/)**<br>

**★バージョン"1.13-1.13.1"の動作について★**<br>
サーバーが"**JAVA9**"以降に対応していないことが原因で、**エラーが発生する不具合が存在します。**<br>
プラグイン自体は正常に動作するのですが、問題が発生する可能性があるのでなるべく"[**1.13.2**](https://papermc.io/legacy)"を利用してください。<br>
**強引な解決方法**、`<Server>.jar`内の`org/objectweb/asm/ClassVisitor.class`を改変すると動作します。<br>

プラグイン記事
-----------
**[[Github, JP-オプション、コマンドの詳細]](https://github.com/yuttyann/ScriptBlockPlus/wiki#%E4%B8%80%E8%88%AC%E3%81%AE%E6%96%B9%E5%90%91%E3%81%91)**<br>
**[[MCPoteton, JP-使い方と機能の紹介]](https://mcpoteton.com/mcplugin-scriptblockplus)**<br>
**[[SpigotMC, EN-Explanation of the plugin]](https://www.spigotmc.org/resources/1-9-1-15-2-scriptblockplus.78413/)**<br>
**[[MCBBS, ZH-插件说明]](https://www.mcbbs.net/thread-691900-1-1.html)**<br>
**[[Readme.md, History]](https://github.com/yuttyann/ScriptBlockPlus/commits/master/README.md)**<br>