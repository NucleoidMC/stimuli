package xyz.nucleoid.stimuli;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
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
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent;

public final class StimuliInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, entity.getBlockPos())) {
                    var result = invokers.get(EntityUseEvent.EVENT)
                            .onUse(serverPlayer, entity, hand, hit);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                try (var invokers = Stimuli.select().forEntity(player)) {
                    return invokers.get(ItemUseEvent.EVENT).onUse(serverPlayer, hand);
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, hitResult.getBlockPos())) {
                    return invokers.get(BlockUseEvent.EVENT).onUse(serverPlayer, hand, hitResult);
                }
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, pos)) {
                    return invokers.get(BlockBreakEvent.PLAYER).onBreak(serverPlayer, (ServerWorld) world, pos) != ActionResult.FAIL;
                }
            }
            return true;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, entity.getBlockPos())) {
                    return invokers.get(PlayerAttackEntityEvent.EVENT).onAttackEntity(serverPlayer, hand, entity, hitResult);
                }
            }
            return ActionResult.PASS;
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            try (var invokers = Stimuli.select().forEntity(sender)) {
                var result = invokers.get(PlayerChatEvent.EVENT).onSendChatMessage(sender, message, params);
                return result != ActionResult.FAIL;
            }
        });
    }
}
