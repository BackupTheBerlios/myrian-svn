package com.redhat.persistence.jdotest;

import com.redhat.persistence.*;
import com.redhat.persistence.jdo.Main;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Expression;
import com.redhat.test.*;
import com.redhat.util.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * PandoraTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public class PandoraTest extends Test {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/PandoraTest.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public static final void main(String[] args) {
        main(PandoraTest.class, args);
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

    private void testClass(Class klass, Object[] values) {
        Session ssn = getSession();
        ObjectType type = ssn.getRoot().getObjectType(klass.getName());
        PropertyMap props = new PropertyMap(type);
        for (int i = 0; i < values.length; i+=2) {
            String name = (String) values[i];
            Object value = values[i+1];
            Property p = type.getProperty(name);
            if (p == null) {
                throw new IllegalArgumentException
                    ("no such property: " + name);
            }
            props.put(p, value);
        }

        List keys = type.getKeyProperties();
        Object[] key = new Object[keys.size()];
        for (int i = 0; i < key.length; i++) {
            key[i] = props.get((Property) keys.get(i));
        }

        Object obj = Main.create(ssn, klass, key);

        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            if (!p.isKeyProperty() && props.contains(p)) {
                String name = setter(p.getName());
                Object[] args =  new Object[] { props.get(p) };
                Method setter = Reflection.dispatch(klass, name, args);
                if (setter == null) {
                    throw new IllegalArgumentException
                        ("no such method: " + p.getName() + " " +
                         Arrays.asList(args));
                }
                try {
                    setter.invoke(obj, args);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                } catch (InvocationTargetException e) {
                    throw new Error(e);
                }
            }
        }

        ssn.flush();

        Expression expr = new All(klass.getName());
        for (int i = 0; i < keys.size(); i++) {
            Property p = (Property) keys.get(i);
            expr = new Filter
                (expr, new Equals(new Variable(p.getName()),
                                  new Literal(props.get(p))));
        }
        Cursor c = Main.cursor(ssn, klass, expr);

        if (c.next()) {
            obj = c.get();
            for (Iterator it = type.getProperties().iterator();
                 it.hasNext(); ) {
                Property p = (Property) it.next();
                if (!props.contains(p)) { continue; }
                Method getter = Reflection.dispatch
                    (klass, getter(p.getName()), new Object[0]);
                if (getter == null) { continue; }
                try {
                    Object result = getter.invoke(obj, null);
                    assertEquals(result, props.get(p), "wrong " + p.getName());
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
        testClass(Picture.class, new Object[] {
            "id", new Integer(100),
            "caption", "Pandora's Box",
            "content", getImageBytes("com/redhat/persistence/jdotest/pandora.jpg")
        });
    }

    public void testProduct() {
        testClass(Product.class, new Object[] {
            "id", new Integer(1000),
            "name", "Test Product",
            "price", new Float(19.99)
        });
    }

    public void testUser() {
        testClass(User.class, new Object[] {
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
        testClass(Group.class, new Object[] {
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
        Session ssn = getSession();

        Picture[] pictures = new Picture[10];
        for (int i = 0; i < pictures.length; i++) {
            pictures[i] = (Picture) Main.create
                (ssn, Picture.class, new Object[] { new Integer(i) });
            pictures[i].setCaption("Caption " + i);
            pictures[i].setContent(new byte[32]);
        }

        Product[] products = new Product[10];
        for (int i = 0; i < products.length; i++) {
            products[i] = (Product) Main.create
                (ssn, Product.class, new Object[] { new Integer(i) });
            products[i].setName("Product " + i);
            products[i].setPrice((float)3.14);
            products[i].setPicture(pictures[i]);
        }

        User rhs = (User) Main.create
            (ssn, User.class, new Object[] { new Integer(0) });
        rhs.setName("Rafael H. Schloming");
        rhs.setEmail("rhs@mit.edu");
        List aux = rhs.getAuxiliaryEmails();
        YAdapter ad = new YAdapter(List.class);
        ad.addInterface(Iterator.class);
        ad.addInterface(ListIterator.class);
        aux = (List) ad.newAdapter(new ArrayList(), aux);
        //System.out.println("aux: " + aux);
        aux.add("fdsa@asdf.com");
        aux.add("fdsa@asdf.com");
        aux.add("two@asdf.com");
        aux.add("fdsa@asdf.com");

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
        }

        for (int i = 0; i < 10; i++) {
            Order o = (Order) Main.create
                (ssn, Order.class, new Object[] { new Integer(i) });
            o.setParty(rhs);
            Collection items = o.getItems();
            for (int j = 0; j < 10; j++) {
                Item item = (Item) Main.create
                    (ssn, Item.class, new Object[] { new Integer(j + 10*i) });
                item.setProduct(products[i]);
                items.add(item);
            }
        }

        Cursor c = Main.all(ssn, Order.class);
        while (c.next()) {
            Main.lock(ssn, c.get());
        }

        ssn.flushAll();

        Class[] klasses =
            new Class[] { Product.class, Picture.class, Order.class,
                          User.class };
        for (int i = 0; i < klasses.length; i++) {
            c = Main.all(ssn, klasses[i]);
            while (c.next()) {
                ssn.delete(c.get());
            }
        }

        ssn.flushAll();
    }

}
