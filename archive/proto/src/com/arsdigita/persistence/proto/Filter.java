package com.arsdigita.persistence.proto;

/**
 * Filter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/02/28 $
 **/

public abstract class Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Filter.java#3 $ by $Author: rhs $, $DateTime: 2003/02/28 17:44:25 $";

    public static abstract class Switch {

        public abstract void onAnd(AndFilter f);
        public abstract void onOr(OrFilter f);
        public abstract void onNot(NotFilter f);
        public abstract void onEquals(EqualsFilter f);
        public abstract void onIn(InFilter f);
        public abstract void onContains(ContainsFilter f);
        public abstract void onPassthrough(PassthroughFilter f);

    }

    public abstract void dispatch(Switch sw);

}
