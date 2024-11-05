package xyz.nucleoid.stimuli.mixin.player;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerConsumeHungerEvent;
import xyz.nucleoid.stimuli.event.player.PlayerRegenerateEvent;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow private int foodLevel;
    @Shadow private float exhaustion;
    @Shadow private float saturationLevel;

    @Inject(method = "update", at = @At("HEAD"))
    private void update(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.exhaustion > 4.0F) {
            try (var invokers = Stimuli.select().forEntity(player)) {
                var result = invokers.get(PlayerConsumeHungerEvent.EVENT)
                        .onConsumeHunger((ServerPlayerEntity) player, this.foodLevel, this.saturationLevel, this.exhaustion);

                if (result == EventResult.DENY) {
                    this.exhaustion = 0.0F;
                }
            }
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;heal(F)V", shift = At.Shift.BEFORE, ordinal = 0), cancellable = true)
    private void attemptRegeneration(ServerPlayerEntity player, CallbackInfo ci, @Local Difficulty difficulty, @Local boolean naturalRegeneration, @Local float amount) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate(player, amount);

            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;heal(F)V", shift = At.Shift.BEFORE, ordinal = 1), cancellable = true)
    private void attemptSecondaryRegeneration(ServerPlayerEntity player, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate(player, 1);

            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }
}
