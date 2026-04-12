package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.entity.EntityShearEvent;
import xyz.nucleoid.stimuli.mixin.BoggedAccessor;

import java.util.List;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.entity.player.Player;

@Mixin(value = {
    MushroomCow.class,
    Sheep.class,
    SnowGolem.class,
    Bogged.class
})
public class ShearableEntityMixin {
    @WrapOperation(
            method = "mobInteract",
            at = {
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/animal/cow/MushroomCow;readyForShearing()Z"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;readyForShearing()Z"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/animal/golem/SnowGolem;readyForShearing()Z"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/monster/skeleton/Bogged;readyForShearing()Z"
                    )
            }
    )
    private boolean onEntityShear(@Coerce Shearable shearable, Operation<Boolean> original, Player player, InteractionHand hand) {
        if (!original.call(shearable)) {
            return false;
        }

        if (!player.level().isClientSide()) {
            // Entities are all subclasses of LivingEntity
            var entity = (LivingEntity) shearable;
            var serverPlayer = (ServerPlayer) player;

            var events = Stimuli.select();

            try (var invokers = events.forEntity(entity)) {
                var result = invokers.get(EntityShearEvent.EVENT).onShearEntity(entity, serverPlayer, hand, null);
                if (result == EventResult.DENY) {
                    if ((Object) this instanceof Bogged) {
                        SynchedEntityData.DataValue<Boolean> shearedEntry = SynchedEntityData.DataValue.create(BoggedAccessor.getDATA_SHEARED(), false);
                        var packet = new ClientboundSetEntityDataPacket(entity.getId(), List.of(shearedEntry));
                        serverPlayer.connection.send(packet);
                    }

                    return false;
                }
            }
        }

        return true;
    }
}
