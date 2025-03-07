package thunder.hack.injection;

import thunder.hack.ThunderHack;
import thunder.hack.events.impl.EventMouse;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static thunder.hack.modules.Module.mc;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void onMouseButtonHook(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == mc.getWindow().getHandle()) {
            if(action == 1)
                ThunderHack.moduleManager.onMoseKeyPressed(button);
            if(action == 0)
                ThunderHack.moduleManager.onMoseKeyReleased(button);

            ThunderHack.EVENT_BUS.post(new EventMouse(button,action));
        }
    }

}