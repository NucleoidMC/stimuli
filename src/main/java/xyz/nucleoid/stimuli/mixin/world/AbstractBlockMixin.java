package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;

import java.util.Collections;
import java.util.List;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @WrapOperation(
            method = "onExploded",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContextParameterSet$Builder;)Ljava/util/List;"
            )
    )
    private List<ItemStack> stimuli_dropBlock(BlockState state, LootContextParameterSet.Builder builder, Operation<List<ItemStack>> operation,
                                              @Local Explosion explosion, @Local World world) {
        var events = Stimuli.select();
        final var entity = explosion.getEntity();

        var pos = entity != null ? entity.getBlockPos() : BlockPos.ofFloored(explosion.getPosition());
        try (var invokers = entity != null ? events.forEntityAt(entity, pos) : events.at(world, pos)) {
            var result = invokers.get(BlockDropItemsEvent.EVENT)
                    .onDropItems(entity, (ServerWorld) world, pos, state, operation.call(state, builder));

            if (result.getResult() != ActionResult.FAIL) {
                return result.getValue();
            } else {
                return Collections.emptyList();
            }
        }
    }
}
