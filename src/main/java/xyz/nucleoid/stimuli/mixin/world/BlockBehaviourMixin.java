package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @WrapOperation(
            method = "onExplosionHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getDrops(Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"
            )
    )
    private List<ItemStack> stimuli_dropBlock(BlockState state, LootParams.Builder builder, Operation<List<ItemStack>> operation,
                                              @Local Explosion explosion, @Local ServerLevel level) {
        var events = Stimuli.select();
        final var entity = explosion.getDirectSourceEntity();

        var pos = entity != null ? entity.blockPosition() : BlockPos.containing(explosion.center());
        try (var invokers = entity != null ? events.forEntityAt(entity, pos) : events.at(level, pos)) {
            var result = invokers.get(BlockDropItemsEvent.EVENT)
                    .onDropItems(entity, (ServerLevel) level, pos, state, operation.call(state, builder));

            return result.dropStacks();
        }
    }
}
