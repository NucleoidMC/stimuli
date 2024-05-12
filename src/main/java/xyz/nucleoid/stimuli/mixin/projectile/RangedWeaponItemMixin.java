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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.duck.PassBowUseTicks;
import xyz.nucleoid.stimuli.event.projectile.ArrowFireEvent;

import java.util.List;

@Mixin(RangedWeaponItem.class)
public abstract class RangedWeaponItemMixin implements PassBowUseTicks {
    @Inject(
      method = "shootAll",
      at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
      cancellable = true
    )
    private void onFireArrow(
      World world,
      LivingEntity shooter,
      Hand hand,
      ItemStack tool,
      List<ItemStack> projectiles,
      float speed,
      float divergence,
      boolean critical,
      @Nullable LivingEntity target,
      CallbackInfo ci,
      @Local(ordinal = 1) ItemStack projectileStack,
      @Local ProjectileEntity projectile
    ) {
        if (!(shooter instanceof ServerPlayerEntity player)) {
            return;
        }

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
}
