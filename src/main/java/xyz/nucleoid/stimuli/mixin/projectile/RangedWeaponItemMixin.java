package xyz.nucleoid.stimuli.mixin.projectile;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.duck.PassBowUseTicks;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.projectile.ArrowFireEvent;

import java.util.function.Consumer;

@Mixin(RangedWeaponItem.class)
public abstract class RangedWeaponItemMixin implements PassBowUseTicks {

    @Shadow
    protected abstract ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical);

    @WrapOperation(
      method = "shootAll",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;spawn(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/entity/projectile/ProjectileEntity;")
    )
    private ProjectileEntity onFireArrow(
            ProjectileEntity projectile, ServerWorld world, ItemStack projectileStack, Consumer<ProjectileEntity> beforeSpawn, Operation<ProjectileEntity> original,
            @Local(argsOnly = true, ordinal = 0) LivingEntity shooter, @Local(argsOnly = true) ItemStack tool, @Share("damage") LocalBooleanRef damage
    ) {
        if (!(shooter instanceof ServerPlayerEntity player)) {
            return original.call(projectile, world, projectileStack, beforeSpawn);
        }

        Item projectileItem = projectileStack.getItem();
        if (!(projectileItem instanceof ArrowItem item) || !(projectile instanceof PersistentProjectileEntity persistentProjectile)) {
            damage.set(true);
            return original.call(projectile, world, projectileStack, beforeSpawn);
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
        return original.call(projectile, world, projectileStack, beforeSpawn);
    }

    @WrapWithCondition(method = "shootAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V"))
    private boolean cancelDamageIfNoProjectile(ItemStack instance, int amount, LivingEntity entity, EquipmentSlot slot, @Share("damage") LocalBooleanRef damage) {
        return damage.get();
    }
}
