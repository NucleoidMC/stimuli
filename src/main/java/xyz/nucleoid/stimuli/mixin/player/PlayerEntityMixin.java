package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.item.ItemThrowEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void dropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> ci) {
        if (this.world.isClient) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        int slot = player.getInventory().selectedSlot;
        ItemStack stack = player.getInventory().getStack(slot);

        try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
            ActionResult result = invokers.get(ItemThrowEvent.EVENT).onThrowItem(player, slot, stack);
            if (result == ActionResult.FAIL) {
                player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, slot, stack));
                ci.setReturnValue(false);
            }
        }
    }
}
