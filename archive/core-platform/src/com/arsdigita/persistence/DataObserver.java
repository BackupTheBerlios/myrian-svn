package com.arsdigita.persistence;

/**
 * DataObserver
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/06/14 $
 **/

public abstract class DataObserver {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataObserver.java#2 $ by $Author: rhs $, $DateTime: 2002/06/14 12:22:31 $";

    public void set(DataObject object, String property, Object previous,
                    Object value) {}

    public void add(DataObject object, String property, DataObject value) {}

    public void remove(DataObject object, String property, DataObject value) {}

    public void clear(DataObject object, String property) {}

    public void beforeSave(DataObject object) {}

    public void afterSave(DataObject object) {}

    public void beforeDelete(DataObject object) {}

    public void afterDelete(DataObject object) {}

}
