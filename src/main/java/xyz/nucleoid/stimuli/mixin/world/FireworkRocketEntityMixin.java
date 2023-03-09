package xyz.nucleoid.stimuli.mixin.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.world.FireworkExplodeEvent;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Shadow
    private static TrackedData<ItemStack> ITEM;

    @Inject(method = "explodeAndRemove", at = @At("HEAD"), cancellable = true)
    private void explodeAndRemove(CallbackInfo ci) {
        var firework = (FireworkRocketEntity) (Object) this;

        if (!firework.world.isClient) {
            try (var invokers = Stimuli.select().forEntity(firework)) {
                var result = invokers.get(FireworkExplodeEvent.EVENT).onFireworkExplode(firework);
                if (result == ActionResult.FAIL) {
                    // Make a copy so the data tracker entry is marked dirty
                    ItemStack stack = firework.getDataTracker().get(ITEM).copy();

                    // Remove explosion data from new stack
                    NbtCompound fireworksNbt = stack.getSubNbt(FireworkRocketItem.FIREWORKS_KEY);
                    if (fireworksNbt != null) {
                        fireworksNbt.remove(FireworkRocketItem.EXPLOSIONS_KEY);
                    }

                    // Update data tracker with new stack
                    firework.getDataTracker().set(ITEM, stack);

                    // Send data tracker update to observing players
                    ServerWorld world = (ServerWorld) firework.getWorld();
                    ThreadedAnvilChunkStorage storage = world.getChunkManager().threadedAnvilChunkStorage;

                    Packet<?> packet = new EntityTrackerUpdateS2CPacket(firework.getId(), firework.getDataTracker().getChangedEntries());
                    storage.sendToOtherNearbyPlayers(firework, packet);
                }
            }
        }
    }
}
