package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
            method = "dispenseFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            ),
            cancellable = true
    )
    private void useDispenser(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo ci,
                              @Local DispenserBlockEntity dispenserBlockEntity, @Local BlockSource blockPointer, @Local int slot, @Local ItemStack itemStack) {
        var events = Stimuli.select();

        try (var invokers = events.at(level, pos)) {
            var result = invokers.get(DispenserActivateEvent.EVENT).onActivate(level, pos, dispenserBlockEntity, slot, itemStack);
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }
}
