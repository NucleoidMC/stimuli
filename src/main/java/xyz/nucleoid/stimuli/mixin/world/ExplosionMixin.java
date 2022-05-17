package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final private World world;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;

    @Shadow @Final private @Nullable Entity entity;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void affectWorld(boolean particles, CallbackInfo ci) {
        if (!this.world.isClient) {
            var pos = new BlockPos(this.x, this.y, this.z);

            try (var invokers = Stimuli.select().at(this.world, pos)) {
                invokers.get(ExplosionDetonatedEvent.EVENT).onExplosionDetonated((Explosion) (Object) this, particles);
            }
        }
    }

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;"))
    private List<ItemStack> stimuli_dropBlock(BlockState state, LootContext.Builder builder) {
        var stacks = state.getDroppedStacks(builder);

        var events = Stimuli.select();

        var pos = this.entity != null ? this.entity.getBlockPos() : new BlockPos(this.x, this.y, this.z);
        try (var invokers = this.entity != null ? events.forEntityAt(this.entity, pos) : events.at(world, pos)) {
            var result = invokers.get(BlockDropItemsEvent.EVENT)
                    .onDropItems(entity, (ServerWorld) world, pos, state, stacks);

            if (result.getResult() != ActionResult.FAIL) {
                return result.getValue();
            } else {
                return Collections.emptyList();
            }
        }
    }
}
