/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 * 
 * A general utility class for text which carries additional type
 * information.  Specifically, we recognize plain text, HTML, and
 * preformatted text.
 *
 * @author Kevin Scaldeferri 
 */

public class TypedText {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/TypedText.java#7 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_PREFORMATTED =
        TEXT_PLAIN + "; format=preformatted";


    private String m_text;
    private String m_type;

    public TypedText(String text, String type) {
        m_text = text;
        m_type = type;
    }


    public String getText() {
        return m_text;
    }

    public void setText(String text) {
        m_text = text;
    }

    public String getType() {
        return m_type;
    }

    public void setType(String type) {
        m_type = type;
    }

    /**
     * Generates a version of the text renderable as HTML based on
     * the type.
     */
    public String getHTMLText() {
        if (m_text == null) {
            return "";
        }

        // Should probably change this to a state pattern
        if (m_type.equals(TEXT_HTML)) {
            return m_text;
        } else if (m_type.equals(TEXT_PREFORMATTED)) {
            return "<pre>" + m_text + "</pre>";
        } else if (m_type.equals(TEXT_PLAIN)) {
            return StringUtils.textToHtml(m_text);
        } else {
            // catch-all... this is where the state pattern would be nice
            return StringUtils.textToHtml(m_text);
        }
    }

    /**
     * Returns true if the text and type are both equal
     */
    public boolean equals(Object o) {
        if (o instanceof TypedText) {
            TypedText t = (TypedText) o;
            return getText().equals(t.getText())
                && getType().equals(t.getType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        // getHTMLText implies this can be null. Of course, m_type isn't guaranteed to be non-null either,
        // and both are used in equals(). Class invariants need to be better thought out, but since this class isn't
        // presently, this should be enough for now.
        if (null != m_text) {
            result = 37*result + m_text.hashCode();
        }

        result = 37*result + m_type.hashCode();
        return result;
    }

}
