package com.github.yuttyann.scriptblockplus.hook.nms;

/**
 * 26.2の子供用SulfurCubeモデルを、ブロックの輪郭表示に合わせる補正値。
 */
final class SulfurCubeGlowAppearance {

    static final int SIZE = 2;

    // 子供モデルの一辺は10/16ブロック。サイズ2をSCALE属性で約1ブロックにします。
    static final double SCALE = 0.8D;

    // LivingEntityRendererの反転・移動とSulfurCubeRendererの子供用移動を相殺します。
    // 表示の中心 = EntityY + SCALE * 0.999 * (SIZE * (1.501 - 1.24) - 0.001)
    static final double Y_OFFSET = 0.5D - SCALE * 0.999D * (SIZE * (1.501D - 1.24D) - 0.001D);

    private SulfurCubeGlowAppearance() { }
}
