package com.redhat.persistence;

public class DuplicateObjectException extends ProtoException {

    private final Object m_object;

    DuplicateObjectException(Object object) { m_object = object; }

    public Object getObject() { return m_object; }

    public String getMessage() {
        return "object : " + m_object + " already exists";
    }
}
