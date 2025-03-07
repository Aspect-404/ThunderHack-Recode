package thunder.hack.gui.mainmenu;

import net.minecraft.client.gui.DrawContext;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.modules.client.HudEditor;
import thunder.hack.utility.render.Render2DEngine;

import java.awt.*;

import static thunder.hack.modules.Module.mc;

public class MainMenuButton {

    private float posX, posY, width, height;
    private final String name;
    private final Runnable action;

    public MainMenuButton(float posx, float posY, String name, Runnable action) {
        this.name = name;
        this.posX = posx;
        this.posY = posY;

        this.action = action;

        this.width = name.equals("EXIT") ? 222f : 107f;
        this.height = 38f;
    }

    public void onRender(DrawContext context, float mouseX, float mouseY) {
        Color c1 = HudEditor.getColor(270);
        Color c2 = HudEditor.getColor(0);
        Color c3 = HudEditor.getColor(180);
        Color c4 = HudEditor.getColor(90);

        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;

        if (Render2DEngine.isHovered(mouseX, mouseY,halfOfWidth +  posX, halfOfHeight +  posY, width, height)) {
            Render2DEngine.drawGradientGlow(context.getMatrices(), c1, c2, c3, c4, halfOfWidth + posX, halfOfHeight + posY, width, height, 10, 10);
        }


        Render2DEngine.drawGradientRoundShader(context.getMatrices(), c1, c2, c3, c4, halfOfWidth + posX, halfOfHeight + posY, width, height, 10);
        Render2DEngine.drawRoundShader(context.getMatrices(), halfOfWidth + posX + 1, halfOfHeight + posY + 1, width - 2, height - 2, 10, HudEditor.plateColor.getValue().getColorObject());
    }

    public void onRenderText(DrawContext context, float mouseX, float mouseY) {
        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;
        boolean hovered = Render2DEngine.isHovered(mouseX, mouseY,halfOfWidth +  posX, halfOfHeight +  posY, width, height);
        FontRenderers.monsterrat.drawCenteredString(context.getMatrices(), name,halfOfWidth +  posX + width / 2f, halfOfHeight + posY + height / 2f - 5f, hovered ? -1 : Render2DEngine.applyOpacity(-1, 0.7f));
    }


    public void onClick(int mouseX, int mouseY) {

        float halfOfWidth = mc.getWindow().getScaledWidth() / 2f;
        float halfOfHeight = mc.getWindow().getScaledHeight() / 2f;
        boolean hovered = Render2DEngine.isHovered(mouseX, mouseY,halfOfWidth +  posX, halfOfHeight +  posY, width, height);

        if (hovered)
            action.run();
    }
}
