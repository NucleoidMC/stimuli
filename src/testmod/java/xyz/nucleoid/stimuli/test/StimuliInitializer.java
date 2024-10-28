package xyz.nucleoid.stimuli.test;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.FlowerPotModifyEvent;
import xyz.nucleoid.stimuli.event.entity.EntityShearEvent;
import xyz.nucleoid.stimuli.event.projectile.ArrowFireEvent;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public final class StimuliInitializer implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<String, EventResult> EVENT_RESULTS = Map.of(
        "pass", EventResult.PASS,
        "allow", EventResult.ALLOW,
        "deny", EventResult.DENY
    );

    private static EventResult result = EventResult.PASS;
    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var command = literal("stimuli_result");
            for (var entry : EVENT_RESULTS.entrySet()) {
                command.then(literal(entry.getKey()).executes(context -> {
                    result = entry.getValue();
                    return 0;
                }));
            }

            dispatcher.register(command);
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server1 -> server = server1);

        ServerLifecycleEvents.SERVER_STOPPED.register(server1 -> server = null);

        Stimuli.global().listen(FlowerPotModifyEvent.EVENT, (player, hand, hitResult) -> {
            player.sendMessage(Text.literal("FlowerPotModifyEvent"));
            return result;
        });
        Stimuli.global().listen(ArrowFireEvent.EVENT, (player, tool, arrowItem, remainingUseTicks, projectile) -> {
            player.sendMessage(Text.literal("ArrowFireEvent: " + remainingUseTicks));
            return result;
        });
        Stimuli.global().listen(EntityShearEvent.EVENT, (entity, player, hand, pos) -> {
            if (player == null) {
                server.sendMessage(Text.literal("EntityShearEvent: " + entity.getName().getString()));
            } else {
                player.sendMessage(Text.literal("EntityShearEvent: " + entity.getName().getString()));
            }
            return result;
        });
        Stimuli.global().listen(ExplosionDetonatedEvent.EVENT, (explosion, particles) -> {
            server.sendMessage(Text.literal("ExplosionDetonatedEvent: " + explosion.getDestructionType().name()));
            return result;
        });
    }
}
