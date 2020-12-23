ScriptBlockPlus [Java8 MC1.9-1.16.x] [![](https://jitpack.io/v/yuttyann/ScriptBlockPlus.svg)](https://jitpack.io/#yuttyann/ScriptBlockPlus)
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
| [~~PsudoCommands~~](https://www.spigotmc.org/resources/psudocommands-add-the-target-selector-to-plugin-commands.56738/)  | セレクターの実装 | **1.9-1.13.1** |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | プレースホルダの拡張 | **ALL** |
| [ScriptEntityPlus](https://www.spigotmc.org/resources/placeholderapi.6245/) | エンティティにスクリプトを設定 | **ALL** |

対応プラットフォーム
-----------
**[`BukkitAPI`](https://hub.spigotmc.org/javadocs/bukkit/overview-summary.html)を実装しているのであれば**基本的に動作します。  
~~&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;~~<br>
**★以下動作を確認済みのプラットフォーム一覧★**<br>
・**[CraftBukkit](https://www.spigotmc.org/)**<br>
・**[Spigot](https://www.spigotmc.org/)**<br>
・**[PaperMC](https://papermc.io/)**<br>
・**[CatServer](http://catserver.moe/)**<br>
~~&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;~~<br>
### 機能制限
最新のMCバージョンを使用している場合には制限は掛かりません。<br>
古いMCバージョンで**NMSに対応したプラットフォーム以外**を利用した場合に制限が掛かります。<br>
※**対応プラットフォームの追加**は[`config.yml`](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/resources/config.yml)の[`Platforms`](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/resources/config.yml#L27)で行えます。<br>
~~&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;~~  
**★以下機能制限の一覧★**<br>
・アドベンチャーモードの左クリック判定の制限 **[v1.9-1.13.1]**<br>
　- 判定が本来のクリック判定ではなく疑似的に再現した判定になります。<br>
・オプション **`@actionbar`** の制限 **[v1.9-1.12]**<br>
　- BukkitAPIに実装されていないため利用はできません。<br>
~~&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;~~<br>

プラグイン記事
-----------
**[[Github, JP-オプション、コマンドの詳細]](https://github.com/yuttyann/ScriptBlockPlus/wiki#%E4%B8%80%E8%88%AC%E3%81%AE%E6%96%B9%E5%90%91%E3%81%91)**<br>
**[[MCPoteton, JP-使い方と機能の紹介]](https://mcpoteton.com/mcplugin-scriptblockplus)**<br>
**[[SpigotMC, EN-Explanation of the plugin]](https://www.spigotmc.org/resources/1-9-1-15-2-scriptblockplus.78413/)**<br>
**[[MCBBS, ZH-插件说明]](https://www.mcbbs.net/thread-691900-1-1.html)**<br>
**[[Readme.md, History]](https://github.com/yuttyann/ScriptBlockPlus/commits/master/README.md)**<br>