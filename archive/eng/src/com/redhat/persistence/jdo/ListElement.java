package com.redhat.persistence.jdo;

/**
 * ListElement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public abstract class ListElement {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/ListElement.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public abstract Number getIndex();

}
