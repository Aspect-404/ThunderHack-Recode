package thunder.hack.modules.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import thunder.hack.ThunderHack;
import thunder.hack.core.ModuleManager;
import thunder.hack.events.impl.EventSync;
import thunder.hack.modules.Module;
import thunder.hack.modules.combat.Aura;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.InventoryUtility;
import thunder.hack.utility.Timer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import thunder.hack.utility.player.PlayerUtility;

public class MiddleClick extends Module {
    private final Setting<Boolean> friend = new Setting<>("Friend", true);
    private final Setting<Boolean> ep = new Setting<>("Pearl", true);
    private final Setting<Boolean> silentPearl = new Setting<>("SilentPearl", true, v -> ep.getValue());
    private final Setting<Boolean> inventoryPearl = new Setting<>("InventoryPearl", true, v -> ep.getValue());
    private final Setting<Integer> swapDelay = new Setting<>("SwapDelay", 100, 0, 1000, v -> !silentPearl.getValue());
    private final Setting<Boolean> xp = new Setting<>("XP", false);
    private final Setting<Boolean> noWasteXp = new Setting<>("Anti Waste", true, v -> xp.getValue());
    private final Setting<Integer> durability = new Setting<>("Stop On", 90, v -> xp.getValue());
    private final Setting<Boolean> feetExp = new Setting<>("FeetXP", false, v -> xp.getValue());
    private final Setting<Boolean> silent = new Setting<>("SilentXP", true, v -> xp.getValue());

    private final Timer timer = new Timer();
    private int lastSlot = -1;


    public MiddleClick() {
        super("MiddleClick", "действия на колесико-мыши", Category.MISC);
    }

    @Override
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder();

        if (friend.getValue()) {
            sb.append("FR");
        }
        if (xp.getValue()) {
            sb.append(" XP ");
        }
        if (ep.getValue()) {
            sb.append(" EP ");
        }

        return sb.toString();
    }


    @EventHandler
    public void onPreMotion(EventSync event) {
        if (fullNullCheck()) return;

        if (xp.getValue() && feetExp.getValue() && mc.options.pickItemKey.isPressed()) {
            mc.player.setPitch(90);
        }

        if (friend.getValue() && mc.currentScreen == null && mc.options.pickItemKey.isPressed() && mc.targetedEntity != null && mc.targetedEntity instanceof PlayerEntity entity && timer.passedMs(800)) {
            if (ThunderHack.friendManager.isFriend(entity.getName().getString())) {
                ThunderHack.friendManager.removeFriend(entity.getName().getString());
                sendMessage("§b" + entity.getName().getString() + "§r удален из друзей!");
            } else {
                ThunderHack.friendManager.addFriend(entity.getName().getString());
                sendMessage("Добавлен друг §b" + entity.getName().getString());
            }
            timer.reset();
            return;
        }

        if (ep.getValue() && timer.passedMs(500) && mc.currentScreen == null && mc.options.pickItemKey.isPressed()) {

            if (ModuleManager.aura.isEnabled() && Aura.target != null) {
                sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
            }

            if (silentPearl.getValue()) {
                if (!inventoryPearl.getValue() || (inventoryPearl.getValue() && findEPSlot() != -1)) {
                    int epSlot = findEPSlot();
                    int originalSlot = mc.player.getInventory().selectedSlot;
                    if (epSlot != -1) {
                        mc.player.getInventory().selectedSlot = epSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(epSlot));
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                        mc.player.getInventory().selectedSlot = originalSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
                    }
                } else {
                    int epSlot = InventoryUtility.findItemInInventory(Items.ENDER_PEARL).slot();
                    if (epSlot != -1) {
                        sendPacket(new PickFromInventoryC2SPacket(epSlot));
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                        sendPacket(new PickFromInventoryC2SPacket(epSlot));
                    }
                }
            } else {
                if (!inventoryPearl.getValue() || (inventoryPearl.getValue() && findEPSlot() != -1)) {
                    int epSlot = findEPSlot();
                    int originalSlot = mc.player.getInventory().selectedSlot;
                    if (epSlot != -1) {
                        new PearlThread(mc.player, epSlot, originalSlot, swapDelay.getValue(), false).start();
                    }
                } else {
                    int epSlot = InventoryUtility.findItemInInventory(Items.ENDER_PEARL).slot();
                    int currentItem = mc.player.getInventory().selectedSlot;
                    if (epSlot != -1) {
                        sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                        new PearlThread(mc.player, epSlot, currentItem, swapDelay.getValue(), true).start();
                    }
                }
            }
            timer.reset();
        }

        if (xp.getValue()) {
            if (noWasteXp.getValue() && !needXp()) return;
            if (mc.options.pickItemKey.isPressed()) {
                int slot = findXPSlot();
                if (slot != -1) {
                    int lastSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = slot;
                    sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    if (silent.getValue()) {
                        mc.player.getInventory().selectedSlot = lastSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
                    }
                } else if (lastSlot != -1) {
                    mc.player.getInventory().selectedSlot = lastSlot;
                    sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
                    lastSlot = -1;
                }
            } else if (lastSlot != -1) {
                mc.player.getInventory().selectedSlot = lastSlot;
                sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
                lastSlot = -1;
            }
        }
    }

    private boolean needXp() {
        if (mc.player == null) return false;

        for (ItemStack stack : mc.player.getArmorItems()) {
            if (PlayerUtility.calculatePercentage(stack) < durability.getValue()) {
                return true;
            }
        }

        return false;
    }

    private int findEPSlot() {
        int epSlot = -1;
        if (mc.player.getMainHandStack().getItem() == Items.ENDER_PEARL) {
            epSlot = mc.player.getInventory().selectedSlot;
        }
        if (epSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.getInventory().getStack(l).getItem() == Items.ENDER_PEARL) {
                    epSlot = l;
                    break;
                }
            }
        }
        return epSlot;
    }

    private int findXPSlot() {
        int epSlot = -1;
        if (mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            epSlot = mc.player.getInventory().selectedSlot;
        }
        if (epSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.getInventory().getStack(l).getItem() == Items.EXPERIENCE_BOTTLE) {
                    epSlot = l;
                    break;
                }
            }
        }
        return epSlot;
    }

    public class PearlThread extends Thread {
        public ClientPlayerEntity player;
        int epSlot, originalSlot, delay;
        boolean inv;

        public PearlThread(ClientPlayerEntity entityPlayer, int epSlot, int originalSlot, int delay, boolean inventory) {
            this.player = entityPlayer;
            this.epSlot = epSlot;
            this.originalSlot = originalSlot;
            this.delay = delay;
            inv = inventory;
        }


        @Override
        public void run() {
            if (!inv) {
                mc.player.getInventory().selectedSlot = epSlot;
                sendPacket(new UpdateSelectedSlotC2SPacket(epSlot));

                try {
                    sleep(delay);
                } catch (Exception ignored) {
                }
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                try {
                    sleep(delay);
                } catch (Exception ignored) {
                }

                mc.player.getInventory().selectedSlot = originalSlot;
                sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
                super.run();
            } else {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, epSlot, originalSlot, SlotActionType.SWAP, mc.player);
                try {
                    sleep(delay);
                } catch (Exception ignored) {
                }
                if (ModuleManager.aura.isEnabled() && Aura.target != null) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
                }
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                try {
                    sleep(delay);
                } catch (Exception ignored) {
                }
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, epSlot, originalSlot, SlotActionType.SWAP, mc.player);

                super.run();
            }
        }
    }
}
