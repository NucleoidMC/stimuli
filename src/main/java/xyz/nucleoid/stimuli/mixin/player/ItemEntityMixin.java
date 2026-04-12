package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.item.ItemPickupEvent;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "playerTouch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onPlayerCollision(Player player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(player, this.blockPosition())) {
            var itemEntity = (ItemEntity) (Object) this;
            var result = invokers.get(ItemPickupEvent.EVENT)
                    .onPickupItem((ServerPlayer) player, itemEntity, itemEntity.getItem());

            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }
}
