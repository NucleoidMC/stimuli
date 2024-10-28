package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerC2SPacketEvent;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void onPacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (listener instanceof ServerPlayNetworkHandler handler) {
            try (var invokers = Stimuli.select().forEntity(handler.player)) {
                var result = invokers.get(PlayerC2SPacketEvent.EVENT).onPacket(handler.player, packet);
                if (result == EventResult.DENY) {
                    ci.cancel();
                }
            }
        }
    }
}
