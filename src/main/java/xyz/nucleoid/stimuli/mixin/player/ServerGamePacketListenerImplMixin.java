package xyz.nucleoid.stimuli.mixin.player;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.*;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleAnimate", at = @At("HEAD"))
    private void onHandSwing(ServerboundSwingPacket packet, CallbackInfo ci) {
        var hand = packet.getHand();
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            invokers.get(PlayerSwingHandEvent.EVENT).onSwingHand(this.player, hand);
        }
    }

    @Inject(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundContainerClickPacket;stateId()I"), cancellable = true)
    private void onInventoryAction(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerInventoryActionEvent.EVENT).onInventoryAction(this.player, packet.slotNum(), packet.containerInput(), packet.buttonNum());
            if (result == EventResult.DENY) {
                for (var e : Int2ObjectMaps.fastIterable(packet.changedSlots())) {
                    this.player.containerMenu.setRemoteSlotUnsafe(e.getIntKey(), e.getValue());
                }

                this.player.containerMenu.setRemoteCarried(packet.carriedItem());

                this.player.containerMenu.broadcastChanges();
                ci.cancel();
            }
        }
    }

    @Inject(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0), cancellable = true)
    private void onSwapWithOffhand(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerSwapWithOffhandEvent.EVENT).onSwapWithOffhand(this.player);
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "handleChatCommand", at = @At("HEAD"), cancellable = true)
    private void onCommandExecution(ServerboundChatCommandPacket packet, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerCommandEvent.EVENT).onPlayerCommand(this.player, packet.command());
            if (result == EventResult.DENY) {
                ci.cancel();
            }
        }
    }

    @WrapWithCondition(method = "handleSpectatorAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setCamera(Lnet/minecraft/world/entity/Entity;)V"))
    private boolean onSpectateEntity(ServerPlayer player, Entity newCamera) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerSpectateEntityEvent.EVENT).onSpectateEntity(this.player, newCamera);
            if (result == EventResult.DENY) {
                return false;
            }
        }
        return true;
    }

}
