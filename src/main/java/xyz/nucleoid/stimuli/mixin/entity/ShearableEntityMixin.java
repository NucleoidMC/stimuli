package xyz.nucleoid.stimuli.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityShearEvent;

@Mixin(value = {
    MooshroomEntity.class,
    SheepEntity.class,
    SnowGolemEntity.class
})
public class ShearableEntityMixin {
    @WrapOperation(
            method = "interactMob",
            at = {
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/passive/MooshroomEntity;isShearable()Z"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/passive/SheepEntity;isShearable()Z"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/passive/SnowGolemEntity;isShearable()Z"
                    )
            }
    )
    private boolean onEntityShear(@Coerce Shearable shearable, Operation<Boolean> original, PlayerEntity player, Hand hand) {
        if (!original.call(shearable)) {
            return false;
        }

        if (!player.getWorld().isClient()) {
            // Entities are all subclasses of LivingEntity
            var entity = (LivingEntity) shearable;
            var serverPlayer = (ServerPlayerEntity) player;

            var events = Stimuli.select();

            try (var invokers = events.forEntity(entity)) {
                var result = invokers.get(EntityShearEvent.EVENT).onShearEntity(entity, serverPlayer, hand, null);
                if (result == ActionResult.FAIL) {
                    return false;
                }
            }
        }

        return true;
    }
}
