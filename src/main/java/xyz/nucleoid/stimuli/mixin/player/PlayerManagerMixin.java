package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent;

import java.util.UUID;
import java.util.function.Function;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Function;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
    public void broadcast(SignedMessage message, Function<ServerPlayerEntity, SignedMessage> playerMessageFactory, MessageSender sender, RegistryKey<MessageType> type, CallbackInfo ci) {
        if (this.handleChatMessage(message, type, sender)) {
            ci.cancel();
        }
    }


    private boolean handleChatMessage(SignedMessage message, RegistryKey<MessageType> type, MessageSender sender) {
        if (type != MessageType.CHAT || sender.uuid() == Util.NIL_UUID) {
            return false;
        }

        var senderEntity = this.getPlayer(sender.uuid());
        if (senderEntity == null) {
            return false;
        }

        try (var invokers = Stimuli.select().forEntity(senderEntity)) {
            var result = invokers.get(PlayerChatEvent.EVENT).onSendChatMessage(senderEntity, sender, message);
            return result == ActionResult.FAIL;
        }
    }
}
