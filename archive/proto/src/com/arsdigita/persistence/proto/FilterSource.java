package com.arsdigita.persistence.proto;

/**
 * FilterSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public interface FilterSource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/FilterSource.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    AndFilter getAnd(Filter leftOperand, Filter rightOperand);

    OrFilter getOr(Filter leftOperand, Filter rightOperand);

    NotFilter getNot(Filter operand);

    EqualsFilter getEquals(Path path, Object value);

    InFilter getIn(Path path, Query query);

    ContainsFilter getContains(Path path, Object value);

}
