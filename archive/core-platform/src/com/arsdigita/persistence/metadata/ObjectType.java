/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.io.PrintStream;

import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.persistence.DataHandler;

/**
 * The ObjectType class is a specialized form of CompoundType that supports
 * inheritence. It also adds the notion of identity by allowing properties to
 * be marked as special "key" properties.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2002/10/01 $
 **/

public class ObjectType extends CompoundType {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/ObjectType.java#7 $ by $Author: rhs $, $DateTime: 2002/10/01 16:08:31 $";

    private static boolean m_optimizeDefault = true;

    public static final void setOptimizeDefault(boolean value) {
        m_optimizeDefault = value;
    }

    public static final boolean getOptimizeDefault() {
        return m_optimizeDefault;
    }

    /**
     * Contains the properties that are part of the key for this object type.
     **/
    private Set m_keys = new HashSet();

    /**
     * The join paths for connecting to other attribute storage tables.
     **/
    private Set m_joinPaths = new HashSet();

    /**
     * The Column used to reference the supertype.
     **/
    private Column m_referenceKey = null;

    /**
     * Points to the supertype.
     **/
    private ObjectType m_super;

    // attributes to be aggressively loaded
    private List m_aggressives = new ArrayList();

    // unique keys
    private Set m_uniqueKeys = new HashSet();

    // Stores the data handler for this type.
    private DataHandler m_dataHandler = null;


    /**
     * Constructs a new base ObjectType of the given name that has no
     * properties.
     *
     * @param name The name of the ObjectType.
     **/

    public ObjectType(String name) {
        this(name, null);
    }


    /**
     * Constructs a new ObjectType of the given name that extends the given
     * supertype.
     *
     * @param name The name of the ObjectType.
     * @param supertype The supertype to extend.
     **/

    public ObjectType(String name, ObjectType supertype) {
        super(name);
        m_super = supertype;
        initOption("OPTIMIZE",
                   getOptimizeDefault() ? Boolean.TRUE : Boolean.FALSE);
        initOption("DATA_HANDLER", null);
    }

    /**
     * Returns the supertype of this ObjectType or null if this is a base
     * type.
     *
     * @return The supertype of this ObjectType or null if this is a base.
     **/

    public ObjectType getSupertype() {
        return m_super;
    }


    /**
     * Returns the base type of this ObjectType (which may simply be the
     * current ObjectType).
     *
     * @return The base type of this ObjectType.
     **/

    public ObjectType getBasetype() {
        ObjectType curr = this;
        while (true) {
            ObjectType supertype = curr.getSupertype();
            if (supertype == null) {
                return curr;
            }
            curr = supertype;
        }
    }

    /**
     * Gets the data handler to be used for instances of this object type.
     **/

