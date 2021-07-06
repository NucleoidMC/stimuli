package xyz.nucleoid.stimuli.mixin.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.item.ItemThrowEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        var player = (ServerPlayerEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerDeathEvent.EVENT).onDeath(player, source);
            if (result == ActionResult.FAIL) {
                if (player.getHealth() <= 0.0F) {
                    player.setHealth(player.getMaxHealth());
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        var player = (ServerPlayerEntity) (Object) this;

        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(PlayerDamageEvent.EVENT).onDamage(player, source, amount);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void dropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> ci) {
        int slot = this.getInventory().selectedSlot;
        var stack = this.getInventory().getStack(slot);

        try (EventInvokers invokers = Stimuli.select().forEntity(this)) {
            ActionResult result = invokers.get(ItemThrowEvent.EVENT).onThrowItem((ServerPlayerEntity) (Object) this, slot, stack);
            if (result == ActionResult.FAIL) {
                this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0,  slot, stack));
                ci.setReturnValue(false);
            }
        }
    }
}
