package xyz.nucleoid.stimuli.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerS2CPacketEvent;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    private void onPacket(Packet<?> packet, PacketCallbacks listener, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayNetworkHandler networkHandler) {
            var player = networkHandler.getPlayer();

            try (var invokers = Stimuli.select().forEntity(player)) {
                var result = invokers.get(PlayerS2CPacketEvent.EVENT).onPacket(player, packet);
                if (result == ActionResult.FAIL) {
                    ci.cancel();
                }
            }
        }
    }
}
