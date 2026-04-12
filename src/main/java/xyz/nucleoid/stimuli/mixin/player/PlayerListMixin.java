package xyz.nucleoid.stimuli.mixin.player;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.ReplacePlayerChatEvent;

import java.util.function.Predicate;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(
        method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void broadcastChatMessage(final PlayerChatMessage message, final Predicate<ServerPlayer> shouldSendFiltered, final @Nullable ServerPlayer senderPlayer, final ChatType.Bound messageType, final CallbackInfo ci) {
        if (senderPlayer != null) {
            try (var invokers = Stimuli.select().forEntity(senderPlayer)) {
                if (invokers.get(ReplacePlayerChatEvent.EVENT).shouldConsumeChatMessage(senderPlayer, message, messageType)) {
                    ci.cancel();
                }
            }
        }
    }
}
