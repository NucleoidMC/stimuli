package xyz.nucleoid.stimuli;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.entity.EntityUseEvent;
import xyz.nucleoid.stimuli.event.item.ItemUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent;

public final class StimuliInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, entity.blockPosition())) {
                    var result = invokers.get(EntityUseEvent.EVENT)
                            .onUse(serverPlayer, entity, hand, hit);
                    if (result != EventResult.PASS) {
                        return result.asActionResult();
                    }
                }
            }

            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                try (var invokers = Stimuli.select().forEntity(player)) {
                    return invokers.get(ItemUseEvent.EVENT).onUse(serverPlayer, hand);
                }
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, hitResult.getBlockPos())) {
                    return invokers.get(BlockUseEvent.EVENT).onUse(serverPlayer, hand, hitResult);
                }
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, entity) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, pos)) {
                    return invokers.get(BlockBreakEvent.EVENT).onBreak(serverPlayer, (ServerLevel) level, pos) != EventResult.DENY;
                }
            }
            return true;
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                try (var invokers = Stimuli.select().forEntityAt(player, entity.blockPosition())) {
                    return invokers.get(PlayerAttackEntityEvent.EVENT).onAttackEntity(serverPlayer, hand, entity, hitResult).asActionResult();
                }
            }
            return InteractionResult.PASS;
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            try (var invokers = Stimuli.select().forEntity(sender)) {
                var result = invokers.get(PlayerChatEvent.EVENT).onSendChatMessage(sender, message, params);
                return result != EventResult.DENY;
            }
        });

        ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register((message, source, params) -> {
            var player = source.getPlayer();
            if (player == null) {
                return true;
            }
            try (var invokers = Stimuli.select().forCommandSource(source)) {
                var result = invokers.get(PlayerChatEvent.EVENT).onSendChatMessage(player, message, params);
                return result != EventResult.DENY;
            }
        });
    }
}
