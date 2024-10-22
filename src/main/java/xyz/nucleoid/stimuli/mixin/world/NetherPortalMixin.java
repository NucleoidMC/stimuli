package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.world.NetherPortalOpenEvent;

import java.util.Optional;

@Mixin(NetherPortal.class)
public class NetherPortalMixin {
    @Shadow private BlockPos lowerCorner;

    @ModifyReturnValue(method = "getNewPortal", at = @At("RETURN"))
    private static Optional<NetherPortal> filterNewPortal(Optional<NetherPortal> original, WorldAccess worldAccess, BlockPos pos, Direction.Axis firstCheckedAxis) {
        return original.filter(portal -> {
            if (!(worldAccess instanceof ServerWorld || worldAccess instanceof ChunkRegion)) {
                return true;
            }

            var world = ((ServerWorldAccess) worldAccess).toServerWorld();
            var lowerCorner = ((NetherPortalMixin) (Object) portal).lowerCorner;

            try (var invokers = Stimuli.select().at(world, lowerCorner)) {
                var result = invokers.get(NetherPortalOpenEvent.EVENT).onOpenNetherPortal(world, lowerCorner);

                if (result == ActionResult.FAIL) {
                    return false;
                }
            }

            return true;
        });
    }
}
