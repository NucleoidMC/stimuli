package xyz.nucleoid.stimuli.duck;

public interface PassBowUseTicks {
    default int stimuli$getLastRemainingUseTicks() {
        return -1;
    }
}
