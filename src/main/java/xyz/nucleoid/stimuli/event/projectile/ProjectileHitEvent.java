package xyz.nucleoid.stimuli.event.projectile;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public final class ProjectileHitEvent {
    /**
     * Called when a {@link net.minecraft.block.Block} is hit by a {@link ProjectileEntity}.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further handlers and executes vanilla behavior.
     * <li>{@link ActionResult#FAIL} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<Block> BLOCK = StimulusEvent.create(Block.class, ctx -> (entity, hitResult) -> {
        try {
            for (Block listener : ctx.getListeners()) {
                ActionResult result = listener.onHitBlock(entity, hitResult);
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
     * Called when an {@link net.minecraft.entity.Entity} is hit by a {@link ProjectileEntity}.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further handlers and executes vanilla behavior.
     * <li>{@link ActionResult#FAIL} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<Entity> ENTITY = StimulusEvent.create(Entity.class, ctx -> (entity, hitResult) -> {
        try {
            for (Entity listener : ctx.getListeners()) {
                ActionResult result = listener.onHitEntity(entity, hitResult);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public interface Block {
        ActionResult onHitBlock(ProjectileEntity entity, BlockHitResult hitResult);
    }

    public interface Entity {
        ActionResult onHitEntity(ProjectileEntity entity, EntityHitResult hitResult);
    }
}
