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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Expression.java#4 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public static Expression valueOf(Path path) {
        if (path.getParent() == null) {
            return new Variable(path.getName());
        } else {
            return new Get(valueOf(path.getParent()), path.getName());
        }
    }

    abstract void frame(Generator generator);

    abstract Code emit(Generator generator);

    abstract void hash(Generator generator);

    abstract String summary();

}
