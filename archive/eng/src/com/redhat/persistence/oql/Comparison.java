/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

public class Comparison extends BinaryCondition {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Comparison.java#1 $ by $Author: ashah $, $DateTime: 2004/07/15 12:07:20 $";

    private final String m_operator;

    public Comparison(Expression left, Expression right, String comparator) {
        super(left, right);
        m_operator = comparator;
    }

    String getOperator() {
        return m_operator;
    }
}
