package com.arsdigita.persistence.proto;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

abstract class DataSet {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/DataSet.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    public abstract Cursor getCursor();

}
