package com.arsdigita.persistence.proto;

/**
 * FilterSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public interface FilterSource {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/FilterSource.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    AndFilter getAnd(Filter leftOperand, Filter rightOperand);

    OrFilter getOr(Filter leftOperand, Filter rightOperand);

    NotFilter getNot(Filter operand);

}
