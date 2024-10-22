package xyz.nucleoid.stimuli.mixin.projectile;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.duck.PassBowUseTicks;
import xyz.nucleoid.stimuli.event.projectile.ArrowFireEvent;

import java.util.List;

@Mixin(RangedWeaponItem.class)
public abstract class RangedWeaponItemMixin implements PassBowUseTicks {
    @Unique private ThreadLocal<ProjectileEntity> projectile = new ThreadLocal<>();

    @Shadow
    protected abstract ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical);

    @Inject(
      method = "shootAll",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;spawn(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/entity/projectile/ProjectileEntity;"),
      cancellable = true
    )
    private void onFireArrow(
      ServerWorld world,
      LivingEntity shooter,
      Hand hand,
      ItemStack tool,
      List<ItemStack> projectiles,
      float speed,
      float divergence,
      boolean critical,
      @Nullable LivingEntity target,
      CallbackInfo ci,
      @Local(ordinal = 1) ItemStack projectileStack
    ) {
        if (!(shooter instanceof ServerPlayerEntity player)) {
            return;
        }

        ProjectileEntity projectile = this.createArrowEntity(world, shooter, tool, projectileStack, critical);
        this.projectile.set(projectile);

        Item projectileItem = projectileStack.getItem();
        if (!(projectileItem instanceof ArrowItem item) || !(projectile instanceof PersistentProjectileEntity persistentProjectile)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(ArrowFireEvent.EVENT)
              .onFireArrow(player, tool, item, this.stimuli$getLastRemainingUseTicks(), persistentProjectile);

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @ModifyArg(
      method = "shootAll",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;spawn(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/entity/projectile/ProjectileEntity;")
    )
    private ProjectileEntity useStoredProjectile(ProjectileEntity original) {
        ProjectileEntity stored = this.projectile.get();
        this.projectile.set(null);
        return stored != null ? stored : original;
    }
}
