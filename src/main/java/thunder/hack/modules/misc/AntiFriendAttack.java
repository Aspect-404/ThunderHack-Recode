package thunder.hack.modules.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.modules.Module;

import static thunder.hack.modules.combat.Criticals.getEntity;

public class AntiFriendAttack extends Module {
    public AntiFriendAttack() {
        super("AntiFriendAttack", Category.PLAYER);
    }

    @EventHandler
    public void onPacketSend(PacketEvent.@NotNull Send e) {
        if (e.getPacket() instanceof PlayerInteractEntityC2SPacket pac) {
            Entity entity = getEntity(pac);
            if (entity == null) return;
            if (ThunderHack.friendManager.isFriend(entity.getName().getString())) e.cancel();
        }
    }
}
