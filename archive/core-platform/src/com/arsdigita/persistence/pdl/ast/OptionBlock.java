/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.pdl.ast;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Defines a set of options to be associated with a metadata element.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/14 $
 **/

public class OptionBlock extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/OptionBlock.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";


    private List m_options = new ArrayList();

    /**
     * Constructs a new OptionBlock.
     **/

    public OptionBlock() {}


    /**
     * Adds an option to this OptionBlock.
     **/

    public void add(Option option) {
        m_options.add(option);
    }

    public void setOptions(com.arsdigita.persistence.metadata.Element el) {
        for (Iterator it = m_options.iterator(); it.hasNext(); ) {
            Option option = (Option) it.next();
            try {
                el.setOption(option.getName(), option.getValue());
            } catch (IllegalArgumentException e) {
                option.error("No such option: " + option.getName());
            }
        }
    }

}
