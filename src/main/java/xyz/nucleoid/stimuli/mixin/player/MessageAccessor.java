package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.server.filter.TextStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextStream.Message.class)
public interface MessageAccessor {
    @Mutable
    @Accessor
    void setRaw(String raw);

    @Mutable
    @Accessor
    void setFiltered(String filtered);
}
