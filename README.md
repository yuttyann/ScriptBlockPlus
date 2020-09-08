ScriptBlockPlus [Java8 MC1.9-1.16.2] [![](https://jitpack.io/v/yuttyann/ScriptBlockPlus.svg)](https://jitpack.io/#yuttyann/ScriptBlockPlus)
==========
概要
--------------------------------------------------
[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の機能を引き継ぎ、新機能追加や改善を施したプラグインです。  
また、開発者向けに[API](https://github.com/yuttyann/ScriptBlockPlus/wiki/%5BJP%5D-API-Tutorial)を公開しています。  

導入
-----------
[Releases](https://github.com/yuttyann/ScriptBlockPlus/releases)または[Yuttyann Files](https://file.yuttyann44581.net/)から`ScriptBlockPlus`のダウンロードを行ってください。  
その後前提プラグインである[`Vault`](https://dev.bukkit.org/projects/vault)(v1.7.1以降推奨)をダウンロードを行い`plugins`フォルダへ保存すれば完了です。  
――――――――――――――――――――――――――――――――――  
**連携プラグイン（拡張機能）**  
・[PsudoCommands - セレクター追加](https://www.spigotmc.org/resources/psudocommands-add-the-target-selector-to-plugin-commands.56738/)  
・[PlaceholderAPI - プレースホルダ拡張](https://www.spigotmc.org/resources/placeholderapi.6245/)  
・[ScriptEntityPlus - エンティティにスクリプトを設定](https://github.com/yuttyann/ScriptEntityPlus)  
――――――――――――――――――――――――――――――――――  

対応プラットフォーム
-----------
**[`BukkitAPI`](https://hub.spigotmc.org/javadocs/bukkit/overview-summary.html)を実装しているのであれば**基本的に動作します。  
――――――――――――――――――――――――――――――――――  
**★以下動作を確認済みのプラットフォーム一覧★**  
・[CraftBukkit](https://www.spigotmc.org/)  
・[Spigot](https://www.spigotmc.org/)  
・[PaperMC](https://papermc.io/)  
・[CatServer](http://catserver.moe/)  
――――――――――――――――――――――――――――――――――  
### 機能制限
最新のMCバージョンを使用している場合には制限は掛かりません。  
古いMCバージョンで**NMSに対応したプラットフォーム**以外を利用した場合に制限が掛かります。  
※**対応プラットフォームの追加**は[`config.yml`](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/resources/config.yml)の[`Platforms`](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/resources/config.yml#L27)で行えます。  
――――――――――――――――――――――――――――――――――  
**★以下機能制限の一覧★**  
・アドベンチャーモードの左クリック判定の制限 **[v1.9-1.13.1]**  
　- 判定が本来のクリック判定ではなく疑似的に再現した判定になります。  
・オプション **`@actionbar`** の制限 **[v1.9-1.11]**  
　- BukkitAPIに実装されていないため利用はできません。    
――――――――――――――――――――――――――――――――――  
**★一部プラグインのコマンドが実行できない問題★**  
・**v2.0.0**にて修正いたしました。  
――――――――――――――――――――――――――――――――――  

プラグイン記事
-----------
**[[Github, JP-オプション、コマンドの詳細]](https://github.com/yuttyann/ScriptBlockPlus/wiki#%E4%B8%80%E8%88%AC%E3%81%AE%E6%96%B9%E5%90%91%E3%81%91)**  
**[[MCPoteton, JP-使い方と機能の紹介]](https://mcpoteton.com/mcplugin-scriptblockplus)**  
**[[SpigotMC, EN-Explanation of the plugin]](https://www.spigotmc.org/resources/1-9-1-15-2-scriptblockplus.78413/)**  
**[[MCBBS, ZH-插件说明]](https://www.mcbbs.net/thread-691900-1-1.html)**  
――――――――――――――――――――――――――――――――――  
**★過去の"README.md"の一覧★**  
**[[Readme.md, History]](https://github.com/yuttyann/ScriptBlockPlus/commits/master/README.md)**  
――――――――――――――――――――――――――――――――――  