package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.StimuliSelector;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.BlockEvents;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Block.class)
public class BlockMixin {
    @Redirect(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static void dropStacks(
            List<ItemStack> stacks,
            Consumer<ItemStack> action,
            BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack
    ) {
        if (!(world instanceof ServerWorld)) {
            stacks.forEach(action);
            return;
        }

        StimuliSelector events = Stimuli.select();

        try (EventInvokers invokers = entity != null ? events.forEntityAt(entity, pos) : events.at(world, pos)) {
            TypedActionResult<List<ItemStack>> result = invokers.get(BlockEvents.DROP_ITEMS)
                    .onDropItems(entity, pos, state, stacks);

            if (result.getResult() != ActionResult.FAIL) {
                List<ItemStack> newStacks = result.getValue();
                newStacks.forEach(action);
            }
        }
    }
}
