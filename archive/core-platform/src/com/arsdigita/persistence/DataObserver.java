package com.arsdigita.persistence;

/**
 * DataObserver
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public abstract class DataObserver {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataObserver.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public void beforeSave(DataObject object) {}

    public void afterSave(DataObject object) {}

    public void beforeDelete(DataObject object) {}

    public void afterDelete(DataObject object) {}

}