    public DataHandler getDataHandler() {
        if (m_dataHandler == null) {
            String className = (String) getOption("DATA_HANDLER");
            if (className != null) {
                try {
                    Class dhImpl = Class.forName(className);
                    m_dataHandler = (DataHandler) dhImpl.newInstance();
                } catch (ClassNotFoundException e) {
                    throw new UncheckedWrapperException(e);
                } catch (InstantiationException e) {
                    throw new UncheckedWrapperException(e);
                } catch (IllegalAccessException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        }

        return m_dataHandler;
    }


    /**
     * Adds the property with the given name to the set of properties defining
     * the key for this ObjectType. This operation can only be performed on
     * base object types.
     *
     * @param name The name of the property to add to the key.
     *
     * @exception IllegalStateException If the object is not a base type.
     * @exception IllegalArgumentException If the given name does not refer to
     *            a property of this object type.
     **/

    public void addKeyProperty(String name) {
        if (m_super != null) {
            throw new IllegalStateException(
                                            "Only base object types may contain keys."
                                            );
        }

        if (!hasDeclaredProperty(name)) {
            throw new IllegalArgumentException(
                                               "No such property: " + name
                                               );
        }

        Property prop = getDeclaredProperty(name);

        if (prop.isCollection()) {
            throw new IllegalArgumentException(
                                               "Property " + prop.getName() + " cannot be part of the " +
                                               "object key because it has multiplicity [0..n]"
                                               );
        }

        m_keys.add(prop);
    }

    /**
     *  Returns true if the given Property is one of the key Properties on this
     *  ObjectType.
     *
     *  @param p The property to check.
     *  @return true if is a key property.
     */
    public boolean isKeyProperty(Property p) {
        if( m_super != null ) {
            return m_super.isKeyProperty(p);
        } else {
            return m_keys.contains(p);
        }

    }

    /**
     *  Returns true if the given Property name belongs to this Object type,
     *  and is one of the key Properties.
     *
     *  @param name The name of a property to check.
     *  @return true if is a key property.
     */
    public boolean isKeyProperty(String name) {
        if ( m_super != null ) {
            return m_super.isKeyProperty(name);
        } else {
            Property p = getDeclaredProperty(name);
            if ( null == p ) {
                return false;
            }
            return m_keys.contains(p);

        }
    }

    /**
     * Returns an Iterator containing all the properties that are part of this
     * ObjectType's key.
     *
     * @return An Iterator containing instances of the Property class.
     *
     * @see Property
     **/

    public Iterator getKeyProperties() {
        if (m_super != null) {
            return m_super.getKeyProperties();
        } else {
            return m_keys.iterator();
        }
    }

    /**
     * Returns an Iterator containing all the properties that are part of this
     * ObjectType. This includes any properties defined in this ObjectType's
     * supertype.
     *
     * @return An Iterator containing instances of the Property class.
     *
     * @see Property
     **/

    public Iterator getProperties() {
        List props = new ArrayList();

        if (m_super != null) {
            for (Iterator it = m_super.getProperties(); it.hasNext(); ) {
                props.add(it.next());
            }
        }

        for (Iterator it = super.getProperties(); it.hasNext(); ) {
            props.add(it.next());
        }

        return props.iterator();
    }

    /**
     * Returns an Iterator containing all the properties that are defined by
     * this ObjectType directly. This does <i>not</i> include any properties
     * that are defined in this ObjectType's supertype.
     *
     * @return An Iterator contianing instances of the Property class.
     *
     * @see Property
     **/

    public Iterator getDeclaredProperties() {
        return super.getProperties();
    }


    /**
     * Returns true if this ObjectType contains a Property with the given
     * name. This includes any Properties inherited from the supertype.
     *
     * @param name The name of the property to query for.
     *
     * @return True if this ObjectType contains a Property with the given
     *         name, false otherwise.
     **/

    public boolean hasProperty(String name) {
        if (m_super == null) {
            return super.hasProperty(name);
        } else {
            return m_super.hasProperty(name) || super.hasProperty(name);
        }
    }


    /**
     * Returns true if this ObjectType directly defines a Property with the
     * given name. This does <i>not</i> any Properties inherited from the
     * supertype.
     *
     * @param name The name of the property to query for.
     *
     * @return True if this ObjectType contains a directly defined Property
     *         with the given name, false otherwise.
     **/

    public boolean hasDeclaredProperty(String name) {
        return super.hasProperty(name);
    }


    /**
     * Returns the Property contained by this ObjectType with the given name
     * or null if no such property exists. This includes any properties that
     * may be defined by the supertype.
     *
     * @param name The name of the property to retrieve.
     *
     * @return An instance of Property or null.
     **/

    public Property getProperty(String name) {
        if (m_super == null) {
            return super.getProperty(name);
        } else {
            Property result = m_super.getProperty(name);
            if (result == null) {
                return super.getProperty(name);
            } else {
                return result;
            }
        }
    }


    /**
     * Returns the Property directly defined by this ObjectType with the given
     * name or null if no such property exists. This does <i>not</i> include
     * any properties that may be defined by the supertype.
     *
     * @param name The name of the property to retrieve.
     *
     * @return An instance of Property or null.
     **/

    public Property getDeclaredProperty(String name) {
        return super.getProperty(name);
    }


    /**
     * Adds a JoinPath to this ObjectType.
     *
     * @param path The JoinPath to add.
     **/

    public void addJoinPath(JoinPath path) {
        m_joinPaths.add(path);
    }


    /**
     * Gets all the JoinPaths contained in this ObjectType.
     *
     * @return An Iterator containing instances of JoinPath.
     *
     * @see JoinPath
     **/

    public Iterator getJoinPaths() {
        return m_joinPaths.iterator();
    }


    /**
     * Sets the reference key. The reference key is used to reference the
     * supertype's table.
     *
     * @param referenceKey The Column used to reference the supertype.
     **/

    public void setReferenceKey(Column referenceKey) {
        m_referenceKey = referenceKey;
    }


    /**
     * Gets the reference key. The reference key is used to reference the
     * supertype's table.
     *
     * @return The Column used to reference the supertype's table.
     **/

    public Column getReferenceKey() {
        return m_referenceKey;
    }

    public Column getColumn() {
        if (m_super == null) {
            return ((Property) getKeyProperties().next()).getColumn();
        }

        if (m_referenceKey != null) {
            return m_referenceKey;
        }

        return m_super.getColumn();
    }

    Set getKeyColumns() {
        Set result = new HashSet();

        if (m_super == null) {
            for (Iterator it = getKeyProperties(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                Column col = prop.getColumn();
                if (col == null) {
                    col = prop.getJoinPath().getJoinElement(0).getFrom();
                }
                result.add(col);
            }
        } else {
            result.add(getColumn());
        }

        return result;
    }

    /**
     * Forces a role reference attribute to be loaded in the default retrieve
     * events created by MDSQL.
     *
     * @param aggressive "path" to an attribute
     */
    public void addAggressiveLoad(String[] aggressive) {
        m_aggressives.add(aggressive);
    }

    /**
     * Returns an iterator of attributes to be aggressively loaded
     *
     * @return an iterator of attributes to be aggressively loaded
     **/
    public Iterator getAggressiveLoads() {
        return m_aggressives.iterator();
    }

    /**
     * Returns an iterator of attributes to be aggressively loaded including
     * those defined on the supertype.
     *
     * @return an iterator of attributes to be aggressively loaded including
     *         those defined on the supertype.
     **/

    public Iterator getAllAggressiveLoads() {
        List result = new ArrayList();
        ObjectType type = this;

        while (type != null) {
            result.addAll(type.m_aggressives);
            type = type.m_super;
        }

        return result.iterator();
    }


    /**
     * Marks the specified properties as being part of a unique key for this
     * object type.
     **/

    public void addUniqueKey(Property[] properties) {
        if (hasUniqueKey(properties)) {
            error("Already has a unique key: " + properties);
        } else {
            m_uniqueKeys.add(properties);
        }
    }

    public boolean hasUniqueKey(Property[] properties) {
        for (Iterator it = m_uniqueKeys.iterator(); it.hasNext(); ) {
            Property[] props = (Property[]) it.next();
            if (Arrays.equals(props, properties)) {
                return true;
            }
        }

        return false;
    }


    /**
     * @see isSubtypeOf(ObjectType)
     **/

    public boolean isSubtypeOf(String qualifiedName) {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        return isSubtypeOf(root.getObjectType(qualifiedName));
    }

    /**
     * Returns true if this ObjectType is a subtype of <i>type</i>. The
     * definition of the subtype relation is that A is a subtype of B if and
     * only if anywhere in code that B appears, A can appear as well and the
     * code will still function. This means that an object type is a subtype
     * of itself.
     *
     * @param type The candidate supertype.
     *
     * @return True if this ObjectType is a subtype of <i>type</i>.
     **/

    public boolean isSubtypeOf(ObjectType type) {
        if (this.equals(type)) {
            return true;
        } else if (m_super != null) {
            return m_super.isSubtypeOf(type);
        } else {
            return false;
        }
    }


    /**
     * Checks if the <code>ObjectType</code> specified by
     * <code>extendedType</code> is a subtype of the <code>ObjectType</code>
     * specified by <code>baseType</code>.
     *
     * @param baseType The base object type.
     * @param extendedType The extended object type.
     *
     * @pre extendedType.isSubtypeOf(baseType)
     *
     * @exception RuntimeException Thrown if <code>extendedType</code>
     * is not a subtype of the <code>ObjectType</code> specified by
     * <code>baseType</code>.
     **/
    public static void verifySubtype(ObjectType baseType,
                                     ObjectType extendedType) {
        if (!extendedType.isSubtypeOf(baseType)) {
            throw new RuntimeException(
                                       "The object type '" + extendedType.getQualifiedName() +
                                       "' is not a subtype of the object type '" +
                                       baseType.getQualifiedName() + "'"
                                       );
        }
    }

    public static void verifySubtype(String baseType,
                                     String extendedType) {
        verifySubtype(
                      MetadataRoot.getMetadataRoot().getObjectType(baseType),
                      MetadataRoot.getMetadataRoot().getObjectType(extendedType)
                      );
    }

    public static void verifySubtype(String baseType,
                                     ObjectType extendedType) {
        ObjectType baseObjectType = MetadataRoot.getMetadataRoot().getObjectType(baseType);
        Assert.assertTrue(baseObjectType != null, "Could not find the ObjectType for the " +
                          "base type.  The base type was: " + baseType + ".");
        verifySubtype(baseObjectType, extendedType);
    }

    public static void verifySubtype(ObjectType baseType,
                                     String extendedType) {
        verifySubtype(
                      baseType,
                      MetadataRoot.getMetadataRoot().getObjectType(extendedType)
                      );
    }


    /**
     * Outputs a serialized representation of this ObjectType on the given
     * PrintStream.
     *
     * The following format is used:
     *
     * <pre>
     *    "object type" &lt;name&gt; [ "extends" &lt;super&gt; ] {
     *        &lt;properties&gt; ";"
     *        [ "object key" (&lt;keyProperties&gt;) ";" ]
     *    }
     * </pre>
     *
     * @param out The PrintStream to use for output.
     * @param printEvents true to print out events, false to not
     **/

    public void outputPDL(PrintStream out, boolean printEvents) {
        out.print("object type " + getName());
        if (m_super != null) {
            out.print(" extends " + m_super.getQualifiedName());
        }
        out.println(" {");

        for (Iterator it = getDeclaredProperties(); it.hasNext(); ) {
            Property p = (Property) it.next();
            if (p.getAssociation() == null) {
                out.print("    ");
                p.outputPDL(out);
                out.println(";");
            }
        }

        if (m_super == null) {
            out.println();
            out.print("    object key (");
            for (Iterator it = getKeyProperties(); it.hasNext(); ) {
                Property p = (Property) it.next();
                out.print(p.getName());
                if (it.hasNext()) {
                    out.print(", ");
                }
            }
            out.println(");");
        } else if (m_referenceKey != null) {
            out.println(" reference key (" + m_referenceKey.getTableName() +
                        "." + m_referenceKey.getColumnName() + ");");
        }

        if (m_aggressives.size() > 0) {
            List aggs = new ArrayList();

            for (Iterator it = m_aggressives.iterator(); it.hasNext(); ) {
                String[] agg = (String[])it.next();

                aggs.add(StringUtils.join(Arrays.asList(agg), "."));
            }

            out.println(" aggressive load (" +
                        StringUtils.join(aggs, ", ") +
                        " );\n");
        }


        if (printEvents) {
            for (int i = 0; i < NUM_EVENT_TYPES; i++) {
                Event event = getEvent(i);
                if (event != null) {
                    out.println();
                    out.print("    " + s_eventTypeText[i] + " ");
                    event.outputPDL(out);
                    out.println();
                }
            }

            for (Iterator it = getDeclaredProperties(); it.hasNext(); ) {
                Property p = (Property) it.next();
                if (p.getAssociation() == null) {
                    p.outputPDLEvents(out);
                }
            }
        }

        out.print("}");
    }

    /**
     * Outputs a serialized representation of this ObjectType on the given
     * PrintStream.
     *
     * The following format is used:
     *
     * <pre>
     *    "object type" &lt;name&gt; [ "extends" &lt;super&gt; ] {
     *        &lt;properties&gt; ";"
     *        [ "object key" (&lt;keyProperties&gt;) ";" ]
     *    }
     * </pre>
     *
     * @param out The PrintStream to use for output.
     **/
    public void outputPDL(PrintStream out) {
        outputPDL(out, true);
    }

    void setNullability() {
        if (m_referenceKey != null) {
            m_referenceKey.setNullable(false);
        }

        for (Iterator it = getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            prop.setNullability();
        }
    }

    void generateUniqueKeys() {
        if (m_referenceKey == null) {
            List keyCols = new ArrayList();
            boolean mdsql = true;
            for (Iterator it = getKeyProperties(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                Column col = prop.getColumn();

                if (col == null) {
                    // Not MDSQL enabled, can't generate key
                    mdsql = false;
                } else {
                    keyCols.add(col);
                }
            }

            if (mdsql) {
                Column[] cols = (Column[]) keyCols.toArray(new Column[0]);

                if (cols.length > 0) {
                    Table table = cols[0].getTable();
                    for (int i = 0; i < cols.length; i++) {
                        if (cols[i].getTable() != table) {
                            error("Not all key columns are from the " +
                                  "same table.");
                        }
                    }
                    if (table.getPrimaryKey() == null) {
                        table.setPrimaryKey(new UniqueKey(table, null, cols));
                    }
                }
            }
        } else {
            Table table = m_referenceKey.getTable();
            if (table.getPrimaryKey() == null) {
                table.setPrimaryKey(new UniqueKey(null, m_referenceKey));
            }
        }

        for (Iterator it = getJoinPaths(); it.hasNext(); ) {
            JoinPath jp = (JoinPath) it.next();
            if (jp.getPath().size() != 1) {
                jp.error("Only length 1 join paths are allowed here.");
            }

            JoinElement je = jp.getJoinElement(0);
            Set keyCols = getKeyColumns();
            Column ext = null;
            if (keyCols.contains(je.getFrom())) {
                ext = je.getTo();
            } else if (keyCols.contains(je.getTo())) {
                ext = je.getFrom();
            } else {
                jp.error("Join path must join key column of object " +
                         "type with extension table.");
            }

            Table table = ext.getTable();
            if (table.getPrimaryKey() == null) {
                table.setPrimaryKey(new UniqueKey(null, ext));
            }
        }

        for (Iterator it = m_uniqueKeys.iterator(); it.hasNext(); ) {
            Property[] props = (Property[]) it.next();
            Column[] cols = new Column[props.length];
            for (int i = 0; i < props.length; i++) {
                cols[i] = props[i].getKeyColumn();
                if (cols[i] == null) {
                    props[i].error(
                                   "Cannot apply unique constraint to this column."
                                   );
                }
            }

            Table table = cols[0].getTable();

            if (table.getUniqueKey(cols) == null) {
                new UniqueKey(table, null, cols);
            }
        }

        /*for (Iterator it = getProperties(); it.hasNext(); ) {
          Property prop = (Property) it.next();
          prop.generateUniqueKeys();
          }*/
    }

    void generateForeignKeys() {
        if (m_referenceKey != null) {
            if (m_super == null) {
                m_referenceKey.error("Cannot specify reference key without " +
                                     "a supertype.");
            }
            if (!m_referenceKey.isForeignKey()) {
                new ForeignKey(null, m_referenceKey, m_super.getColumn(),
                               true);
            }
        }

        for (Iterator it = getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            prop.generateForeignKeys();
        }

        for (Iterator it = getJoinPaths(); it.hasNext(); ) {
            JoinPath jp = (JoinPath) it.next();
            jp.generateForeignKeys(true, false);
        }
    }

}
