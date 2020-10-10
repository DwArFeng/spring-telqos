package com.dwarfeng.springtelqos.impl.service;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 交互信息。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
class InteractionInfo {

    private Lock lock;
    private Condition condition;
    private InteractionStatus interactionStatus;
    private String nextMessage;
    private boolean termination;

    public InteractionInfo() {
    }

    public InteractionInfo(
            Lock lock, Condition condition, InteractionStatus interactionStatus, String nextMessage,
            boolean termination) {
        this.lock = lock;
        this.condition = condition;
        this.interactionStatus = interactionStatus;
        this.nextMessage = nextMessage;
        this.termination = termination;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public InteractionStatus getInteractionStatus() {
        return interactionStatus;
    }

    public void setInteractionStatus(InteractionStatus interactionStatus) {
        this.interactionStatus = interactionStatus;
    }

    public String getNextMessage() {
        return nextMessage;
    }

    public void setNextMessage(String nextMessage) {
        this.nextMessage = nextMessage;
    }

    public boolean isTermination() {
        return termination;
    }

    public void setTermination(boolean termination) {
        this.termination = termination;
    }

    @Override
    public String toString() {
        return "InteractionInfo{" +
                "lock=" + lock +
                ", condition=" + condition +
                ", interactionStatus=" + interactionStatus +
                ", nextMessage='" + nextMessage + '\'' +
                ", termination=" + termination +
                '}';
    }
}
