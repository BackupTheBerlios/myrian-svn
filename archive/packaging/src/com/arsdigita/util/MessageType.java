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

package com.arsdigita.util;

/**
 * Special MIME types useful for typing Message objects.
 *
 * @author Ron Henderson 
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/MessageType.java#1 $
 */

public interface MessageType {

    /**
     * MIME type of "text/html"
     */
    public final static String TEXT_HTML = "text/html";

    /**
     * MIME type of "text/plain"
     */
    public final static String TEXT_PLAIN = "text/plain";

    /**
     * MIME type of "text/plain" with a special format qualifier that
     * text should displayed as formatted.
     */
    public final static String TEXT_PREFORMATTED =
        TEXT_PLAIN + "; format=preformatted";
}
