package com.arsdigita.persistence.proto;

/**
 * Filter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/01/13 $
 **/

public abstract class Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Filter.java#2 $ by $Author: rhs $, $DateTime: 2003/01/13 16:40:35 $";

    public static abstract class Switch {

        public abstract void onAnd(AndFilter f);
        public abstract void onOr(OrFilter f);
        public abstract void onNot(NotFilter f);
        public abstract void onEquals(EqualsFilter f);
        public abstract void onIn(InFilter f);
        public abstract void onContains(ContainsFilter f);

    }

    public abstract void dispatch(Switch sw);

}
