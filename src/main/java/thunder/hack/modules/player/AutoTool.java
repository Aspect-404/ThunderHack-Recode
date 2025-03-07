package thunder.hack.modules.player;

import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;
import net.minecraft.block.AirBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class AutoTool extends Module {

    public Setting<Boolean> swapBack = new Setting<>("SwapBack", true);
    public Setting<Boolean> saveItem = new Setting<>("SaveItem", true);
    public static Setting<Boolean> silent = new Setting<>("Silent", false);
    public Setting<Boolean> echestSilk = new Setting<>("EchestSilk", true);
    public static int itemIndex;
    private boolean swap;
    private long swapDelay;
    private final List<Integer> lastItem = new ArrayList<>();

    public AutoTool() {
        super("AutoTool", "Автоматом свапается на-лучший инструмент", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (mc.crosshairTarget == null || !(mc.crosshairTarget instanceof BlockHitResult)) return;

        BlockHitResult result = (BlockHitResult) mc.crosshairTarget;
        BlockPos pos = result.getBlockPos();
        if (getTool(pos) != -1 && mc.options.attackKey.isPressed()) {
            lastItem.add(mc.player.getInventory().selectedSlot);

            if (silent.getValue())
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(getTool(pos)));
            else
                mc.player.getInventory().selectedSlot = getTool(pos);

            itemIndex = getTool(pos);
            swap = true;

            swapDelay = System.currentTimeMillis();

        } else if (swap && !lastItem.isEmpty() && System.currentTimeMillis() >= swapDelay + 300 && swapBack.getValue()) {

            if (silent.getValue())
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(lastItem.get(0)));
            else
                mc.player.getInventory().selectedSlot = lastItem.get(0);

            itemIndex = lastItem.get(0);
            lastItem.clear();
            swap = false;
        }
    }


    private int getTool(final BlockPos pos) {
        int index = -1;
        float CurrentFastest = 1.0f;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != ItemStack.EMPTY) {
                if (!(mc.player.getInventory().getStack(i).getMaxDamage() - mc.player.getInventory().getStack(i).getDamage() > 10) && saveItem.getValue())
                    continue;

                final float digSpeed = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getMiningSpeedMultiplier(mc.world.getBlockState(pos));

                if (mc.world.getBlockState(pos).getBlock() instanceof AirBlock) return -1;
                if (mc.world.getBlockState(pos).getBlock() instanceof EnderChestBlock && echestSilk.getValue()) {
                    if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0 && digSpeed + destroySpeed > CurrentFastest) {
                        CurrentFastest = digSpeed + destroySpeed;
                        index = i;
                    }
                } else if (digSpeed + destroySpeed > CurrentFastest) {
                    CurrentFastest = digSpeed + destroySpeed;
                    index = i;
                }
            }
        }
        return index;
    }
}