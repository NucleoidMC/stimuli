package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public class EntityLeashEvent {
    /**
     * Called when a leash is attached to either a holding {@link Entity} or a {@link BlockPos}.
	 * 
	 * <p>A leash holder can be either a player or an existing leash knot.
     * A leash position may or may not contain a leash knot yet.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further handlers and allows the leash to be attached.
     * <li>{@link ActionResult#FAIL} cancels further handlers and prevents the leash from being attached.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<Attach> ATTACH = StimulusEvent.create(Attach.class, ctx -> (entity, leashHolder, leashPos, player, hand) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onAttachLeash(entity, leashHolder, leashPos, player, hand);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public interface Attach {
        ActionResult onAttachLeash(Entity entity, @Nullable Entity leashHolder, @Nullable BlockPos leashPos, ServerPlayerEntity player, @Nullable Hand hand);
    }
}
