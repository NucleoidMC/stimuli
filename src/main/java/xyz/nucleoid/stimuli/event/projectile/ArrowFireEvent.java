package xyz.nucleoid.stimuli.event.projectile;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface ArrowFireEvent {
    /**
     * Called when a {@link ServerPlayerEntity} fires an {@link ArrowEntity} from a bow.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further handlers and executes vanilla behavior.
     * <li>{@link ActionResult#FAIL} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     */
    StimulusEvent<ArrowFireEvent> EVENT = StimulusEvent.create(ArrowFireEvent.class, ctx -> (user, tool, arrows, remaining, projectile) -> {
        try {
            for (ArrowFireEvent listener : ctx.getListeners()) {
                ActionResult result = listener.onFireArrow(user, tool, arrows, remaining, projectile);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onFireArrow(ServerPlayerEntity user, ItemStack tool, ArrowItem arrowItem, int remainingUseTicks, PersistentProjectileEntity projectile);
}
