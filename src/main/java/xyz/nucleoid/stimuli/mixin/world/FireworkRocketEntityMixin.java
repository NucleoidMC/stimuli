package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.FireworkExplodeEvent;

import java.util.Collections;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Final @Shadow private static TrackedData<ItemStack> ITEM;

    @Inject(method = "explodeAndRemove", at = @At("HEAD"))
    private void explodeAndRemove(CallbackInfo ci) {
        var firework = (FireworkRocketEntity) (Object) this;

        if (!firework.getEntityWorld().isClient()) {
            try (var invokers = Stimuli.select().forEntity(firework)) {
                var result = invokers.get(FireworkExplodeEvent.EVENT).onFireworkExplode(firework);
                if (result == EventResult.DENY) {
                    // Make a copy so the data tracker entry is marked dirty
                    ItemStack stack = firework.getDataTracker().get(ITEM).copy();

                    // Remove explosion data from new stack
                    FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
                    if (fireworksComponent != null) {
                        stack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(fireworksComponent.flightDuration(), Collections.emptyList()));
                    }

                    // Update data tracker with new stack
                    firework.getDataTracker().set(ITEM, stack);

                    // Send data tracker update to observing players
                    ServerWorld world = (ServerWorld) firework.getEntityWorld();

                    var dirty = firework.getDataTracker().getDirtyEntries();

                    if (dirty != null) {
                        var packet = new EntityTrackerUpdateS2CPacket(firework.getId(), dirty);
                        ServerChunkManager chunkManager = world.getChunkManager();
                        chunkManager.sendToOtherNearbyPlayers(firework, packet);
                    }
                }
            }
        }
    }
}
