ScriptBlockPlus [Java8 MC1.9-1.15.2]
==========
概要
--------------------------------------------------
[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の機能を引き継ぎ、新機能追加や改善を施したプラグインです。  
また、開発者向けに[API](https://github.com/yuttyann/ScriptBlockPlus/wiki/API%E3%81%AE%E5%8F%96%E5%BE%97%E6%96%B9%E6%B3%95)(今後WIKIを書き直します。)も公開しています。

導入
-----------
[Releases](https://github.com/yuttyann/ScriptBlockPlus/releases)または[Yuttyann Files](https://file.yuttyann44581.net/)からダウンロードを行ってください。  
その後前提プラグインである[Vault](https://dev.bukkit.org/projects/vault)をダウンロードし、`plugins`フォルダへ保存すれば完了です。

対応プラットフォーム
-----------
一部プラットフォームでの動作は確認できませんが、`BukkitAPIを実装しているのであれば基本的に動作します。`  
**以下動作を確認したプラットフォームの一覧です。**
- [CraftBukkit](https://www.spigotmc.org/)
- [Spigot](https://www.spigotmc.org/)
- [PaperMC](https://papermc.io/)
- [CatServer](http://catserver.moe/)

### 機能制限
※最新のMCバージョンを使用している場合には(セレクターの機能以外)制限は掛かりません。  
古いMCバージョンで`CraftBukkit`、`Spigot`、`PaperMC`以外のプラットフォームを使用した場合に制限が掛かります。  
(今後どのプラットフォームで動作させるのかを決めるフィルター機能を`YAML`形式で実装する予定です。)  
**以下制限一覧です。**
- アドベンチャーモードの左クリック判定の制限[v1.9-1.13.1]
  - 判定が本来のクリック判定ではなく疑似的に再現した判定になります。
- オプション`@actionbar`の制限[v1.9-1.11]
  - BukkitAPIに実装されていないため利用はできません。
- コマンドのセレクターの制限[ALL]
  - NMSに依存しているためセレクターの利用はできません。

プラグイン記事
-----------
**[MCPoteton]** [【プラグイン紹介】ScriptBlockPlus【～1.15.2対応】](https://mcpoteton.com/mcplugin-scriptblockplus) **[日本語 - 使い方や機能の紹介]**  
**[Github]** [記事一覧](https://github.com/yuttyann/ScriptBlockPlus/wiki#%E4%B8%80%E8%88%AC%E3%81%AE%E6%96%B9%E5%90%91%E3%81%91) **[日本語 - API、オプション、コマンドの詳細等]**  
**[MCBBS]** [フォーラムの紹介ページ](https://mcpoteton.com/mcplugin-scriptblockplus) **[中国語 - 使い方や機能の紹介]**