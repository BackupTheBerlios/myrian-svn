/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.util;

/**
 * Assertion failure.
 *
 * @see Assert#fail(String)
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since  2003-05-21
 * @version $Revision: #2 $ $Date: 2004/04/05 $
 **/
public class AssertionError extends Error {

    public AssertionError(String msg) {
        super(msg);
    }
}
