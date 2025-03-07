package thunder.hack.gui.thundergui.components;



import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.thundergui.ThunderGui2;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Parent;
import thunder.hack.utility.render.Render2DEngine;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class ParentComponent extends SettingElement {


    public ParentComponent(Setting setting) {
        super(setting);
        Parent parent = (Parent) setting.getValue();
        parent.setExtended(true);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack,mouseX, mouseY, partialTicks);
        if ((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY) {
            return;
        }
        FontRenderers.modules.drawCenteredString(stack,getSetting().getName(), (float) (getX() + width / 2f), (float) getY() + 2, new Color(0xB0FFFFFF, true).getRGB());
        Render2DEngine.draw2DGradientRect(stack,(float) (getX() + 10), (float) (getY() + 6), (float) ((getX() + width / 2f) - 20), (float) (getY() + 7), new Color(0x0FFFFFF, true), new Color(0x0FFFFFF, true), new Color(0xB0FFFFFF, true), new Color(0xB0FFFFFF, true));
        Render2DEngine.draw2DGradientRect(stack,(float) (getX() + width / 2f + 20f), (float) (getY() + 6), (float) (getX() + width - 10), (float) (getY() + 7), new Color(0xB0FFFFFF, true), new Color(0xB0FFFFFF, true), new Color(0x0FFFFFF, true), new Color(0x0FFFFFF, true));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if ((getY() > ThunderGui2.getInstance().main_posY + ThunderGui2.getInstance().height) || getY() < ThunderGui2.getInstance().main_posY) {
            return;
        }
        if (hovered) {
            Parent parent = (Parent) setting.getValue();
            parent.setExtended(!parent.isExtended());
        }
    }

}
