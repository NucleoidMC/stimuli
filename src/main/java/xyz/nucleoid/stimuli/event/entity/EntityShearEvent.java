package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called before a {@linkplain net.minecraft.entity.Shearable shearable} entity is sheared by a player or dispenser.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and shears the entity.
 * <li>{@link ActionResult#FAIL} cancels further processing and doesn't shear the entity.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the entity is sheared.
 *
 * <p>If the entity is being sheared by a player, then {@code player} and {@code hand} will be provided.
 * If the entity is being sheared by a dispenser, then {@code pos} will be provided.
 */
public interface EntityShearEvent {
    StimulusEvent<EntityShearEvent> EVENT = StimulusEvent.create(EntityShearEvent.class, ctx -> (entity, player, hand, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onShearEntity(entity, player, hand, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onShearEntity(LivingEntity entity, @Nullable ServerPlayerEntity player, @Nullable Hand hand, @Nullable BlockPos pos);
}
