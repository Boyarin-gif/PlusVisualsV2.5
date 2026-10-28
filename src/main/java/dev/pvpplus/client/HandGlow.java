package dev.pvpplus.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

/**
 * Свечение предметов в руках. Рисуется на экране кольцевым градиентом вокруг области, где в первом лице
 * находится предмет (центр оставлен прозрачным, чтобы не закрывать текстуру предмета).
 * Это экранный эффект, а не контур 3D-модели.
 */
public final class HandGlow {
    public static void render(DrawContext ctx, RenderTickCounter tick) {
        if (!Config.on("itemglow")) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || !mc.options.getPerspective().isFirstPerson()) return;
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        boolean rightMain = mc.options.getMainArm().getValue() == Arm.RIGHT;

        hand(ctx, w, h, mc.player.getMainHandStack(), Config.b("itemglow.main"), rightMain,
                Config.color("itemglow.color_main"), Config.i("itemglow.power_main"));
        hand(ctx, w, h, mc.player.getOffHandStack(), Config.b("itemglow.off"), !rightMain,
                Config.color("itemglow.color_off"), Config.i("itemglow.power_off"));
    }

    private static void hand(DrawContext ctx, int w, int h, ItemStack st, boolean enabled, boolean rightSide, int color, int power) {
        if (!enabled || st.isEmpty()) return;
        int cx = w / 2 + (rightSide ? 1 : -1) * (int) (w * 0.22);
        int cy = (int) (h * 0.80);
        int radius = Math.max(18, h / 8);
        int layers = Optimization.glowLayers();
        int rin = radius * 45 / 100;
        int a = Math.max(8, (int) (power * 1.6 / layers));
        int col = Gfx.alpha(color, a);
        for (int l = 0; l < layers; l++) {
            int ro = radius - l * (radius - rin - 2) / layers;
            for (int dy = -ro; dy <= ro; dy++) {
                int wo = (int) Math.sqrt((double) ro * ro - (double) dy * dy);
                if (Math.abs(dy) < rin) {
                    int wi = (int) Math.sqrt((double) rin * rin - (double) dy * dy);
                    ctx.fill(cx - wo, cy + dy, cx - wi, cy + dy + 1, col);
                    ctx.fill(cx + wi, cy + dy, cx + wo, cy + dy + 1, col);
                } else {
                    ctx.fill(cx - wo, cy + dy, cx + wo, cy + dy + 1, col);
                }
            }
        }
    }
}
