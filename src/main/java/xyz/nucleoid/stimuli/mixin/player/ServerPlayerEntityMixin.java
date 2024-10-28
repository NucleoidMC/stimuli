package xyz.nucleoid.stimuli.mixin.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.item.ItemThrowEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;
import xyz.nucleoid.stimuli.event.player.PlayerRegenerateEvent;
import xyz.nucleoid.stimuli.event.player.PlayerSpectateEntityEvent;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        var player = (ServerPlayerEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerDeathEvent.EVENT).onDeath(player, source);
            if (result == EventResult.DENY) {
                if (player.getHealth() <= 0.0F) {
                    player.setHealth(player.getMaxHealth());
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        var player = (ServerPlayerEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerDamageEvent.EVENT).onDamage(player, source, amount);
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void dropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> ci) {
        var player = (ServerPlayerEntity) (Object) this;
        int slot = player.getInventory().selectedSlot;
        var stack = player.getInventory().getStack(slot);

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(ItemThrowEvent.EVENT).onThrowItem(player, slot, stack);
            if (result == EventResult.DENY) {
                player.networkHandler.sendPacket(player.getInventory().createSlotSetPacket(slot));
                ci.setReturnValue(false);
            }
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerPlayerEntity.setCameraEntity(Lnet/minecraft/entity/Entity;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onSpectateEntity(Entity target, CallbackInfo ci){
        var player = (ServerPlayerEntity) (Object) this;
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerSpectateEntityEvent.EVENT).onSpectateEntity(player, target);
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(method = "tickHunger", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;heal(F)V"))
    private void attemptPeacefulRegeneration(ServerPlayerEntity player, float amount, Operation<Boolean> original) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate((ServerPlayerEntity) player, amount);

            if (result != EventResult.DENY) {
                original.call(player, amount);
            }
        }
    }
}
