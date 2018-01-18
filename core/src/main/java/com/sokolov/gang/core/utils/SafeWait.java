package com.sokolov.gang.core.utils;

public class SafeWait {

    public SafeWait(int millis) {
        synchronized (this) {
            try {
                wait(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
