package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerC2SPacketEvent;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "genericsFtw", at = @At("HEAD"), cancellable = true)
    private static void onPacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (listener instanceof ServerGamePacketListenerImpl handler) {
            try (var invokers = Stimuli.select().forEntity(handler.player)) {
                var result = invokers.get(PlayerC2SPacketEvent.EVENT).onPacket(handler.player, packet);
                if (result == EventResult.DENY) {
                    ci.cancel();
                }
            }
        }
    }
}
