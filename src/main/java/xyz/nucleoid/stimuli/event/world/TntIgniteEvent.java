package xyz.nucleoid.stimuli.event.world;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when TNT is ignited within the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further handlers and allows the TNT to be ignited.
 * <li>{@link ActionResult#FAIL} cancels further handlers and does not allow the TNT to be ignited.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 */
public interface TntIgniteEvent {
    StimulusEvent<TntIgniteEvent> EVENT = StimulusEvent.create(TntIgniteEvent.class, ctx -> (world, pos, igniter) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onIgniteTnt(world, pos, igniter);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onIgniteTnt(ServerWorld world, BlockPos pos, @Nullable LivingEntity igniter);
}
