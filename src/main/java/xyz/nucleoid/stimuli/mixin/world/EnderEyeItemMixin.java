package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.EndPortalOpenEvent;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/pattern/BlockPattern;find(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;"))
    private BlockPattern.BlockPatternMatch searchAround(BlockPattern instance, LevelReader levelView, BlockPos pos, Operation<BlockPattern.BlockPatternMatch> original, @Local(argsOnly = true) UseOnContext context) {
        var patternResult = original.call(instance, levelView, pos);

        var level = context.getLevel();
        try (var invokers = Stimuli.select().at(level, pos)) {
            var result = invokers.get(EndPortalOpenEvent.EVENT).onOpenEndPortal(context, patternResult);
            if (result == EventResult.DENY) {
                return null;
            }
        }


        return patternResult;
    }
}
