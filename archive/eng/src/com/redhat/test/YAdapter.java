package com.redhat.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * This class allows you to compare two different implementations of the same
 * interface side by side.
 *
 * See {@link #newAdapter(Object, Object)} for details.
 *
 * @since 2004-05-21
 * @author Vadim Nasardinov (vadimn@redhat.com)
 **/
public final class YAdapter {
    private final Class m_iface;
    private final Set  m_adaptableIfaces;

    /**
     * Creates a Y adapter capable of producing implementations of the interface
     * <code>iface</code>.
     *
     * @see #newAdapter(Object, Object)
     *
     * @param iface specifies the interface whose implementations will be
     * returned by {@link #newAdapter(Object, Object)}.
     **/
    public YAdapter(Class iface) {
        if (iface==null) { throw new NullPointerException("iface"); }
        m_adaptableIfaces = new HashSet();
        m_iface = iface;
        m_adaptableIfaces.add(m_iface);
    }

    /**
     * Adds to the set of adaptable return types.
     *
     * 
     **/
    public void addInterface(Class iface) {
        m_adaptableIfaces.add(iface);
    }

    void addInterfaces(Set ifaces) {
        m_adaptableIfaces.addAll(ifaces);
    }

    /**
     * This returns an object that implements two interfaces: the specified
     * interface <code>iface</code> and {@link YAdapter.Exposable}.
     * 
     * <p>The <code>iface</code> interface is implemented by delegating all
     * method calls to <code>canonicalImpl</code> and <code>testedImpl</code>.
     * To ground this in a concrete example, suppose you're adapting the {@link
     * java.util.List} interface.  Let's call the return value of this method
     * <code>yAdapter</code>.  The <code>yAdapter</code> object implements the
     * <code>List</code> interface.  Say, you call {@link
     * java.util.List#iterator() yAdapter.iterator()} on it.  The return value
     * of this call is computed as follows.</p>
     *
     * <p>The call is delegated to both <code>canonicalImpl</code> and
     * <code>testedImpl</code>.  Say, these return iterators <code>it1</code>
     * <code>it2</code>, respectively.  One or both of this values will be used
     * to construct the return value of the call to
     * <code>yAdapter.iterator()</code>.  The following logic is used.</p>
     * 
     * <p>Declared return type <em>T</em> of the invoked method is examined.  In
     * our example, the return type is {@link java.util.Iterator}.  If this
     * interface had previously been made known to the <code>YAdapter</code>
     * instance that produced <code>yAdapter</code> via a call to {@link
     * #addInterface(Class)}, then the returned value will itself be a Y adaptor
     * that combines <code>i1</code> and <code>i2</code>.</p>
     *
     * <p>If <em>T</em> is not an adaptable return type, then the value produced
     * by <code>canonicalImpl</code> is tested for equality against the value
     * produced by <code>testedImpl</code>.  If the two are equal, the latter is
     * returned.  Otherwise, an exception is raised signalling an error in
     * <code>testedImpl</code>'s implementation of the invoked method.</p>
     *
     * @param canonicalImpl the canonical implementation of the interface
     * <code>iface</code> against which <code>testedImpl</code> is to be tested.
     * @param testedImpl the implementation of <code>iface</code> to be tested
     * against the canonical implementation.
     *
     * @throws YAdapterException if value returned by <code>canonicalImpl</code>
     * is not equal to that returned by <code>testedImpl</code>.
     **/
    public Object newAdapter(Object canonicalImpl, Object testedImpl) {
        assertImplements(canonicalImpl);
        assertImplements(testedImpl);

        // TODO: What's the right classloader to pass to newProxyInstance?
        return Proxy.newProxyInstance
            (YAdapter.class.getClassLoader(),
             new Class[] {m_iface, Exposable.class},
             new Handler(m_iface, m_adaptableIfaces, canonicalImpl, testedImpl));
    }

    private void assertImplements(Object obj) {
        if (!m_iface.isInstance(obj)) {
            throw new YAdapterException
                (obj + " does not implement " + m_iface);
        }
    }

    private static class Handler implements InvocationHandler {
        private static final Method s_getIface   = method("getInterface");
        private static final Method s_getCanonicalImpl = method("getCanonicalImpl");
        private static final Method s_getTestedImpl = method("getTestedImpl");

        private final Class m_iface;
        private final Set m_adaptableIfaces;
        private final Object m_canonical;
        private final Object m_tested;

        Handler(Class iface, Set adaptableIfaces,
                Object canonical, Object tested) {

            m_iface = iface;
            m_adaptableIfaces = adaptableIfaces;
            m_canonical = canonical;
            m_tested = tested;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

            if (Exposable.class.equals(method.getDeclaringClass())) {
                return dispatchToExposable(method);
            } 

            Object canonicalResult = method.invoke(m_canonical, args);
            Object testedResult = method.invoke(m_tested, args);

            if (m_adaptableIfaces.contains(method.getReturnType())) {
                YAdapter adapter = new YAdapter(method.getReturnType());
                adapter.addInterfaces(m_adaptableIfaces);
                return adapter.newAdapter(canonicalResult, testedResult);
            } else {
                assertEquals(canonicalResult, testedResult);
                return testedResult;
            }
        }

        private Object dispatchToExposable(Method method) {
            if ( method.equals(s_getIface)) { return m_iface; }
            if ( method.equals(s_getCanonicalImpl)) { return m_canonical; }
            if ( method.equals(s_getTestedImpl)) { return m_tested; }

            throw new IllegalStateException("Unreachable statement.");
        }

        private static Method method(String name) {
            try {
                return Exposable.class.getMethod(name, new Class[] {});
            } catch (NoSuchMethodException ex) {
                throw new YAdapterException("can't happen", ex);
            }
        }
    }

    /**
     * @throws YAdapterException
     **/
    private static void assertEquals(Object obj1, Object obj2) {
        if (obj1==null && obj2==null) { return; }
        if (obj1==null || obj2==null
            || !obj1.equals(obj2) || !obj2.equals(obj1)) {

            throw new YAdapterException
                ("unequal: obj1=" + obj1 + "; obj2=" + obj2);
        }
    }


    /**
     * This gives you access to the objects for which the object returned by
     * {@link #newAdapter(Object, Object)} is proxying.
     **/
    public interface Exposable {
        /**
         * Returns the interface implementation by the adapter.
         **/
        Class getInterface();

        /**
         * Exposes the object used as the canonical implementation of the {@link
         * #getInterface() interface}.
         *
         * @see YAdapter#newAdapter(Object,Object)
         **/
        Object getCanonicalImpl();

        /**
         * Exposes the object whose implementation of the {@link #getInterface()
         * interface} is being tested.
         *
         * @see YAdapter#newAdapter(Object,Object)
         **/
        Object getTestedImpl();
    }
}
