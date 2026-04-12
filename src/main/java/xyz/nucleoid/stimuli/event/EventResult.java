package xyz.nucleoid.stimuli.event;

import net.minecraft.world.InteractionResult;

/**
 * Represents the change in control flow that should occur in response to an event listener.
 */
public enum EventResult {
    /**
     * Indicates that the event should move on to the next listener.
     */
    PASS,
    /**
     * Indicates that the event should cancel further processing and allow the behavior to occur.
     */
    ALLOW,
    /**
     * Indicates that the event should cancel further processing and prevent behavior from occurring.
     */
    DENY;

    public InteractionResult asActionResult() {
        return switch (this) {
            case ALLOW -> InteractionResult.SUCCESS;
            case DENY -> InteractionResult.FAIL;
            default -> InteractionResult.PASS;
        };
    }
}
