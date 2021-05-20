package xyz.nucleoid.stimuli.event.world;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when fluid attempts to flow in the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further handlers and allows the fluid to flow.
 * <li>{@link ActionResult#FAIL} cancels further handlers and does not allow the fluid to flow.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 */
public interface FluidFlowEvent {
    StimulusEvent<FluidFlowEvent> EVENT = StimulusEvent.create(FluidFlowEvent.class, ctx -> {
        return (world, fluidPos, fluidBlock, flowDirection, flowTo, flowToBlock) -> {
            try {
                for (FluidFlowEvent listener : ctx.getListeners()) {
                    ActionResult result = listener.onFluidFlow(world, fluidPos, fluidBlock, flowDirection, flowTo, flowToBlock);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return ActionResult.PASS;
        };
    });

    ActionResult onFluidFlow(ServerWorld world, BlockPos fluidPos, BlockState fluidBlock, Direction flowDirection, BlockPos flowTo, BlockState flowToBlock);
}
