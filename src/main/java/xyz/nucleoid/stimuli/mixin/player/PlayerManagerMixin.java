package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.ReplacePlayerChatEvent;

import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(
        method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void broadcastChatMessage(final SignedMessage message, final Predicate<ServerPlayerEntity> shouldSendFiltered, final @Nullable ServerPlayerEntity senderPlayer, final MessageType.Parameters messageType, final CallbackInfo ci) {
        if (senderPlayer != null) {
            try (var invokers = Stimuli.select().forEntity(senderPlayer)) {
                if (invokers.get(ReplacePlayerChatEvent.EVENT).shouldConsumeChatMessage(senderPlayer, message, messageType)) {
                    ci.cancel();
                }
            }
        }
    }
}
