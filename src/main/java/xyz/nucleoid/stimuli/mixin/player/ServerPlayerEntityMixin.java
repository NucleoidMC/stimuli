package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        var player = (ServerPlayerEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerDeathEvent.EVENT).onDeath(player, source);
            if (result == ActionResult.FAIL) {
                if (player.getHealth() <= 0.0F) {
                    player.setHealth(player.getMaxHealth());
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        var player = (ServerPlayerEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerDamageEvent.EVENT).onDamage(player, source, amount);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
