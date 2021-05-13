package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.PlayerEvents;

import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    public void broadcastChatMessage(Text message, MessageType type, UUID senderUuid, CallbackInfo ci) {
        if (type != MessageType.CHAT || senderUuid == Util.NIL_UUID) {
            return;
        }

        ServerPlayerEntity sender = this.getPlayer(senderUuid);
        if (sender == null) {
            return;
        }

        try (EventInvokers invokers = Stimuli.select().forEntity(sender)) {
            ActionResult result = invokers.get(PlayerEvents.CHAT).onSendChatMessage(sender, message);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
