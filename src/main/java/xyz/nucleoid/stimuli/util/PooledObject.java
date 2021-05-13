package xyz.nucleoid.stimuli.util;

import org.jetbrains.annotations.Nullable;

public abstract class PooledObject<S extends PooledObject<S>> implements AutoCloseable {
    private final ObjectPool<S> owner;

    protected PooledObject(@Nullable ObjectPool<S> owner) {
        this.owner = owner;
    }

    protected abstract void release();

    @Override
    @SuppressWarnings("unchecked")
    public final void close() {
        if (this.owner != null) {
            this.release();
            this.owner.release((S) this);
        }
    }

    public interface Factory<T extends PooledObject<T>> {
        T create(ObjectPool<T> pool);
    }
}
