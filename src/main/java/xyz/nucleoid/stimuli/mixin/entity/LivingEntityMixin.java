package xyz.nucleoid.stimuli.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityDeathEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDropItemsEvent;

import java.util.List;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void callDeathListener(DamageSource source, CallbackInfo ci) {
        if (this.world.isClient) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        try (EventInvokers invokers = Stimuli.select().forEntity(entity)) {
            ActionResult result = invokers.get(EntityDeathEvent.EVENT).onDeath(entity, source);

            // cancel death if FAIL was returned from any listener
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V"))
    private void modifyDroppedLoot(LootTable lootTable, LootContext context, Consumer<ItemStack> lootConsumer) {
        if (this.world.isClient) {
            lootTable.generateLoot(context, lootConsumer);
            return;
        }

        try (EventInvokers invokers = Stimuli.select().forEntity(this)) {
            List<ItemStack> droppedStacks = lootTable.generateLoot(context);

            TypedActionResult<List<ItemStack>> result = invokers.get(EntityDropItemsEvent.EVENT)
                    .onDropItems((LivingEntity) (Object) this, droppedStacks);

            if (result.getResult() != ActionResult.FAIL) {
                result.getValue().forEach(this::dropStack);
            }
        }
    }
}
