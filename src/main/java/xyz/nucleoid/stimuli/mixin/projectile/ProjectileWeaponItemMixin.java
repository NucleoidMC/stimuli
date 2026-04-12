package xyz.nucleoid.stimuli.mixin.projectile;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.duck.PassBowUseTicks;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.projectile.ArrowFireEvent;

import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin implements PassBowUseTicks {

    @WrapOperation(
      method = "shoot",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/world/entity/projectile/Projectile;")
    )
    private Projectile onFireArrow(
            Projectile projectile, ServerLevel level, ItemStack projectileStack, Consumer<Projectile> beforeSpawn, Operation<Projectile> original,
            @Local(argsOnly = true, ordinal = 0) LivingEntity shooter, @Local(argsOnly = true) ItemStack tool, @Share("damage") LocalBooleanRef damage
    ) {
        if (!(shooter instanceof ServerPlayer player)) {
            return original.call(projectile, level, projectileStack, beforeSpawn);
        }

        Item projectileItem = projectileStack.getItem();
        if (!(projectileItem instanceof ArrowItem item) || !(projectile instanceof AbstractArrow persistentProjectile)) {
            damage.set(true);
            return original.call(projectile, level, projectileStack, beforeSpawn);
        }

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(ArrowFireEvent.EVENT)
              .onFireArrow(player, tool, item, this.stimuli$getLastRemainingUseTicks(), persistentProjectile);

            if (result == EventResult.DENY) {
                damage.set(false);
                return projectile;
            }
        }
        damage.set(true);
        return original.call(projectile, level, projectileStack, beforeSpawn);
    }

    @WrapWithCondition(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private boolean cancelDamageIfNoProjectile(ItemStack instance, int amount, LivingEntity entity, EquipmentSlot slot, @Share("damage") LocalBooleanRef damage) {
        return damage.get();
    }
}
