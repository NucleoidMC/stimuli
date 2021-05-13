package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.PlayerEvents;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
            ActionResult result = invokers.get(PlayerEvents.DEATH).onDeath(player, source);
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
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
            ActionResult result = invokers.get(PlayerEvents.DAMAGE).onDamage(player, source, amount);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
