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

package com.arsdigita.persistence.pdl.ast;

/**
 * Defines an option to be associated with a metadata element.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class Option extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/Option.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    String m_name;
    Object m_value;

    public Option(String name, Object value) {
	m_name = name;
	m_value = value;
    }

    public String getName() {
	return m_name;
    }

    public Object getValue() {
	return m_value;
    }

}
