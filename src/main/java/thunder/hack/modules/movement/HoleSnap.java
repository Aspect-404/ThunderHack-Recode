package thunder.hack.modules.movement;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import thunder.hack.events.impl.EventKeyboardInput;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.EventPlayerJump;
import thunder.hack.events.impl.EventPlayerTravel;
import thunder.hack.modules.Module;
import thunder.hack.modules.client.MainSettings;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.InteractionUtility;
import thunder.hack.utility.world.HoleUtility;

import java.util.ArrayList;

public class HoleSnap extends Module {
    private final Setting<Mode> mode = new Setting<>("Mode", Mode.Normal);
    private final Setting<Boolean> stopOverHole = new Setting<>("StopOverHole", true);

    private enum Mode {
        Normal,
        OnMove
    }

    private BlockPos hole;
    private float prevClientYaw;

    public HoleSnap() {
        super("HoleSnap", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        hole = findHole();
    }

    @Override
    public void onUpdate() {
        if (mc.player != null && mc.player.horizontalCollision && mc.player.isOnGround())
            mc.player.jump();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(EventMove e) {
        BlockPos bp = BlockPos.ofFloored(mc.player.getPos());
        if (HoleUtility.isHole(bp.down()) || HoleUtility.isHole(bp.down(2)) || HoleUtility.isHole(bp.down(3))) {
            Vec3d center = new Vec3d(Math.floor(mc.player.getX()) + 0.5, mc.player.getY(), Math.floor(mc.player.getZ()) + 0.5);
            if (center.distanceTo(mc.player.getPos()) < 0.15f) {
                e.set_x(0);
                e.set_z(0);
                e.cancel();
            }
        }
    }

    @EventHandler
    public void modifyVelocity(EventPlayerTravel e) {
        if (mc.player == null) return;
        if (mc.player.age % 10 == 0) hole = findHole();

        if (hole != null) {
            if (e.isPre()) {
                prevClientYaw = mc.player.getYaw();
                mc.player.setYaw(InteractionUtility.calculateAngle(hole.toCenterPos())[0]);
            } else {
                mc.player.setYaw(prevClientYaw);
            }
        }
    }

    @EventHandler
    public void modifyJump(EventPlayerJump e) {
        if (mc.player == null) return;
        if (hole != null) {
            if (e.isPre()) {
                prevClientYaw = mc.player.getYaw();
                mc.player.setYaw(InteractionUtility.calculateAngle(hole.toCenterPos())[0]);
            } else {
                mc.player.setYaw(prevClientYaw);
            }
        }
    }

    @EventHandler
    public void onKeyboardInput(EventKeyboardInput e) {
        if (mc.player == null || mode.getValue() != Mode.Normal) return;
        mc.player.input.movementForward = 1;
    }

    private @Nullable BlockPos findHole() {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        BlockPos centerPos = mc.player.getBlockPos();
        for (int i = centerPos.getX() - 3; i < centerPos.getX() + 3; i++) {
            for (int j = centerPos.getY() - 4; j < centerPos.getY() + 2; j++) {
                for (int k = centerPos.getZ() - 3; k < centerPos.getZ() + 3; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (HoleUtility.isHole(pos)) {
                        blocks.add(new BlockPos(pos));
                    }
                }
            }
        }

        float nearestDistance = 10;
        BlockPos fbp = null;
        for (BlockPos bp : blocks) {
            if (BlockPos.ofFloored(mc.player.getPos()).equals(bp)) {
                disable(MainSettings.isRu() ? "Ты в холке! Отключаю.." : "You're in the hole! Disabling..");
                return null;
            }
            if (mc.player.squaredDistanceTo(bp.toCenterPos()) < nearestDistance) {
                nearestDistance = (float) mc.player.squaredDistanceTo(bp.toCenterPos());
                fbp = bp;
            }
        }

        return fbp;
    }
}
