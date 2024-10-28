package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link DispenserBlockEntity} is activated and is not empty.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the use.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the use.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the use succeeds and proceeds with normal logic.
 */
public interface DispenserActivateEvent {
    StimulusEvent<DispenserActivateEvent> EVENT = StimulusEvent.create(DispenserActivateEvent.class, ctx -> (world, pos, dispenserBlockEntity, slot, stackToDispense) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onActivate(world, pos, dispenserBlockEntity, slot, stackToDispense);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onActivate(ServerWorld world, BlockPos pos, DispenserBlockEntity dispenserBlockEntity, int slot, ItemStack stackToDispense);
}
