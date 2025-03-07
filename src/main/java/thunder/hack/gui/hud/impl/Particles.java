package thunder.hack.gui.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import thunder.hack.utility.render.Render2DEngine;

import java.awt.*;

import static thunder.hack.modules.Module.mc;

public class Particles {
    public double x, y, deltaX, deltaY, size, opacity;
    public Color color;

    private final Identifier star = new Identifier("textures/star.png");


    public static Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }


    public void render2D(MatrixStack matrixStack) {
        drawStar(matrixStack, (float) x, (float) y, color);
    }

    public void drawStar(MatrixStack matrices, float x, float y, Color c) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, star);
        RenderSystem.setShaderColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, (float) (opacity / 255f));
        Render2DEngine.renderTexture(matrices, x, y, size, size, 0, 0, 256, 256, 256, 256);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public void updatePosition() {
        x += deltaX * 2;
        y += deltaY * 2;
        deltaY *= 0.95;
        deltaX *= 0.95;
        opacity -= 2f;
        size /= 1.1;
        if (opacity < 1) opacity = 1;
    }

    public void init(final double x, final double y, final double deltaX, final double deltaY, final double size, final Color color) {
        this.x = x;
        this.y = y;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.size = size;
        this.opacity = 254;
        this.color = color;
    }
}