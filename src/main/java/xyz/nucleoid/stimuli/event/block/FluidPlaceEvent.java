package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link PlayerEntity} or {@link DispenserBlockEntity} attempts to place fluid from bucket.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the placement.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the placement.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the use succeeds and proceeds with normal logic.
 */
public interface FluidPlaceEvent {
    StimulusEvent<FluidPlaceEvent> EVENT = StimulusEvent.create(FluidPlaceEvent.class, ctx -> (world, pos, player, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onFluidPlace(world, pos, player, hitResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onFluidPlace(ServerWorld world, BlockPos pos, @Nullable ServerPlayerEntity player, @Nullable BlockHitResult hitResult);
}
