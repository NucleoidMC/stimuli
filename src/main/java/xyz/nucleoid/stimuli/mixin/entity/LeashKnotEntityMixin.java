package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityLeashEvent;

@Mixin(LeashKnotEntity.class)
public class LeashKnotEntityMixin {
    @WrapOperation(
            method = "interact",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Leashable;getLeashHolder()Lnet/minecraft/entity/Entity;",
                ordinal = 0
            )
    )
    private Entity onLeashAttachToKnot(Leashable leashable, Operation<Entity> original, @Local(argsOnly = true) PlayerEntity player, @Local(argsOnly = true) Hand hand, @Share("attached") LocalBooleanRef attached) {
        // Leashable is a filtered Entity instance
        var entity = (Entity) leashable;

        var leashHolder = (Entity) (Object) this;
        var currentHolder = original.call(leashable);

        // Vanilla reattaches leashes already attached to this knot, so these cases must be filtered out
        if (leashHolder != currentHolder) {
            var serverPlayer = (ServerPlayerEntity) player;

            var events = Stimuli.select();

            try (var invokers = events.forEntity(serverPlayer)) {
                var result = invokers.get(EntityLeashEvent.ATTACH).onAttachLeash(entity, leashHolder, null, serverPlayer, hand);
                if (result == ActionResult.FAIL) {
                    return null;
                }
            }
        }

        return currentHolder;
    }
}
