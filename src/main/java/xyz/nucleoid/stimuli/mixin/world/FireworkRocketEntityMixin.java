package xyz.nucleoid.stimuli.mixin.world;

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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Fireworks;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Final @Shadow private static EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM;

    @Inject(method = "explode", at = @At("HEAD"))
    private void explodeAndRemove(CallbackInfo ci) {
        var firework = (FireworkRocketEntity) (Object) this;

        if (!firework.level().isClientSide()) {
            try (var invokers = Stimuli.select().forEntity(firework)) {
                var result = invokers.get(FireworkExplodeEvent.EVENT).onFireworkExplode(firework);
                if (result == EventResult.DENY) {
                    // Make a copy so the data tracker entry is marked dirty
                    ItemStack stack = firework.getEntityData().get(DATA_ID_FIREWORKS_ITEM).copy();

                    // Remove explosion data from new stack
                    Fireworks fireworksComponent = stack.get(DataComponents.FIREWORKS);
                    if (fireworksComponent != null) {
                        stack.set(DataComponents.FIREWORKS, new Fireworks(fireworksComponent.flightDuration(), Collections.emptyList()));
                    }

                    // Update data tracker with new stack
                    firework.getEntityData().set(DATA_ID_FIREWORKS_ITEM, stack);

                    // Send data tracker update to observing players
                    ServerLevel level = (ServerLevel) firework.level();

                    var dirty = firework.getEntityData().packDirty();

                    if (dirty != null) {
                        var packet = new ClientboundSetEntityDataPacket(firework.getId(), dirty);
                        ServerChunkCache chunkManager = level.getChunkSource();
                        chunkManager.sendToTrackingPlayers(firework, packet);
                    }
                }
            }
        }
    }
}
