package com.eremix.app;

import java.util.LinkedList;

public class BoundedStack<T> {
    private LinkedList<T> stack;
    private int maxSize;

    public BoundedStack(int maxSize) {
        this.stack = new LinkedList<>();
        this.maxSize = maxSize;
    }

    public BoundedStack() {

    }

    public void push(T element) {
        stack.addFirst(element);
        if (stack.size() > maxSize) {
            stack.removeLast();
        }
    }

    public T pop() {
        return stack.removeFirst();
    }

    public int size() {
        return stack.size();
    }
}
