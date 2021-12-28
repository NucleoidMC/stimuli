package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.world.EndPortalOpenEvent;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/pattern/BlockPattern;searchAround(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/pattern/BlockPattern$Result;"))
    private BlockPattern.Result searchAround(BlockPattern pattern, WorldView patternWorld, BlockPos pos, ItemUsageContext context) {
        var patternResult = pattern.searchAround(patternWorld, pos);

        var world = context.getWorld();
        try (var invokers = Stimuli.select().at(world, pos)) {
            var result = invokers.get(EndPortalOpenEvent.EVENT).onOpenEndPortal(context, patternResult);
            if (result == ActionResult.FAIL) {
                return null;
            }
        }

        return patternResult;
    }
}
