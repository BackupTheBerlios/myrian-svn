/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.pdl;

/**
 * PDLException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 */

public class PDLException extends Exception {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/pdl/PDLException.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public PDLException(String message) {
        super(message);
    }

}
