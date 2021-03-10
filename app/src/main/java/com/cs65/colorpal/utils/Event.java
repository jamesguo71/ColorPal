package com.cs65.colorpal.utils;

// https://github.com/googlecodelabs/kotlin-coroutines/blob/07656e93624bf06d58a5c030c2a765fe6b73de3f/kotlin-coroutines-end/app/src/main/java/com/example/android/kotlincoroutines/util/ConsumableValue.kt

public class Event<T> {
    private boolean hasBeenHandled = false;
    private final T content;

    public final boolean getHasBeenHandled() {
        return this.hasBeenHandled;
    }

    public final T getContentIfNotHandled() {
        if (this.hasBeenHandled) {
            return null;
        } else {
            this.hasBeenHandled = true;
             return this.content;
        }
    }

    public final T peekContent() {
        return this.content;
    }

    public Event(T content) {
        this.content = content;
    }
}
