package com.arsdigita.persistence;

import java.util.*;

/**
 * FakeDataHandler
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class FakeDataHandler extends DataHandler {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/FakeDataHandler.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public static final Set DELETED = new HashSet();

    public void doDelete(DataObject data) {
        DELETED.add(data);
        super.doDelete(data);
    }

}
