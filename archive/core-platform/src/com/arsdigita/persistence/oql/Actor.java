package com.arsdigita.persistence.oql;

/**
 * Actor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 **/

abstract class Actor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Actor.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public void act(Query query) {}

    public void act(Node node) {}

    public void act(Table table) {}

}
