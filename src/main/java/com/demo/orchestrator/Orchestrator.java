package com.demo.orchestrator;

public interface Orchestrator<I, O> {
    O orchestrate(I i);
}
