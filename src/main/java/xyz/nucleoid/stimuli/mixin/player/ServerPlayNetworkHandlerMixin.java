package xyz.nucleoid.stimuli.mixin.player;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.*;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onHandSwing", at = @At("HEAD"))
    private void onHandSwing(HandSwingC2SPacket packet, CallbackInfo ci) {
        var hand = packet.getHand();
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            invokers.get(PlayerSwingHandEvent.EVENT).onSwingHand(this.player, hand);
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    private void onPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerS2CPacketEvent.EVENT).onPacket(this.player, packet);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onClickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/ClickSlotC2SPacket;getRevision()I"), cancellable = true)
    private void onInventoryAction(ClickSlotC2SPacket packet, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerInventoryActionEvent.EVENT).onInventoryAction(this.player, packet.getSlot(), packet.getActionType(), packet.getButton());
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "handleMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(TextStream.Message message, CallbackInfo ci) {
        var raw = message.getRaw();
        if (!raw.startsWith("/")) {
            try (var invokers = Stimuli.select().forEntity(this.player)) {
                var mutable = new PlayerChatContentEvent.MutableMessage(raw, message.getFiltered());
                var result = invokers.get(PlayerChatContentEvent.EVENT).onSendChat(this.player, mutable);
                if (result == ActionResult.FAIL) {
                    ci.cancel();
                } else {
                    ((MessageAccessor) message).setRaw(mutable.getRaw());
                    ((MessageAccessor) message).setFiltered(mutable.getFiltered());
                }
            }
        }
    }

    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void onCommandExecution(String input, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerCommandEvent.EVENT).onPlayerCommand(this.player, input);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

}
