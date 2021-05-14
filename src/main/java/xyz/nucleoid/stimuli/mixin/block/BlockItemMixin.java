package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.BlockEvents;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private void onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> ci) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        BlockPos blockPos = context.getBlockPos();

        try (EventInvokers invokers = Stimuli.select().forEntityAt(player, blockPos)) {
            BlockState state = context.getWorld().getBlockState(blockPos);
            invokers.get(BlockEvents.AFTER_PLACE).onPlace(player, player.getServerWorld(), blockPos, state);
        }
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> ci) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        BlockPos blockPos = context.getBlockPos();

        try (EventInvokers invokers = Stimuli.select().forEntityAt(player, blockPos)) {
            ActionResult result = invokers.get(BlockEvents.PLACE).onPlace(player, player.getServerWorld(), blockPos, state, context);

            if (result == ActionResult.FAIL) {
                // notify the client that this action did not go through
                int slot = context.getHand() == Hand.MAIN_HAND ? player.inventory.selectedSlot : 40;
                ItemStack stack = context.getStack();
                player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, slot, stack));

                ci.setReturnValue(false);
            }
        }
    }
}
