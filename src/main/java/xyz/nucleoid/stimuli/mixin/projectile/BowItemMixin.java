package xyz.nucleoid.stimuli.mixin.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ProjectileWeaponItemMixin {
    @Unique private int lastRemainingUseTicks;

    @Override
    public int stimuli$getLastRemainingUseTicks() {
        return lastRemainingUseTicks;
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"))
    private void onStoppedUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> ci) {
        lastRemainingUseTicks = remainingUseTicks;
    }
}
