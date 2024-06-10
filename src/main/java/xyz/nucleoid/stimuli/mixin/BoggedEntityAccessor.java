package xyz.nucleoid.stimuli.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.BoggedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BoggedEntity.class)
public interface BoggedEntityAccessor {
    @Accessor
    static TrackedData<Boolean> getSHEARED() {
        throw new AssertionError();
    }
}
