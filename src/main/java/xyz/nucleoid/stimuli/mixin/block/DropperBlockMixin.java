package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.DispenserActivateEvent;

@Mixin(DropperBlock.class)
public class DropperBlockMixin {
    @Inject(
            method = "dispense",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            ),
            cancellable = true
    )
    private void useDispenser(ServerWorld world, BlockState state, BlockPos pos, CallbackInfo ci,
                              @Local DispenserBlockEntity dispenserBlockEntity, @Local BlockPointer blockPointer, @Local int slot, @Local ItemStack itemStack) {
        var events = Stimuli.select();

        try (var invokers = events.at(world, pos)) {
            var result = invokers.get(DispenserActivateEvent.EVENT).onActivate(world, pos, dispenserBlockEntity, slot, itemStack);
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }
}
