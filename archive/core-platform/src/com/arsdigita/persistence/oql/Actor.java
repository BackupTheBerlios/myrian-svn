package com.arsdigita.persistence.oql;

/**
 * Actor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/06/10 $
 **/

abstract class Actor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Actor.java#2 $ by $Author: rhs $, $DateTime: 2002/06/10 15:35:38 $";

    public void act(Query query) {}

    public void act(Node node) {}

    public void act(Table table) {}

}
