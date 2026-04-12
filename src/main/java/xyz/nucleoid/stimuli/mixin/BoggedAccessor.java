package xyz.nucleoid.stimuli.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Bogged.class)
public interface BoggedAccessor {
    @Accessor
    static EntityDataAccessor<Boolean> getDATA_SHEARED() {
        throw new AssertionError();
    }
}
