package com.redhat.persistence;

public abstract class EngineException extends ProtoException {

    protected EngineException() { super(); }

    protected EngineException(String msg) { super(msg); }
}
