package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.item.ItemPickupEvent;

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

        try (var invokers = Stimuli.select().forEntityAt(player, this.getBlockPos())) {
            var itemEntity = (ItemEntity) (Object) this;
            var result = invokers.get(ItemPickupEvent.EVENT)
                    .onPickupItem((ServerPlayerEntity) player, itemEntity, itemEntity.getStack());

            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }
}
