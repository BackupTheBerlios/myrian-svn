package com.redhat.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class Main {
    private YAdapter m_yList;


    private Main() {
        m_yList = new YAdapter(List.class);
        m_yList.addInterface(Iterator.class);
    }

    public static void  main(String[] args) {
        new Main().test1();
        new Main().test2();
        new Main().test3();
    }

    private void test1() {
        List list =
            (List) m_yList.newAdapter(new ArrayList(), new LinkedList());

        list.add("foo");
        log("size=" + list.size());
        YAdapter.Exposable guts = (YAdapter.Exposable) list;
        List listOne = (List) guts.getCanonicalImpl();
        List listTwo = (List) guts.getTestedImpl();
        log("implements " + guts.getInterface());
        log("listOne = " + listOne);
        log("listTwo = " + listTwo);
    }

    private void test2() {
        subtest2(null, null, false);
        subtest2(null, "foo", true);
        subtest2("foo", null, true);
        subtest2("foo", "foo", false);
        subtest2("foo", "bar", true);
        log("test2 completed successfully.");
    }

    private void subtest2(Object val1, Object val2, boolean exceptionExpected) {
        Simple simple = (Simple) new YAdapter(Simple.class).newAdapter
            (new SimpleImpl(val1), new SimpleImpl(val2));

        boolean exceptionRaised = false;
        try {
            simple.value();
        } catch (YAdapterException ex) {
            exceptionRaised = true;
        }
        assertTruth(exceptionExpected==exceptionRaised,
               "Unexpectedly, exception " + (exceptionRaised ? "was" : "") +
               " raised.");
    }

    private void test3() {
        final Collection initial =
            Arrays.asList(new String[] {"one", "two", "3", "vier"});

        List list = (List)
            m_yList.newAdapter(new ArrayList(initial), new LinkedList(initial));

        list.add("fuenf");
        log("size=" + list.size());
        Iterator it = list.iterator();
        YAdapter.Exposable guts = (YAdapter.Exposable) it;
        Iterator canonical = (Iterator) guts.getCanonicalImpl();
        Iterator tested    = (Iterator) guts.getTestedImpl();
        assertTruth("one".equals(it.next()), "first element");
        String canonicalSecond = (String) canonical.next();
        String testedSecond    = (String) tested.next();
        assertTruth(canonicalSecond.equals(testedSecond), "second elements");
        log("test3 completed successfully");
    }


    private void assertTruth(boolean truth, String msg) {
        if (!truth) {
            throw new YAdapterException(msg);
        }
    }


    private void log(String str) {
        System.out.println(str);
    }

    private interface Simple {
        Object value();
    }

    private static class SimpleImpl implements Simple {
        private final Object m_value;

        SimpleImpl(Object value) {
            m_value = value;
        }

        public Object value() {
            return m_value;
        }
    }
}
