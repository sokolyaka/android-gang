package com.sokolov.gang.core.utils;

public class SafeInterrupt {
    private final Thread thread;

    public SafeInterrupt(Thread thread) {
        this.thread = thread;
    }

    public void interruptAndJoin() {
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
