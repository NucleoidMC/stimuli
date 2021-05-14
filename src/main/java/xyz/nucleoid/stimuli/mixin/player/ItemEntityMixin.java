package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.ItemEvents;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "onPlayerCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        try (EventInvokers invokers = Stimuli.select().forEntityAt(player, this.getBlockPos())) {
            ItemEntity itemEntity = (ItemEntity) (Object) this;
            ActionResult result = invokers.get(ItemEvents.PICKUP)
                    .onPickupItem((ServerPlayerEntity) player, itemEntity, itemEntity.getStack());

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
