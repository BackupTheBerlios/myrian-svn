package com.redhat.persistence;

public class TestSession {

    private TestSession() { } // no construction allowed

    public static void testCommit(Session ssn) { ssn.testCommit(); }
}
