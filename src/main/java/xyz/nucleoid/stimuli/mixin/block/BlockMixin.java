package xyz.nucleoid.stimuli.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public class BlockMixin {
    @Redirect(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static void dropStacks(
            List<ItemStack> stacks,
            Consumer<ItemStack> action,
            BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack
    ) {
        handleDropStacks(stacks, action, state, level, pos, entity);
    }


    @Redirect(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, LevelAccessor level, BlockPos pos) {
        handleDropStacks(stacks, action, state, (Level) level, pos, null);
    }

    @Redirect(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")
    )
    private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, Level level, BlockPos pos) {
        handleDropStacks(stacks, action, state, level, pos, null);
    }

    @Unique
    private static void handleDropStacks(List<ItemStack> stacks,
                                         Consumer<ItemStack> action,
                                         BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(level instanceof ServerLevel)) {
            stacks.forEach(action);
            return;
        }

        var events = Stimuli.select();

        try (var invokers = entity != null ? events.forEntityAt(entity, pos) : events.at(level, pos)) {
            var result = invokers.get(BlockDropItemsEvent.EVENT)
                    .onDropItems(entity, (ServerLevel) level, pos, state, stacks);

            var newStacks = result.dropStacks();
            newStacks.forEach(action);
        }
    }
}
