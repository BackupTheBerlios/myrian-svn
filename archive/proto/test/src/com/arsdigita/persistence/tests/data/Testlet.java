package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import com.arsdigita.util.*;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Testlet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public abstract class Testlet {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/tests/data/Testlet.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private static final Logger LOG = Logger.getLogger(Testlet.class);

    protected static final int KEY = 0x1;
    protected static final int ATTRIBUTE = 0x2;
    protected static final int ROLE = 0x4;
    protected static final int OPTIONAL = 0x8;
    protected static final int REQUIRED = 0x10;
    protected static final int COLLECTION = 0x20;
    protected static final int COMPONENT = 0x40;
    protected static final int COMPOSITE = 0x80;

    protected static final boolean test(Property prop, final int flags) {
        if ((KEY & flags) > 0 && prop.isKeyProperty()) {
            return true;
        } else if ((ATTRIBUTE & flags) > 0 &&
                   prop.isAttribute()) {
            return true;
        } else if ((ROLE & flags) > 0 &&
                   prop.isRole()) {
            return true;
        } else if ((OPTIONAL & flags) > 0 &&
                   prop.isNullable()) {
            return true;
        } else if ((REQUIRED & flags) > 0 &&
                   prop.isRequired()) {
            return true;
        } else if ((COLLECTION & flags) > 0 &&
                   prop.isCollection()) {
            return true;
        } else if ((COMPONENT & flags) > 0 &&
                   prop.isComponent()) {
            return true;
        } else if ((COMPOSITE & flags) > 0 &&
                   prop.isComposite()) {
            return true;
        }

        return false;
    }

    protected static final ObjectTree makeTree(ObjectType type, int include,
                                         int exclude, int depth) {
        ObjectTree result = new ObjectTree(type);
        addPaths(result, include, exclude, depth);
        return result;
    }

    private static final void addPaths(ObjectTree tree, int include,
                                       int exclude, int depth) {
        ObjectType type = tree.getObjectType();
        for (Iterator it = type.getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (test(prop, include) && !test(prop, exclude)) {
                tree.addPath(prop.getName());
                if (prop.isRole()) {
                    ObjectTree subtree = tree.getSubtree(prop.getName());
                    if (depth > 0) {
                        addPaths(subtree, include, exclude, depth - 1);
                    } else {
                        addPaths(subtree, REQUIRED, exclude, 0);
                    }
                }
            }
        }
    }

    private static final boolean isCircular(ObjectTree tree, Property prop) {
        if (tree.getObjectType().equals(prop.getType())) {
            return true;
        } else {
            ObjectTree parent = tree.getParent();
            if (parent == null) {
                return false;
            } else {
                return isCircular(parent, prop);
            }
        }
    }

    protected static final void verify(DataObject data, ObjectTree tree,
                                       DataSource ds) {
        if (data == null) {
            Assert.fail("Null data object for " + tree.getAbsolutePath() +
                        ", expected: " + ds.getOID(tree) +
                        ", tree: " + tree.getRoot());
        }

        LOG.warn("Comparing " + data.getOID() + " with tree:\n" + tree +
                 " against the following datasource: " + ds.getKey());
        for (Iterator it = tree.getAttributes().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            Assert.assertEquals(ds.getTestData(tree, prop.getName()),
                                data.get(prop.getName()),
                                tree.getAbsolutePath(prop.getName()),
                                tree.getAbsolutePath(prop.getName()));
        }

        for (Iterator it = tree.getSubtrees().iterator(); it.hasNext(); ) {
            ObjectTree subtree = (ObjectTree) it.next();

            verify((DataObject) data.get(subtree.getName()), subtree, ds);
        }
    }

    protected static final DataObject create(ObjectTree tree, DataSource ds) {
        OID oid = ds.getOID(tree);

        Session ssn = SessionManager.getSession();
        LOG.warn("Creating " + oid + " for " + tree.getAbsolutePath());
        DataObject result = ssn.create(oid);

        for (Iterator it = tree.getAttributes().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            if (!prop.isKeyProperty()) {
                Object td = ds.getTestData(tree, prop.getName());
                LOG.warn("Setting " +
                         tree.getAbsolutePath(prop.getName()) + " to " + td);
                result.set(prop.getName(), td);
            }
        }

        for (Iterator it = tree.getSubtrees().iterator(); it.hasNext(); ) {
            ObjectTree subtree = (ObjectTree) it.next();
            Property prop = tree.getProperty(subtree.getName());

            DataObject data = create(subtree, ds);
            data.save();

            if (prop.isCollection()) {
                LOG.warn("Adding " + data.getOID() +
                         " to " + tree.getAbsolutePath(prop.getName()));
                DataAssociation da =
                    (DataAssociation) result.get(prop.getName());
                da.add(data);
            } else {
                LOG.warn("Setting " +
                         tree.getAbsolutePath(prop.getName()) + " to " +
                         data.getOID());
                result.set(prop.getName(), data);
            }
        }

        return result;
    }

    protected static final void update(DataObject data, ObjectTree tree,
                                       DataSource ds) {
        for (Iterator it = tree.getAttributes().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            if (!prop.isKeyProperty()) {
                data.set(prop.getName(),
                         ds.getTestData(tree, prop.getName()));
            }
        }

        for (Iterator it = tree.getSubtrees().iterator(); it.hasNext(); ) {
            ObjectTree subtree = (ObjectTree) it.next();
            Property prop = tree.getProperty(subtree.getName());

            DataObject child = (DataObject) data.get(prop.getName());
            update(child, subtree, ds);
            child.save();
        }
    }

    public abstract void run();

}
