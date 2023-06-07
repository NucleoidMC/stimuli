package xyz.nucleoid.stimuli.mixin.entity;

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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
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

    @Redirect(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;JLjava/util/function/Consumer;)V"))
    private void modifyDroppedLoot(LootTable lootTable, LootContextParameterSet parameters, long seed, Consumer<ItemStack> lootConsumer) {
        if (this.getWorld().isClient) {
            lootTable.generateLoot(parameters, lootConsumer);
            return;
        }

        try (var invokers = Stimuli.select().forEntity(this)) {
            var droppedStacks = lootTable.generateLoot(parameters, seed);

            var result = invokers.get(EntityDropItemsEvent.EVENT)
                    .onDropItems((LivingEntity) (Object) this, droppedStacks);

            if (result.getResult() != ActionResult.FAIL) {
                result.getValue().forEach(this::dropStack);
            }
        }
    }
}
