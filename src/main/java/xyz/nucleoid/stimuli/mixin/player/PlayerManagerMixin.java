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
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent;

import java.util.UUID;
import java.util.function.Function;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    public void broadcastChatMessage(Text message, MessageType type, UUID senderId, CallbackInfo ci) {
        if (this.handleChatMessage(message, type, senderId)) {
            ci.cancel();
        }
    }

    @Inject(method = "broadcast", at = @At("HEAD"), cancellable = true)
    public void broadcast(Text message, Function<ServerPlayerEntity, Text> messageFactory, MessageType type, UUID senderId, CallbackInfo ci) {
        if (this.handleChatMessage(message, type, senderId)) {
            ci.cancel();
        }
    }

    private boolean handleChatMessage(Text message, MessageType type, UUID senderId) {
        if (type != MessageType.CHAT || senderId == Util.NIL_UUID) {
            return false;
        }

        var sender = this.getPlayer(senderId);
        if (sender == null) {
            return false;
        }

        try (var invokers = Stimuli.select().forEntity(sender)) {
            var result = invokers.get(PlayerChatEvent.EVENT).onSendChatMessage(sender, message);
            return result == ActionResult.FAIL;
        }
    }
}
