package xyz.nucleoid.stimuli.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public interface ObjectPool<T extends PooledObject<T>> {
    static <T extends PooledObject<T>> ObjectPool<T> create(int capacity, PooledObject.Factory<T> factory) {
        if (capacity == 1) {
            return new UnaryAtomic<>(factory);
        } else {
            return new FixedAtomic<>(capacity, factory);
        }
    }

    T acquire();

    void release(T object);

    final class UnaryAtomic<T extends PooledObject<T>> implements ObjectPool<T> {
        private final AtomicReference<T> object = new AtomicReference<>();
        private final PooledObject.Factory<T> factory;

        UnaryAtomic(PooledObject.Factory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T acquire() {
            var object = this.object.getAndSet(null);
            if (object == null) {
                object = this.factory.create(this);
            }

            return object;
        }

        @Override
        public void release(T object) {
            this.object.compareAndSet(null, object);
        }
    }

    final class FixedAtomic<T extends PooledObject<T>> implements ObjectPool<T> {
        private final int capacity;

        private final AtomicReferenceArray<T> array;
        private final AtomicInteger pointer = new AtomicInteger(-1);

        private final PooledObject.Factory<T> factory;

        FixedAtomic(int capacity, PooledObject.Factory<T> factory) {
            this.capacity = capacity;
            this.array = new AtomicReferenceArray<>(capacity);
            this.factory = factory;
        }

        @Override
        public T acquire() {
            while (true) {
                int pointer = this.pointer.get();

                // we've fallen outside the pool: allocate a new entry
                if (pointer < 0) {
                    return this.factory.create(this);
                }

                if (this.pointer.compareAndSet(pointer, pointer - 1)) {
                    var object = this.array.getAndSet(pointer, null);
                    if (object == null) {
                        // this value hasn't been set yet: try again
                        continue;
                    }
                    return object;
                }
            }
        }

        @Override
        public void release(T object) {
            while (true) {
                int pointer = this.pointer.get();
                int newPointer = pointer + 1;

                // the pool is full, we don't need to return this object
                if (newPointer >= this.capacity) {
                    return;
                }

                if (this.pointer.compareAndSet(pointer, newPointer)) {
                    this.array.set(newPointer, object);
                    return;
                }
            }
        }
    }
}
