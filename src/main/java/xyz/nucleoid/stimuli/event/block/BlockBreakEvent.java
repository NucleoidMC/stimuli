package xyz.nucleoid.stimuli.event.block;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public final class BlockBreakEvent {
    /**
     * Called when a block is broken in a {@link net.minecraft.world.World}, regardless of the breaker.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the break.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the break.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the break succeeds and proceeds with normal logic.
     */
    public static final StimulusEvent<World> WORLD = StimulusEvent.create(World.class, ctx -> (pos, drop, breakingEntity, maxUpdateDepth) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBreak(pos, drop, breakingEntity, maxUpdateDepth);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    /**
     * Called when any {@link ServerPlayerEntity} attempts to break a block.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the break.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the break.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the break succeeds.
     */
    public static final StimulusEvent<Player> PLAYER = StimulusEvent.create(Player.class, ctx -> (player, world, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBreak(player, world, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    /**
     * @deprecated Use {@link #PLAYER} instead.
     */
    @Deprecated
    public static final StimulusEvent<Player> EVENT = PLAYER;

    public interface Player {
        ActionResult onBreak(ServerPlayerEntity player, ServerWorld world, BlockPos pos);
    }

    public interface World {
        ActionResult onBreak(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth);
    }
}
