package xyz.nucleoid.stimuli.mixin.player;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerConsumeHungerEvent;
import xyz.nucleoid.stimuli.event.player.PlayerRegenerateEvent;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @Shadow private int foodLevel;
    @Shadow private float exhaustionLevel;
    @Shadow private float saturationLevel;

    @Inject(method = "tick", at = @At("HEAD"))
    private void update(ServerPlayer player, CallbackInfo ci) {
        if (this.exhaustionLevel > 4.0F) {
            try (var invokers = Stimuli.select().forEntity(player)) {
                var result = invokers.get(PlayerConsumeHungerEvent.EVENT)
                        .onConsumeHunger((ServerPlayer) player, this.foodLevel, this.saturationLevel, this.exhaustionLevel);

                if (result == EventResult.DENY) {
                    this.exhaustionLevel = 0.0F;
                }
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", shift = At.Shift.BEFORE, ordinal = 0), cancellable = true)
    private void attemptRegeneration(ServerPlayer player, CallbackInfo ci, @Local Difficulty difficulty, @Local boolean naturalRegeneration, @Local float amount) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate(player, amount);

            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", shift = At.Shift.BEFORE, ordinal = 1), cancellable = true)
    private void attemptSecondaryRegeneration(ServerPlayer player, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate(player, 1);

            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }
}
