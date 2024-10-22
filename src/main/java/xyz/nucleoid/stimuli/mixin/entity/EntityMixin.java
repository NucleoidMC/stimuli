package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityLeashEvent;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(
            method = "interact",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Leashable;canLeashAttachTo()Z"
            )
    )
    private static boolean onLeashAttach(Leashable leashable, Operation<Boolean> original, @Local(argsOnly = true) PlayerEntity player, @Local(argsOnly = true) Hand hand) {
        if (!original.call(leashable)) {
            return false;
        }

        // Leashable instance is the same as this entity
        var entity = (Entity) leashable;

        if (!entity.getWorld().isClient()) {
            var serverPlayer = (ServerPlayerEntity) player;

            var events = Stimuli.select();

            try (var invokers = events.forEntity(serverPlayer)) {
                var result = invokers.get(EntityLeashEvent.ATTACH).onAttachLeash(entity, serverPlayer, null, serverPlayer, hand);
                if (result == ActionResult.FAIL) {
                    var stack = player.getStackInHand(hand);

                    // notify the client that this action did not go through
                    int slot = hand == Hand.MAIN_HAND ? serverPlayer.getInventory().selectedSlot : 40;
                    serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, slot, stack));

                    return false;
                }
            }
        }

        return true;
    }
}
