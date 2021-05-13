package xyz.nucleoid.stimuli.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public final class WorldEvents {
    public static final StimulusEvent<ExplosionDetonated> EXPLOSION_DETONATED = StimulusEvent.create(ExplosionDetonated.class, ctx -> (explosion, particles) -> {
        try {
            for (ExplosionDetonated listener : ctx.getListeners()) {
                listener.onExplosionDetonated(explosion, particles);
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
    });

    public static final StimulusEvent<OpenNetherPortal> OPEN_NETHER_PORTAL = StimulusEvent.create(OpenNetherPortal.class, ctx -> (world, lowerCorner) -> {
        try {
            for (OpenNetherPortal listener : ctx.getListeners()) {
                ActionResult result = listener.onOpenNetherPortal(world, lowerCorner);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<IgniteTnt> IGNITE_TNT = StimulusEvent.create(IgniteTnt.class, ctx -> (world, pos, igniter) -> {
        try {
            for (IgniteTnt listener : ctx.getListeners()) {
                ActionResult result = listener.onIgniteTnt(world, pos, igniter);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<SummonWither> SUMMON_WITHER = StimulusEvent.create(SummonWither.class, ctx -> (world, pos) -> {
        try {
            for (SummonWither listener : ctx.getListeners()) {
                ActionResult result = listener.onSummonWither(world, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<FireTick> FIRE_TICK = StimulusEvent.create(FireTick.class, ctx -> (world, pos) -> {
        try {
            for (FireTick listener : ctx.getListeners()) {
                ActionResult result = listener.onFireTick(world, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<MeltIce> MELT_ICE = StimulusEvent.create(MeltIce.class, ctx -> (world, pos) -> {
        try {
            for (MeltIce listener : ctx.getListeners()) {
                ActionResult result = listener.onIceMelt(world, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<FluidFlow> FLUID_FLOW = StimulusEvent.create(FluidFlow.class, ctx -> {
        return (world, fluidPos, fluidBlock, flowDirection, flowTo, flowToBlock) -> {
            try {
                for (FluidFlow listener : ctx.getListeners()) {
                    ActionResult result = listener.onFluidFlow(world, fluidPos, fluidBlock, flowDirection, flowTo, flowToBlock);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return ActionResult.PASS;
        };
    });

    public interface ExplosionDetonated {
        void onExplosionDetonated(Explosion explosion, boolean particles);
    }

    public interface OpenNetherPortal {
        ActionResult onOpenNetherPortal(ServerWorld world, BlockPos lowerCorner);
    }

    public interface IgniteTnt {
        ActionResult onIgniteTnt(ServerWorld world, BlockPos pos, @Nullable LivingEntity igniter);
    }

    public interface SummonWither {
        ActionResult onSummonWither(ServerWorld world, BlockPos pos);
    }

    public interface FireTick {
        ActionResult onFireTick(ServerWorld world, BlockPos pos);
    }

    public interface MeltIce {
        ActionResult onIceMelt(ServerWorld world, BlockPos pos);
    }

    public interface FluidFlow {
        ActionResult onFluidFlow(ServerWorld world, BlockPos fluidPos, BlockState fluidBlock, Direction flowDirection, BlockPos flowTo, BlockState flowToBlock);
    }
}
