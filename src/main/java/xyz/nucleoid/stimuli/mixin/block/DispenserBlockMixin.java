package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.DispenserActivateEvent;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(
            method = "dispense",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/DispenserBlock;getBehaviorForItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void useDispenser(ServerWorld world, BlockPos pos, CallbackInfo ci, BlockPointerImpl blockPointerImpl, DispenserBlockEntity dispenserBlockEntity, int slot, ItemStack itemStack) {
        var events = Stimuli.select();

        try (var invokers = events.at(world, pos)) {
            var result = invokers.get(DispenserActivateEvent.EVENT).onActivate(world, pos, dispenserBlockEntity, slot, itemStack);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
