package xyz.nucleoid.stimuli.event;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public final class DroppedItemsResult {
    private static final DroppedItemsResult DENY = new DroppedItemsResult(EventResult.PASS, Collections.emptyList());

    private final EventResult result;
    private final List<ItemStack> dropStacks;

    private DroppedItemsResult(EventResult result, List<ItemStack> dropStacks) {
        this.result = result;
        this.dropStacks = dropStacks;
    }

    public EventResult result() {
        return this.result;
    }

    public List<ItemStack> dropStacks() {
        return this.dropStacks;
    }

    @Override
    public String toString() {
        return "DroppedItemsResult{result=" + this.result + ", dropStacks=" + this.dropStacks + "}";
    }

    public static DroppedItemsResult pass(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(EventResult.PASS, dropStacks);
    }

    public static DroppedItemsResult allow(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(EventResult.ALLOW, dropStacks);
    }

    public static DroppedItemsResult deny() {
        return DENY;
    }
}