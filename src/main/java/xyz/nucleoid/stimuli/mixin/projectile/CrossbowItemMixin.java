package xyz.nucleoid.stimuli.mixin.projectile;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.projectile.ArrowFireEvent;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Inject(
            method = "shoot",
            at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void shoot(
            World world,
            LivingEntity user,
            Hand hand,
            ItemStack tool,
            ItemStack projectileStack,
            float soundPitch,
            boolean creative,
            float speed,
            float divergence,
            float simulated,
            CallbackInfo ci,
            boolean firework,
            ProjectileEntity projectile
    ) {
        if (!(user instanceof ServerPlayerEntity)) {
            return;
        }

        Item projectileItem = projectileStack.getItem();
        if (!(projectileItem instanceof ArrowItem) || !(projectile instanceof PersistentProjectileEntity)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntity(user)) {
            var result = invokers.get(ArrowFireEvent.EVENT)
                    .onFireArrow((ServerPlayerEntity) user, tool, (ArrowItem) projectileItem, -1, (PersistentProjectileEntity) projectile);

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
