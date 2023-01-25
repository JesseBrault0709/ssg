package com.jessebrault.gcp.tokenizer;

final class Counter {

    private int count = 0;

    public void increment() {
        this.count++;
    }

    public void decrement() {
        this.count--;
    }

    public boolean isZero() {
        return this.count == 0;
    }

    @Override
    public String toString() {
        return "Counter(" + this.count + ")";
    }

}
