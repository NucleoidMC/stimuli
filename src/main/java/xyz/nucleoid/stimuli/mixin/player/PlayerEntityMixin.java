package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerRegenerateEvent;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private void attemptPeacefulRegeneration(PlayerEntity player, float amount) {
        if (!(player instanceof ServerPlayerEntity)) {
            player.heal(amount);
            return;
        }

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerRegenerateEvent.EVENT)
                    .onRegenerate((ServerPlayerEntity) player, amount);

            if (result != ActionResult.FAIL) {
                player.heal(amount);
            }
        }
    }
}
