/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.jdo;

import org.myrian.persistence.Cursor;
import org.myrian.persistence.Engine;
import org.myrian.persistence.PropertyMap;
import org.myrian.persistence.QuerySource;
import org.myrian.persistence.Session;
import org.myrian.persistence.engine.rdbms.ConnectionSource;
import org.myrian.persistence.engine.rdbms.PostgresWriter;
import org.myrian.persistence.engine.rdbms.RDBMSEngine;
import org.myrian.persistence.jdo.C;
import org.myrian.persistence.jdo.PersistenceManagerImpl;
import org.myrian.persistence.metadata.ObjectType;
import org.myrian.persistence.metadata.Property;
import org.myrian.persistence.metadata.Root;
import org.myrian.persistence.oql.*;
import org.myrian.persistence.oql.Expression;
import org.myrian.persistence.pdl.PDL;
import org.myrian.persistence.pdl.Schema;
import org.myrian.test.YAdapter;
import org.myrian.util.Reflection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * PandoraTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class PandoraTest extends WithTxnCase {
    private final static Logger s_log = Logger.getLogger(PandoraTest.class);

    private Session m_ssn;

    public PandoraTest() {}

    public PandoraTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        PersistenceManagerImpl pm = (PersistenceManagerImpl) m_pm;
        m_ssn = pm.getSession();
    }

    private byte[] getImageBytes(String resource) {
        InputStream is = getClass().getClassLoader()
            .getResourceAsStream(resource);
        if (is == null) {
            throw new IllegalArgumentException
                ("no such resource: " + resource);
        }
        try {
            try {
                List bytes = new ArrayList();
                while (true) {
                    int b = is.read();
                    if (b == -1) {
                        break;
                    } else {
                        bytes.add(new Byte((byte) b));
                    }
                }
                byte[] result = new byte[bytes.size()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = ((Byte) bytes.get(i)).byteValue();
                }
                return result;
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new Error(e.getMessage());
        }
    }

    private String prependStudly(String prefix, String str) {
        return prefix + Character.toUpperCase(str.charAt(0)) +
            str.substring(1);
    }

    private String getter(String prop) {
        return prependStudly("get", prop);
    }

    private String setter(String prop) {
        return prependStudly("set", prop);
    }

    private void testObject(Object pc, Object[] values) {
        final Class klass = pc.getClass();
        Map props = new LinkedHashMap();
        for (int i = 0; i < values.length; i+=2) {
            String name = (String) values[i];
            Object value = values[i+1];
            props.put(name, value);
        }

        ObjectType type = m_ssn.getRoot().getObjectType(klass.getName());

        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String key = (String) me.getKey();
            String name = setter(key);
            Object[] args =  new Object[] { me.getValue() };
            Method setter = Reflection.dispatch(klass, name, args);
            if (setter == null) {
                if (type.isKeyProperty(key)) { continue; }
                throw new IllegalArgumentException
                    ("no such method: " + name + " " + Arrays.asList(args));
            }
            try {
                setter.invoke(pc, args);
            } catch (IllegalAccessException e) {
                throw new Error(e);
            } catch (InvocationTargetException e) {
                throw new Error(e);
            }
        }

        m_ssn.flush();

        Expression expr = new All(klass.getName());
        List keys = type.getKeyProperties();

        for (int i = 0; i < keys.size(); i++) {
            Property p = (Property) keys.get(i);
            expr = new Filter
                (expr, new Equals(new Variable(p.getName()),
                                  new Literal(props.get(p.getName()))));
        }
        Cursor c = C.cursor(m_ssn, klass, expr);

        if (c.next()) {
            Object obj = c.get();
            for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                String key = (String) me.getKey();
                String name = getter(key);
                Method getter =
                    Reflection.dispatch(klass, name, new Object[0]);
                if (getter == null) { continue; }
                try {
                    Object result = getter.invoke(obj, null);
                    _assertEquals("wrong " + name, result, props.get(key));
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                } catch (InvocationTargetException e) {
                    throw new Error(e);
                }
            }
        } else {
            fail("no rows");
        }

        if (c.next()) {
            fail("too many rows");
        }
    }

    public void testPicture() {
        Picture picture = new Picture(100);
        m_pm.makePersistent(picture);
        testObject(picture, new Object[] {
            "id", new Integer(100),
            "caption", "Pandora's Box",
            "content", getImageBytes("org/myrian/persistence/jdo/pandora.jpg")
        });
    }

    public void testProduct() {
        Product product = new Product(1000);
        m_pm.makePersistent(product);
        testObject(product, new Object[] {
            "id", new Integer(1000),
            "name", "Test Product",
            "price", new Float(19.99)
        });
    }

    public void testUser() {
        User rhs = new User(10000);
        m_pm.makePersistent(rhs);
        testObject(rhs, new Object[] {
            "id", new Integer(10000),
            "email", "rhs@planitia.org",
            "name", "Rafael H. Schloming",
            "auxiliaryEmails", Arrays.asList(new String[] {
                "rhs@mit.edu",
                "rafaels@redhat.com",
                "foo@asdf.com"
            })
        });
    }

    public void testGroup() {
        Group group = new Group(10001);
        m_pm.makePersistent(group);
        testObject(group, new Object[] {
            "id", new Integer(10001),
            "email", "group@planitia.org",
            "name", "Group",
            "auxiliaryEmails", Arrays.asList(new String[] {
                "foo@asdf.com",
                "asdf@foo.com",
                "group@asdf.com"
            })
        });
    }

    public void testMain() {
        Picture[] pictures = new Picture[10];
        for (int i = 0; i < pictures.length; i++) {
            pictures[i] = new Picture(i);
            m_pm.makePersistent(pictures[i]);
            pictures[i].setCaption("Caption " + i);
            pictures[i].setContent(new byte[32]);
        }

        Product[] products = new Product[10];
        for (int i = 0; i < products.length; i++) {
            products[i] = new Product(i);
            m_pm.makePersistent(products[i]);

            products[i].setName("Product " + i);
            products[i].setPrice((float)3.14);
            products[i].setPicture(pictures[i]);
        }

        final User rhs = new User(0);
        rhs.setName("Rafael H. Schloming");
        rhs.setEmail("rhs@mit.edu");
        m_pm.makePersistent(rhs);

        List aux = rhs.getAuxiliaryEmails();
        YAdapter ad = new YAdapter(List.class);
        ad.addInterface(Iterator.class);
        ad.addInterface(ListIterator.class);
        aux = (List) ad.newAdapter(new ArrayList(), aux);
        aux.add("fdsa@asdf.com");
        aux.add("fdsa@asdf.com");
        aux.add("two@asdf.com");
        aux.add("fdsa@asdf.com");

        for (Iterator it = aux.iterator(); it.hasNext(); ) {
            it.next();
            if (!it.hasNext()) {
                try {
                    it.next();
                } catch (NoSuchElementException nsme) {
                    // expected behavior
                    break;
                }
            }
        }

        aux.indexOf("fdsa@asdf.com");
        aux.lastIndexOf("fdsa@asdf.com");
        aux.indexOf("asdf");
        aux.get(2);

        aux.set(1, "one@asdf.com");
        aux.get(1);

        for (ListIterator it = aux.listIterator(); it.hasNext(); ) {
            it.nextIndex();
            it.previousIndex();
            it.next();
        }

        aux.set(1, null);

        for (ListIterator it = aux.listIterator(); it.hasNext(); ) {
            it.nextIndex();
            it.previousIndex();
            it.next();
        }

        for (ListIterator it = aux.listIterator(aux.size());
             it.hasPrevious(); ) {
            it.previousIndex();
            it.nextIndex();
            it.previous();
        }

        aux.size();

        aux.remove(2);

        for (ListIterator it = aux.listIterator(); it.hasNext(); ) {
            it.nextIndex();
            it.previousIndex();
            it.next();
        }

        aux.add(1, "one@asdf.com");

        for (ListIterator it = aux.listIterator(); it.hasNext(); ) {
            it.nextIndex();
            it.previousIndex();
            it.next();
            if (!it.hasNext()) {
                try {
                   it.next();
                } catch (NoSuchElementException nsme) {
                    // expected behavior
                    break;
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            Order order = new Order();

            order.setId(i);
            m_pm.makePersistent(order);

            order.setParty(rhs);
            Set items = order.getItems();
            for (int j = 0; j < 10; j++) {
                Item item = new Item(j + 10*i);
                m_pm.makePersistent(item);
                item.setProduct(products[i]);
                items.add(item);
            }
        }

        Cursor c = C.all(m_ssn, Order.class);
        while (c.next()) {
            C.lock(m_ssn, c.get());
        }

        m_ssn.flushAll();

        Class[] klasses =
            new Class[] { Product.class, Picture.class, Order.class,
                          User.class };
        for (int i = 0; i < klasses.length; i++) {
            c = C.all(m_ssn, klasses[i]);
            while (c.next()) {
                m_ssn.delete(c.get());
            }
        }

        m_ssn.flushAll();
        m_pm.currentTransaction().commit();
    }

   private void _assertEquals(String message, Object o1, Object o2) {
       if (o1 == null) { assertTrue(message, o2 == null); }
       if (o2 == null) { assertTrue(message, o1 == null); }

       if (o1.getClass().isArray() && o2.getClass().isArray()) {
           int l1 = Array.getLength(o1);
           int l2 = Array.getLength(o2);
           assertEquals(message, l1, l2);
           for (int i = 0; i < l1; i++) {
               assertEquals(message, Array.get(o1, i), Array.get(o2, i));
           }
       } else {
           assertTrue(message, o1.equals(o2) && o2.equals(o1));
       }
   }
}
