package com.arsdigita.persistence.oql;

/**
 * Actor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

abstract class Actor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Actor.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public void act(Node node) {}

    public void act(Table table) {}

}
