package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerCommandEvent;
import xyz.nucleoid.stimuli.event.player.PlayerInventoryActionEvent;
import xyz.nucleoid.stimuli.event.player.PlayerSwapWithOffhandEvent;
import xyz.nucleoid.stimuli.event.player.PlayerSwingHandEvent;

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

    @Inject(method = "onClickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/ClickSlotC2SPacket;getRevision()I"), cancellable = true)
    private void onInventoryAction(ClickSlotC2SPacket packet, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerInventoryActionEvent.EVENT).onInventoryAction(this.player, packet.getSlot(), packet.getActionType(), packet.getButton());
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onPlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0), cancellable = true)
    private void onSwapWithOffhand(PlayerActionC2SPacket packet, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerSwapWithOffhandEvent.EVENT).onSwapWithOffhand(this.player);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    private void onCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        try (var invokers = Stimuli.select().forEntity(this.player)) {
            var result = invokers.get(PlayerCommandEvent.EVENT).onPlayerCommand(this.player, packet.command());
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

}
