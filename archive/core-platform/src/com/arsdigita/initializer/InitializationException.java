/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.initializer;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * InitializationException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

public class InitializationException extends UncheckedWrapperException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/initializer/InitializationException.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable rootCause) {
        super(rootCause);
    }

    public InitializationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
