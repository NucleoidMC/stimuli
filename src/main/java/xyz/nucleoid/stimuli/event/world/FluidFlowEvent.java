package xyz.nucleoid.stimuli.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when fluid attempts to flow in the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the fluid to flow.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the fluid to flow.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface FluidFlowEvent {
    StimulusEvent<FluidFlowEvent> EVENT = StimulusEvent.create(FluidFlowEvent.class, ctx -> {
        return (level, fluidPos, fluidBlock, flowDirection, flowTo, flowToBlock) -> {
            try {
                for (var listener : ctx.getListeners()) {
                    var result = listener.onFluidFlow(level, fluidPos, fluidBlock, flowDirection, flowTo, flowToBlock);
                    if (result != EventResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return EventResult.PASS;
        };
    });

    EventResult onFluidFlow(ServerLevel level, BlockPos fluidPos, BlockState fluidBlock, Direction flowDirection, BlockPos flowTo, BlockState flowToBlock);
}
