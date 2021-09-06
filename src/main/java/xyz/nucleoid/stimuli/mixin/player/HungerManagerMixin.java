package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerConsumeHungerEvent;
import xyz.nucleoid.stimuli.event.player.PlayerRegenerateEvent;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow private int foodLevel;
    @Shadow private float foodSaturationLevel;
    @Shadow private float exhaustion;

    @Inject(method = "update", at = @At("HEAD"))
    private void update(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        if (this.exhaustion > 4.0F) {
            try (var invokers = Stimuli.select().forEntity(player)) {
                var result = invokers.get(PlayerConsumeHungerEvent.EVENT)
                        .onConsumeHunger((ServerPlayerEntity) player, this.foodLevel, this.foodSaturationLevel, this.exhaustion);

                if (result == ActionResult.FAIL) {
                    this.exhaustion = 0.0F;
                }
            }
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V", shift = At.Shift.BEFORE, ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void attemptRegeneration(PlayerEntity player, CallbackInfo ci, Difficulty difficulty, boolean naturalRegeneration, float amount) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate((ServerPlayerEntity) player, amount);

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V", shift = At.Shift.BEFORE, ordinal = 1), cancellable = true)
    private void attemptSecondaryRegeneration(PlayerEntity player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate((ServerPlayerEntity) player, 1);

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
