/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.utils.future;
import java.util.concurrent.*;

/**
 * Basic implementation of the {@link Future} interface. <tt>BasicFuture<tt>
 * can be put into a completed state by invoking any of the following methods:
 * {@link #cancel()}, {@link #failed(Throwable)}, or {@link #completed(Object)}.
 *
 * @param <T> the future result type of an asynchronous operation.
 * @since 4.2
 */
public class BasicFuture<T> implements Future<T>, Cancellable {
    private final    FutureCallback<T> callback;
    private volatile boolean           completed;
    private volatile boolean           cancelled;
    private volatile T                 result;
    private volatile Throwable         ex;

    public BasicFuture() {
        super();
        this.callback = null;
    }

    public BasicFuture(T value) {
        this();
        this.completed = true;
        this.result = value;
    }

    public BasicFuture(final FutureCallback<T> callback) {
        super();
        this.callback = callback;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isDone() {
        return this.completed;
    }

    private T getResult() throws ExecutionException {
        if (this.ex != null) {
            throw new ExecutionException(this.ex.getMessage(), this.ex);
        }
        return this.result;
    }

    public synchronized T get() throws InterruptedException, ExecutionException {
        while (!this.completed) {
            wait();
        }
        return getResult();
    }

    public synchronized T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (unit == null) {
            throw new NullPointerException("Time unit");
        }
        final long msecs = unit.toMillis(timeout);
        final long startTime = (msecs <= 0) ? 0 : System.currentTimeMillis();
        long waitTime = msecs;
        if (this.completed) {
            return getResult();
        } else if (waitTime <= 0) {
            throw new TimeoutException();
        } else {
            for (; ; ) {
                wait(waitTime);
                if (this.completed) {
                    return getResult();
                } else {
                    waitTime = msecs - (System.currentTimeMillis() - startTime);
                    if (waitTime <= 0) {
                        throw new TimeoutException();
                    }
                }
            }
        }
    }

    public boolean completed(final T result) {
        synchronized (this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.result = result;
            notifyAll();
        }
        if (this.callback != null) {
            this.callback.completed(result);
        }
        return true;
    }

    public boolean failed(final Throwable exception) {
        synchronized (this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.ex = exception;
            notifyAll();
        }
        if (this.callback != null) {
            this.callback.failed(exception);
        }
        return true;
    }

    public boolean cancel(final boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (this.completed) {
                return false;
            }
            this.completed = true;
            this.cancelled = true;
            notifyAll();
        }
        if (this.callback != null) {
            if (this.callback instanceof CancellFutureCallback) {
                ((CancellFutureCallback) this.callback).cancelled();
            } else {
                this.failed(new CancellationException());
            }
        }
        return true;
    }

    public boolean cancel() {
        return cancel(true);
    }
}
