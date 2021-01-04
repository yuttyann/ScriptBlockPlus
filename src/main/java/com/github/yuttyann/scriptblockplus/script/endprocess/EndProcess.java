package com.github.yuttyann.scriptblockplus.script.endprocess;

import com.github.yuttyann.scriptblockplus.enums.InstanceType;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.SBInstance;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptBlockPlus EndProcess インターフェース
 * @author yuttyann44581
 */
public interface EndProcess extends SBInstance<EndProcess> {

    /**
     * インスタンスを生成します。
     * @return {@link EndProcess}
     */
    @NotNull
    default EndProcess newInstance() {
        return EndProcessManager.newInstance(this.getClass(), InstanceType.REFLECTION);
    }

    /**
     * スクリプトの実行が最後まで成功した場合に呼び出されます。
     * @param sbRead {@link SBRead}
     */
    void success(@NotNull SBRead sbRead);

    /**
     * スクリプトの実行が途中で失敗した場合に呼び出されます。
     * @param sbRead {@link SBRead}
     */
    void failed(@NotNull SBRead sbRead);
}