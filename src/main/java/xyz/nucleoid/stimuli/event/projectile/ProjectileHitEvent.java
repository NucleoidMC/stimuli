package xyz.nucleoid.stimuli.event.projectile;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public final class ProjectileHitEvent {
    /**
     * Called when a {@link net.minecraft.world.level.block.Block} is hit by a {@link Projectile}.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link EventResult#ALLOW} cancels further handlers and executes vanilla behavior.
     * <li>{@link EventResult#DENY} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<Block> BLOCK = StimulusEvent.create(Block.class, ctx -> (entity, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onHitBlock(entity, hitResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    /**
     * Called when an {@link net.minecraft.world.entity.Entity} is hit by a {@link Projectile}.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link EventResult#ALLOW} cancels further handlers and executes vanilla behavior.
     * <li>{@link EventResult#DENY} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<Entity> ENTITY = StimulusEvent.create(Entity.class, ctx -> (entity, hitResult) -> {
        try {
            for (Entity listener : ctx.getListeners()) {
                var result = listener.onHitEntity(entity, hitResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    public interface Block {
        EventResult onHitBlock(Projectile entity, BlockHitResult hitResult);
    }

    public interface Entity {
        EventResult onHitEntity(Projectile entity, EntityHitResult hitResult);
    }
}
