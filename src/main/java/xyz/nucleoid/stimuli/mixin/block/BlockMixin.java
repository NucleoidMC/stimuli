package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;

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
        handleDropStacks(stacks, action, state, world, pos, entity);
    }


    @Redirect(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, WorldAccess world, BlockPos pos) {
        handleDropStacks(stacks, action, state, (World) world, pos, null);
    }

    @Redirect(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, World world, BlockPos pos) {
        handleDropStacks(stacks, action, state, world, pos, null);
    }

    @Unique
    private static void handleDropStacks(List<ItemStack> stacks,
                                         Consumer<ItemStack> action,
                                         BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(world instanceof ServerWorld)) {
            stacks.forEach(action);
            return;
        }

        var events = Stimuli.select();

        try (var invokers = entity != null ? events.forEntityAt(entity, pos) : events.at(world, pos)) {
            var result = invokers.get(BlockDropItemsEvent.EVENT)
                    .onDropItems(entity, (ServerWorld) world, pos, state, stacks);

            if (result.getResult() != ActionResult.FAIL) {
                var newStacks = result.getValue();
                newStacks.forEach(action);
            }
        }
    }
}
