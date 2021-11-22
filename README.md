[[Java11](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9)] ScriptBlockPlus [v2.1.7](https://github.com/yuttyann/ScriptBlockPlus/releases/tag/v2.1.7) [MC1.9-1.17.1] [![](https://jitpack.io/v/yuttyann/ScriptBlockPlus.svg)](https://jitpack.io/#yuttyann/ScriptBlockPlus)
==========

概要
-----------
[ScriptBlock](https://dev.bukkit.org/projects/scriptblock)の機能を引き継ぎ、新機能追加や改善を施したプラグインです。  
また、開発者向けに[API](https://github.com/yuttyann/ScriptBlockPlus/wiki/%5BJP%5D-API-Tutorial)を公開しています。  

導入
-----------
[ダウンロード](https://www.spigotmc.org/resources/scriptblockplus.78413/)した`ScriptBlockPlus`を、`plugins`フォルダへ保存すれば完了です。  

### Java8版
別の[リポジトリ](https://github.com/yuttyann/ScriptBlockPlus-Java8)にて、**ScriptBlockPlus**の**Java8版**を[公開](https://github.com/yuttyann/ScriptBlockPlus-Java8/releases)しています。  
古いプラットフォームのサーバーで[**Java11**](https://adoptopenjdk.net/?variant=openjdk11)が動作しない場合はご利用ください。  
但し、基本的にはサポートを行っていない事と、近いうちに更新停止することをご了承ください。  
**プラグインバージョンv2.1.2より、更新を停止いたしました。**

### 連携プラグイン
| Plugin | Description | Development |
|:---|:---|:---:|
| [Vault](https://www.spigotmc.org/resources/vault.34315/) | 権限、経済系プラグインの機能を利用する事ができます。 | **1.7.3** |
| [DiscordSRV](https://www.spigotmc.org/resources/discordsrv.18494/) | ディスコ―ドの機能を利用する事ができます。 | **1.22.0** |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | プレースホルダの機能を拡張する事ができます。 | **2.10.9** |
| [ScriptEntityPlus](https://github.com/yuttyann/ScriptEntityPlus) | エンティティにスクリプトを設定する事ができます。 | **1.1.8** |  

**== 各バージョンの動作範囲 ==**
| Plugin | Server | Java |
|:---:|:---:|:---:|
|**`2.1.5-2.1.7`**|**`1.9-1.17.1`**|**[Java11](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9)**|
|`2.1.2-2.1.4`|`1.9-1.17`|**[Java11](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9)**|
|`2.0.4-2.1.1`|`1.9-1.16.5`|**[Java11](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9)**|
|[`2.x.x-JV8`](https://github.com/yuttyann/ScriptBlockPlus-Java8)|`1.9-1.16.5`|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|`1.8.5-2.0.3`|`1.9-1.16.5`|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|`1.6.0-1.8.4`|`1.8-1.15.2`|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|`1.4.0-1.5.0`|`1.7.2-1.13.2`|**[Java8](https://adoptopenjdk.net/?variant=openjdk8)**|
|`1.0.0-1.3.3`|`1.7.2-1.13.2`|**[Java7](https://jdk.java.net/java-se-ri/7)**|  

**== バージョン"1.13-1.13.1"の動作について ==**  
サーバーが[**Java9**](https://jdk.java.net/java-se-ri/9)以降に対応していないことが原因で、**エラーが発生する不具合が存在します。**  
プラグイン自体は正常に動作するのですが、問題が発生する可能性があるのでなるべく"[**1.13.2**](https://papermc.io/legacy)"を利用してください。  
**解決方法(非推奨)**、`<Server>.jar`内の`org/objectweb/asm/ClassVisitor.class`を[改変](https://pastebin.com/UFBdKXJD)することで動作します。  

**== プラグインの不具合について ==**  
報告される不具合の多くはデータファイルや設定ファイル系が殆んどです。  
報告される際は、一度`plugins/ScriptBlockPlus/json`内の`format.sbp`を削除して再生成を行ってください。  
また、アップデートで設定ファイルが変更されることもままあるので、再生成を推奨します。  
**(データファイル、設定ファイルの再生成や削除で解決しなかった場合は[Issue](https://github.com/yuttyann/ScriptBlockPlus/issues)で報告してください。)**  
`ちなみにそもそも起動しない場合は、Javaのバージョンをチェックしてください。`

対応プラットフォーム
-----------
**[`BukkitAPI`](https://hub.spigotmc.org/javadocs/bukkit/overview-summary.html)を実装しているのであれば**基本的に動作します。  
[**`net.minecraft.server`**](https://sodocumentation.net/ja/bukkit/topic/9576/nms)が見つからなかった場合は、**NMS依存の機能が強制的に無効**になります。  
下記は、動作確認を行ったサーバーの一覧です。
| Server | Description |
|:---:|:---|
|**[Spigot](https://www.spigotmc.org/)**|可もなく不可もない、一般的に採用されているサーバーです。|
|**[Paper](https://papermc.io/)**|Spigotの派生サーバーで、最適化の他、詳細な設定の追加等も行われています。|
|**[Tuinity](https://ci.codemc.io/job/Spottedleaf/job/Tuinity/)**|Paperの派生サーバーで、大規模なサーバー向けに最適化されています。|
|**[Yatopia](https://yatopiamc.org/)**|Tuinityの派生サーバーで、様々なサーバープラットフォームの最適化パッチを適用しています。|
|**[Purpur](https://purpur.pl3x.net/)**|Tuinityの派生サーバーで、様々なユニークな機能が追加されています。|
|**[Akarin](https://github.com/Akarin-project/Akarin)**|Paperの派生サーバーで、パフォーマンスの向上を目的として作成されています。|
|**[Mohist](https://mohistmc.com/)**|ForgeとSpigot(Paper)の両方の機能を備えています。|
|**[Magma](https://magmafoundation.org/)**|ForgeとSpigot(Paper)の両方の機能を備えています。|

リンク
-----------
**[[Files, JP]](https://file.yuttyann44581.net/)**  
**[[Wiki, JP]](https://github.com/yuttyann/ScriptBlockPlus/wiki)**  
**[[Poteton, JP]](https://mcpoteton.com/mcplugin-scriptblockplus)**  
**[[SpigotMC, EN]](https://www.spigotmc.org/resources/scriptblockplus.78413/)**  
**[[MCBBS, ZH]](https://www.mcbbs.net/thread-691900-1-1.html)**