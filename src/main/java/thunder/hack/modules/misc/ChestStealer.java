package thunder.hack.modules.misc;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import thunder.hack.modules.Module;
import thunder.hack.modules.combat.AutoTotem;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChestStealer extends Module {

    public ChestStealer() {
        super("ChestStealer", "ChestStealer", Category.MISC);
    }

    private final Setting<Integer> delay = new Setting<>("Delay", 100, 0, 1000);
    private final Setting<Boolean> random = new Setting<>("Random", false);
    public Setting<Sort> sort = new Setting<>("Sort", Sort.None);

    public List<String> items = new ArrayList<>();

    private final Timer timer = new Timer();
    private final Random rnd = new Random();

    private enum Sort{
        None, WhiteList, BlackList
    }

    @Override
    public void onUpdate() {
        if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            GenericContainerScreenHandler mxChest = (GenericContainerScreenHandler) mc.player.currentScreenHandler;
            for (int i = 0; i < mxChest.getInventory().size(); i++) {
                Slot slot = mxChest.getSlot(i);
                if (slot.hasStack()) {
                    if(isAllowed(slot.getStack())) {
                        if (timer.passedMs(delay.getValue() + (random.getValue() ? rnd.nextInt(delay.getValue()) : 0))) {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                            timer.reset();
                        }
                    }
                }
            }
            if (isContainerEmpty(mxChest))
                mc.player.currentScreenHandler.onClosed(mc.player);
        }
    }

    private boolean isAllowed(ItemStack stack) {
        if(sort.getValue() == Sort.None){
            return true;
        } else if(sort.getValue() == Sort.WhiteList){
            return items.contains(stack.getItem().getTranslationKey().replace("block.minecraft.","").replace("item.minecraft.",""));
        } else {
            return !items.contains(stack.getItem().getTranslationKey().replace("block.minecraft.","").replace("item.minecraft.",""));
        }
    }


    private boolean isContainerEmpty(GenericContainerScreenHandler container) {
        boolean empty = true;
        int i = 0;
        int slotAmount = container.getInventory().size() == 90 ? 54 : 27;
        while (i < slotAmount) {
            if (container.getSlot(i).hasStack()) {
                empty = false;
            }
            ++i;
        }
        return empty;
    }
}
