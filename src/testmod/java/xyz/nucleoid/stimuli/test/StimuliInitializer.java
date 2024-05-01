package xyz.nucleoid.stimuli.test;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.FlowerPotModifyEvent;
import xyz.nucleoid.stimuli.event.projectile.ArrowFireEvent;

import static net.minecraft.server.command.CommandManager.literal;

public final class StimuliInitializer implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ActionResult result = ActionResult.PASS;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var command = literal("stimuli_result");
            for (ActionResult value : ActionResult.values()) {
                command.then(literal(value.name()).executes(context -> {
                    result = value;
                    return 0;
                }));
            }

            dispatcher.register(command);
        });

        Stimuli.global().listen(FlowerPotModifyEvent.EVENT, (player, hand, hitResult) -> {
            player.sendMessage(Text.literal("FlowerPotModifyEvent"));
            return result;
        });
        Stimuli.global().listen(ArrowFireEvent.EVENT, (player, tool, arrowItem, remainingUseTicks, projectile) -> {
            player.sendMessage(Text.literal("ArrowFireEvent: " + remainingUseTicks));
            return result;
        });
    }
}
