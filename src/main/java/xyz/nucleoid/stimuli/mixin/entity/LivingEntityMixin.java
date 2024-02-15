package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityActivateTotemEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDamageEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDeathEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDropItemsEvent;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        if (this.getWorld().isClient) {
            return;
        }

        var entity = (LivingEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(entity)) {
            var result = invokers.get(EntityDamageEvent.EVENT).onDamage(entity, source, amount);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void callDeathListener(DamageSource source, CallbackInfo ci) {
        if (this.getWorld().isClient) {
            return;
        }

        var entity = (LivingEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(entity)) {
            var result = invokers.get(EntityDeathEvent.EVENT).onDeath(entity, source);

            // cancel death if FAIL was returned from any listener
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;JLjava/util/function/Consumer;)V"))
    private void modifyDroppedLoot(LootTable instance, LootContextParameterSet parameters, long seed, Consumer<ItemStack> lootConsumer, Operation<Void> original) {
        if (this.getWorld().isClient) {
            original.call(instance, parameters, seed, lootConsumer);
            return;
        }

        try (var invokers = Stimuli.select().forEntity(this)) {
            var droppedStacks = instance.generateLoot(parameters, seed);

            var result = invokers.get(EntityDropItemsEvent.EVENT)
                    .onDropItems((LivingEntity) (Object) this, droppedStacks);

            if (result.getResult() != ActionResult.FAIL) {
                result.getValue().forEach(lootConsumer);
            }
        }
    }

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"), cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) ItemStack itemStack) {
        var entity = (LivingEntity) (Object) this;
        if (!this.getWorld().isClient()) {
            try (var invokers = Stimuli.select().forEntity(entity)) {
                var result = invokers.get(EntityActivateTotemEvent.EVENT).onTotemActivate(entity, source, itemStack);
                if (result == ActionResult.FAIL) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
