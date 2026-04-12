package xyz.nucleoid.stimuli.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link Player} or {@link DispenserBlockEntity} attempts to place fluid from bucket.
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
    StimulusEvent<FluidPlaceEvent> EVENT = StimulusEvent.create(FluidPlaceEvent.class, ctx -> (level, pos, player, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onFluidPlace(level, pos, player, hitResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onFluidPlace(ServerLevel level, BlockPos pos, @Nullable ServerPlayer player, @Nullable BlockHitResult hitResult);
}
