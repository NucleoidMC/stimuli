package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.entity.EntityActivateDeathProtectionEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDamageEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDeathEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDropItemsEvent;

import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        var entity = (LivingEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(entity)) {
            var result = invokers.get(EntityDamageEvent.EVENT).onDamage(entity, source, amount);
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void callDeathListener(DamageSource source, CallbackInfo ci) {
        if (this.level().isClientSide()) {
            return;
        }

        var entity = (LivingEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(entity)) {
            var result = invokers.get(EntityDeathEvent.EVENT).onDeath(entity, source);

            // cancel death if DENY was returned from any listener
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"))
    private void modifyDroppedLoot(LootTable instance, LootParams parameters, long seed, Consumer<ItemStack> lootConsumer, Operation<Void> original) {
        if (this.level().isClientSide()) {
            original.call(instance, parameters, seed, lootConsumer);
            return;
        }

        try (var invokers = Stimuli.select().forEntity(this)) {
            var droppedStacks = instance.getRandomItems(parameters, seed);

            var result = invokers.get(EntityDropItemsEvent.EVENT)
                    .onDropItems((LivingEntity) (Object) this, droppedStacks);

            result.dropStacks().forEach(lootConsumer);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), cancellable = true)
    private void tryUseDeathProtector(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) ItemStack itemStack) {
        if (this.level().isClientSide()) {
            return;
        }

        var entity = (LivingEntity) (Object) this;
        try (var invokers = Stimuli.select().forEntity(entity)) {
            var result = invokers.get(EntityActivateDeathProtectionEvent.EVENT).onDeathProtectionActivate(entity, source, itemStack);
            if (result == EventResult.DENY) {
                cir.setReturnValue(false);
            }
        }
    }
}
