/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

/**
 * RightJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class RightJoin extends LeftJoin {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/RightJoin.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    public RightJoin(Expression left, Expression right, Expression condition) {
        super(right, left, condition);
    }

}
