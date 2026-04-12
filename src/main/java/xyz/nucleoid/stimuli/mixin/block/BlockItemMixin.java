package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockPlaceEvent;
import xyz.nucleoid.stimuli.util.SlotHelper;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(
            method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void onPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> ci) {
        if (!(context.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        var blockPos = context.getClickedPos();

        try (var invokers = Stimuli.select().forEntityAt(player, blockPos)) {
            var state = context.getLevel().getBlockState(blockPos);
            invokers.get(BlockPlaceEvent.AFTER).onPlace(player, player.level(), blockPos, state);
        }
    }

    @Inject(method = "placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlace(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> ci) {
        if (!(context.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        var blockPos = context.getClickedPos();

        try (var invokers = Stimuli.select().forEntityAt(player, blockPos)) {
            var result = invokers.get(BlockPlaceEvent.BEFORE).onPlace(player, player.level(), blockPos, state, context);

            if (result == EventResult.DENY) {
                // notify the client that this action did not go through
                int slot = SlotHelper.getHandSlot(player, context.getHand());
                SlotHelper.updateSlot(player, slot);

                ci.setReturnValue(false);
            }
        }
    }
}
