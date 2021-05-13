package xyz.nucleoid.stimuli.event;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public final class ProjectileEvents {
    /**
     * Called when a {@link net.minecraft.block.Block} is hit by a {@link ProjectileEntity}.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further handlers and executes vanilla behavior.
     * <li>{@link ActionResult#FAIL} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<HitBlock> HIT_BLOCK = StimulusEvent.create(HitBlock.class, ctx -> (entity, hitResult) -> {
        try {
            for (HitBlock listener : ctx.getListeners()) {
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
    public static final StimulusEvent<HitEntity> HIT_ENTITY = StimulusEvent.create(HitEntity.class, ctx -> (entity, hitResult) -> {
        try {
            for (HitEntity listener : ctx.getListeners()) {
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

    public interface HitBlock {
        ActionResult onHitBlock(ProjectileEntity entity, BlockHitResult hitResult);
    }

    public interface HitEntity {
        ActionResult onHitEntity(ProjectileEntity entity, EntityHitResult hitResult);
    }
}
