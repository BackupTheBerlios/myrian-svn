package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;

import java.io.*;
import java.util.*;

/**
 * Main
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Main {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Main.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    public static final void main(String[] args) throws Exception {
        PDL pdl = new PDL();
        List expressions = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.endsWith(".pdl")) {
                pdl.load(new FileReader(arg), arg);
            } else {
                OQLParser p = new OQLParser(new FileReader(arg));
                expressions.add(p.expression());
            }
        }

        Root root = new Root();
        pdl.emit(root);

        List types = new ArrayList();
        ObjectType type = new ObjectType(null, "root", null);
        types.add(type);

        for (Iterator it = root.getObjectTypes().iterator(); it.hasNext(); ) {
            ObjectType to = (ObjectType) it.next();
            if (to.isKeyed()) {
                Expression.addKey(to, to.getKeyProperties());
                addPath(types, type, Path.get(to.getQualifiedName()), to);
            }
        }

        for (Iterator it = expressions.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            Frame f = new Frame(null, null);
            f.setType(type);
            f.setCollection(false);
            Environment env = new Environment();
            env.add(e, f);
            env.analyze();
            StringBuffer buf = new StringBuffer();
            env.dump(buf, f);
            System.out.println(buf.toString());
        }

        /*OutputStreamWriter osw = new OutputStreamWriter(System.out);
        PDLWriter w = new PDLWriter(osw);
        for (Iterator it = types.iterator(); it.hasNext(); ) {
            w.write((ObjectType) it.next());
            osw.flush();
            System.out.println();
            }*/
    }

    private static void addPath(List types, ObjectType from, Path path,
                                ObjectType to) {
        if (path.getParent() == null) {
            Property prop = from.getProperty(path.getName());
            if (prop != null) { return; }
            from.addProperty
                (new Role(path.getName(), to, false, to.getRoot() != null,
                          true));
        } else {
            Property prop = from.getProperty(path.getParent());
            ObjectType type;
            if (prop == null) {
                type = new ObjectType(null, path.getPath() + " intermediate",
                                      null);
                types.add(type);
                addPath(types, from, path.getParent(), type);
            } else {
                type = prop.getType();
            }

            addPath(types, type, Path.get(path.getName()), to);
        }
    }

    public static final void main2(String[] args) {
        Model GLOBAL = Model.getInstance("global");
        ObjectType STRING = new ObjectType(GLOBAL, "String", null);
        ObjectType BIGINT = new ObjectType(GLOBAL, "BigInteger", null);
        Model KERNEL = Model.getInstance("kernel");
        ObjectType USER = new ObjectType(KERNEL, "User", null);
        ObjectType GROUP = new ObjectType(KERNEL, "Group", null);
        Property GID = new Role("id", BIGINT, false, false, false);
        GROUP.addProperty(GID);
        GROUP.addProperty(new Role("members", USER, false, true, true));
        Property UID = new Role("id", BIGINT, false, false, false);
        USER.addProperty(UID);
        Property EMAIL = new Role("email", STRING, false, false, false);
        USER.addProperty(EMAIL);
        USER.addProperty(new Role("parent", USER, false, false, true));

        Expression.addKey(GROUP, Collections.singleton(GID));
        Expression.addKey(USER, Collections.singleton(UID));
        Expression.addKey(USER, Collections.singleton(EMAIL));

        Variable users = new Variable("users");
        Variable users1 = new Variable("users");
        Variable users2 = new Variable("users");
        Variable user = new Variable("user");
        Variable user1 = new Variable("user");
        Variable user2 = new Variable("user");
        Variable groups = new Variable("groups");
        Variable group = new Variable("group");
        Variable member = new Variable("member");
        Variable parent = new Variable("parent");
        Variable param = new Variable("param");

        Expression q = new Get
            (new Filter
             (new Join
              (new Define(users, "user"), new Define(groups, "group"),
               new Exists
               (new Filter(new Define(new Get(group, "members"), "member"),
                           new Equals(user1, member)))),
              new Equals(new Get(user2, "email"), param)), "group");

        System.out.println(q);
        System.out.println();
        System.out.println(q.toSQL());

        Environment env = new Environment();
        env.add(q, null);
        Frame uframe = env.getFrame(users);
        uframe.setType(USER);
        uframe.addAllKeys(Expression.getKeys(USER));
        Frame gframe = env.getFrame(groups);
        gframe.setType(GROUP);
        gframe.addAllKeys(Expression.getKeys(GROUP));
        env.analyze();

        System.out.println();
        System.out.print(env);

        q = new Filter
            (new Define(users1, "user"),
             new Exists(new Filter(new Define(users2, "u2"),
                                   new Equals(new Get(new Variable("u2"),
                                                      "email"),
                                              new Get(user, "email")))));
        System.out.println(q);
        System.out.println();
        System.out.println(q.toSQL());

        env = new Environment();
        env.add(q, null);
        uframe = env.getFrame(users1);
        uframe.setType(USER);
        uframe.addAllKeys(Expression.getKeys(USER));
        uframe = env.getFrame(users2);
        uframe.setType(USER);
        gframe.addAllKeys(Expression.getKeys(GROUP));
        env.analyze();

        System.out.println();
        System.out.print(env);
    }

}
