package com.arsdigita.persistence.proto;

public class TestSession {

    private TestSession() { } // no construction allowed

    public static void testCommit(Session ssn) { ssn.testCommit(); }
}
