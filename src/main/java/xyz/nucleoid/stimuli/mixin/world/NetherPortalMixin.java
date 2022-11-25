package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.world.NetherPortalOpenEvent;

@Mixin(NetherPortal.class)
public class NetherPortalMixin {
    @Shadow @Final private WorldAccess world;
    @Shadow @Nullable private BlockPos lowerCorner;

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void isValid(CallbackInfoReturnable<Boolean> ci) {
        if (!(this.world instanceof ServerWorldAccess) || this.lowerCorner == null) {
            return;
        }

        var world = ((ServerWorldAccess) this.world).toServerWorld();

        try (var invokers = Stimuli.select().at(world, this.lowerCorner)) {
            var result = invokers.get(NetherPortalOpenEvent.EVENT).onOpenNetherPortal(world, this.lowerCorner);

            if (result == ActionResult.FAIL) {
                ci.setReturnValue(false);
            }
        }
    }
}
