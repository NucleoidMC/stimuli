package xyz.nucleoid.stimuli;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.entity.EntityUseEvent;
import xyz.nucleoid.stimuli.event.item.ItemUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;

public final class StimuliInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (player instanceof ServerPlayerEntity) {
                try (EventInvokers invokers = Stimuli.select().forEntityAt(player, entity.getBlockPos())) {
                    ActionResult result = invokers.get(EntityUseEvent.EVENT)
                            .onUse((ServerPlayerEntity) player, entity, hand, hit);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player instanceof ServerPlayerEntity) {
                try (EventInvokers invokers = Stimuli.select().forEntity(player)) {
                    return invokers.get(ItemUseEvent.EVENT).onUse((ServerPlayerEntity) player, hand);
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayerEntity) {
                try (EventInvokers invokers = Stimuli.select().forEntityAt(player, hitResult.getBlockPos())) {
                    return invokers.get(BlockUseEvent.EVENT).onUse((ServerPlayerEntity) player, hand, hitResult);
                }
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (player instanceof ServerPlayerEntity) {
                try (EventInvokers invokers = Stimuli.select().forEntityAt(player, pos)) {
                    return invokers.get(BlockBreakEvent.EVENT).onBreak((ServerPlayerEntity) player, (ServerWorld) world, pos) != ActionResult.FAIL;
                }
            }
            return true;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity) {
                try (EventInvokers invokers = Stimuli.select().forEntityAt(player, entity.getBlockPos())) {
                    return invokers.get(PlayerAttackEntityEvent.EVENT).onAttackEntity((ServerPlayerEntity) player, hand, entity, hitResult);
                }
            }
            return ActionResult.PASS;
        });
    }
}
