package com.redhat.persistence.jdo;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.*;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.*;
import java.util.*;

/**
 * ReflectionAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/07/12 $
 **/

public class ReflectionAdapter extends Adapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/ReflectionAdapter.java#3 $ by $Author: vadim $, $DateTime: 2004/07/12 12:09:36 $";

    private static final Class[] PERSISTENT = new Class[] { Persistent.class };

    public Object getObject(ObjectType basetype,
                            PropertyMap props,
                            Session ssn) {

        ObjectType type = props.getObjectType();
        Class klass = type.getJavaClass();
        Enhancer en = new Enhancer();
        /*Set entries = props.entrySet();
        Class[] parameters = new Class[entries.size()];
        Object[] args = new Object[parameters.length];
        int index = 0;
        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (props.contains(prop)) {
                parameters[index] = prop.getType().getJavaClass();
                args[index] = props.get(prop);
                index++;
            }
            }*/
        en.setSuperclass(klass);
        en.setInterfaces(PERSISTENT);
        en.setCallback(new PersistentInterceptor(props));

        Persistent result = (Persistent) en.create();
        result.setPersistentSession(ssn);
        return result;
    }

    public void setSession(Object obj, Session ssn) {
        ((Persistent) obj).setPersistentSession(ssn);
    }

    public PropertyMap getProperties(Object obj) {
        return ((Persistent) obj).getPersistentOIDProperties();
    }

    public ObjectType getObjectType(Object obj) {
        return getProperties(obj).getObjectType();
    }

    public static interface Persistent {
        void setPersistentSession(Session ssn);
        PropertyMap getPersistentOIDProperties();
    }

    private static class PersistentInterceptor implements MethodInterceptor {

        private PropertyMap m_props;
        private Session m_ssn;

        public PersistentInterceptor(PropertyMap props) {
            m_props = props;
        }

        public Object intercept(Object ths, Method method, Object[] args,
                                MethodProxy proxy) throws Throwable {
            String name = method.getName();
            if ("getPersistentOIDProperties".equals(name)) {
                return m_props;
            } else if ("setPersistentSession".equals(name)) {
                m_ssn = (Session) args[0];
                return null;
            } else if (name.length() > 3
                       && ((name.startsWith("get") && args.length == 0)
                           || name.startsWith("set") && args.length == 1)
                       && (method.getModifiers() & Modifier.ABSTRACT) != 0) {
                char c = name.charAt(3);
                if (Character.isUpperCase(c)) {
                    String propName =
                        Character.toLowerCase(c) + name.substring(4);
                    Property prop =
                        m_props.getObjectType().getProperty(propName);
                    if (prop != null) {
                        if (name.startsWith("get")) {
                            if (prop.isKeyProperty()) {
                                return m_props.get(prop);
                            } else {
                                return m_ssn.get(ths, prop);
                            }
                        } else {
                            m_ssn.set(ths, prop, args[0]);
                            return null;
                        }
                    }
                }
            }

            return proxy.invokeSuper(ths, args);
        }

    }

}
