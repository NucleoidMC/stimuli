package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.LeadItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityLeashEvent;

import java.util.List;
import java.util.function.Predicate;

@Mixin(LeadItem.class)
public class LeadItemMixin {
    @WrapOperation(
            method = "attachHeldMobsToBlock",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/item/LeadItem;collectLeashablesAround(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/function/Predicate;)Ljava/util/List;"
            )
    )
    private static List<Leashable> onLeashAttachToBlock(World world, BlockPos pos, Predicate<Leashable> predicate, Operation<List<Leashable>> original, @Local(argsOnly = true) PlayerEntity player) {
        return original.call(world, pos, predicate).stream()
                .filter(leashable -> {
                    // Leashable is a filtered Entity instance
                    var entity = (Entity) leashable;
                    var serverPlayer = (ServerPlayerEntity) player;

                    var events = Stimuli.select();

                    try (var invokers = events.forEntity(serverPlayer)) {
                        var result = invokers.get(EntityLeashEvent.ATTACH).onAttachLeash(entity, null, pos, serverPlayer, null);
                        if (result == ActionResult.FAIL) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }
}
