package com.arsdigita.persistence;
import com.arsdigita.persistence.metadata.*;
import com.arsdigita.util.StringUtils;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import junit.framework.*;
import java.math.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;


/**
 * PropertyManipulator - This class essentially implements a Visitor/Template Method for
 * DataObjects and their Properties.  The entry point to the framework is 
 * method manipulateProperties. This method takes a DataObject and an implementation
 * of the nested interface, PredicateManipulator, as arguments. The framework iterates
 * over all Properties in the DataObject. For each property that obeys the predicate of
 * PredicateManipulator, an action is taken on that Property and/or DataObject. The
 * methods to be implemented by an implementation of PredicateManipulator are obeys, 
 * the predicate method, and manipulate, the method that manipulates the DataObject and
 * Property.
 * 
 * For convenience, several partial implementations of PredicateManipulator are provided
 * for common cases as Attribute Properties, Key Properties, etc.
 *  
 * @author <a href="mailto:jorris@arsdigita.com"Jon Orris</a>
 * @version $Revision: #3 $ $Date: 2002/08/13 $
 */
public class PropertyManipulator {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/PropertyManipulator.java#3 $ by $Author: dennis $, $DateTime: 2002/08/13 11:53:00 $";
    private static final Logger s_log =
        Logger.getLogger(PropertyManipulator.class.getName());
    static  {
        s_log.setPriority(Priority.DEBUG);
    }
    
    /**
     *  Predicate provides the obeys method for checking some truth
     *  value on a Property.
     */ 
    public interface Predicate {
        /**
         *  @param p The Property to check.
         *  @return true if p obeys some predicate.
         */          
        public boolean obeys(Property p);
    }

    /**
     *  Predicate provides the manipulate method for manipulating some DataObject
     *  and/or Property of the DataObject.
     */ 
    public interface Manipulator {
        /**
         *  Performs some function on the Property and/or DataObject.
         *
         *  @param p The Property
         *  @param data The DataObject that p belongs to.
         *  
         *  @pre data.getObjectType.hasProperty(p.getName());
         *
         *  @throws Exception on any error in manipulation.
         */
        public void manipulate(Property p, DataObject data) throws Exception;
    }

    // Utility class for tying manipulators and predicates, used in most instances.
    // Predicate and Manipulator are separate interfaces to allow separate reuse.
    // Granted, I can't immediately see why you'd want to.
    public interface PredicateManipulator extends Predicate, Manipulator {
        
    }

    /**
     * Partial implementation of PredicateManipulator.
     * obeys is true for Attributes
     */ 
    public static abstract class AttributeManipulator implements PredicateManipulator {
        public boolean obeys(Property p) {
            return p.isAttribute();
        }
    }

    /**
     * Partial implementation of PredicateManipulator.
     * obeys is true for non key Attributes
     */ 
    public static abstract class NonKeyManipulator extends AttributeManipulator {
        public NonKeyManipulator(ObjectType type) {
            m_type = type;
        }
        public boolean obeys(Property p) {
            return super.obeys(p) && !m_type.isKeyProperty(p);
        }

        final ObjectType m_type;
    }

    /**
     * Partial implementation of PredicateManipulator.
     * obeys is true for Component properties.
     */ 
    public static abstract class ComponentManipulator implements PredicateManipulator {
        public boolean obeys(Property p) {
            return p.isComponent();
        }
    }

    /**
     * Specialization of ComponentManipulator.
     * obeys is true for non Collection Component properties.
     */ 
    public static abstract class SingleComponentManipulator extends ComponentManipulator {
        public boolean obeys(Property p) {
            return super.obeys(p) && !p.isCollection();
        }
    }

    /**
     * Specialization of ComponentManipulator.
     * obeys is true for non Component properties that are a Collection.
     */ 
    public static abstract class MultiComponentManipulator extends ComponentManipulator {
        public boolean obeys(Property p) {
            return super.obeys(p) && p.isCollection();
        }
    }

    /**
     * Partial implementation of PredicateManipulator.
     * obeys is true for Associations, defined as non component RoleReferences.
     */ 
    public static abstract class AssociationManipulator implements PredicateManipulator {
        public boolean obeys(Property p) {
            return p.isRole() && !p.isComponent();
        }
    }

    /**
     *  This method is the entry point of the framework. It iterates over all  
     *  Properties of the DataObject. Using the PredicateManipulator, it determines
     *  which Properties obey some predicate. Those that do are passed to the manipulate
     *  method. 
     */ 
    public static void manipulateProperties(DataObject data, PredicateManipulator manip) throws Exception {
        manipulateProperties(data, manip, manip);        
    }
   
    /**
     *  This method implements the functionality of the two arg method above. It is used in case
     *  the Predicate and Manipulator are different instances. 
     */ 
    public static void manipulateProperties(DataObject data, Predicate pred, Manipulator manip) throws Exception {
        ObjectType type = data.getObjectType();
        Iterator props = type.getProperties();

        while(props.hasNext()) {
            Property p = (Property) props.next();
            if (pred.obeys(p)) {
                manip.manipulate(p, data);
            }
        }
        
    }


}
