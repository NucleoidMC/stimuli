package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockRandomTickEvent;
import xyz.nucleoid.stimuli.event.block.FluidRandomTickEvent;
import xyz.nucleoid.stimuli.event.entity.EntitySpawnEvent;
import xyz.nucleoid.stimuli.event.world.SnowFallEvent;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    private void applyEntitySpawnEvent(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        try (var invokers = Stimuli.select().at((ServerWorld) (Object) this, entity.getBlockPos())) {
            var result = invokers.get(EntitySpawnEvent.EVENT).onSpawn(entity);
            if (result == ActionResult.FAIL) {
                cir.setReturnValue(false);
            }
        }
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;canSetSnow(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean applySnowFallEvent(Biome biome, WorldView world, BlockPos pos) {
        if (!biome.canSetSnow(world, pos)) {
            return false;
        }

        ServerWorld serverWorld = (ServerWorld) world;

        try (var invokers = Stimuli.select().at(serverWorld, pos)) {
            var result = invokers.get(SnowFallEvent.EVENT).onSnowFall(serverWorld, pos);
            if (result == ActionResult.FAIL) {
                return false;
            }
        }

        return true;
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;randomTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"))
    private void applyBlockRandomTickEvent(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        try (var invokers = Stimuli.select().at(world, pos)) {
            var result = invokers.get(BlockRandomTickEvent.EVENT).onBlockRandomTick(world, pos, state);
            if (result == ActionResult.FAIL) {
                return;
            }
        }

        state.randomTick(world, pos, random);
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;onRandomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"))
    private void applyFluidRandomTickEvent(FluidState state, World world, BlockPos pos, Random random) {
        ServerWorld serverWorld = (ServerWorld) world;

        try (var invokers = Stimuli.select().at(world, pos)) {
            var result = invokers.get(FluidRandomTickEvent.EVENT).onFluidRandomTick(serverWorld, pos, state);
            if (result == ActionResult.FAIL) {
                return;
            }
        }

        state.onRandomTick(world, pos, random);
    }

}
