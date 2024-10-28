package xyz.nucleoid.stimuli.event;

import net.minecraft.util.ActionResult;

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

    public ActionResult asActionResult() {
        return switch (this) {
            case ALLOW -> ActionResult.SUCCESS;
            case DENY -> ActionResult.FAIL;
            default -> ActionResult.PASS;
        };
    }
}
