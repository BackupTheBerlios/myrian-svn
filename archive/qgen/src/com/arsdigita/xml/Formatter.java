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

package com.arsdigita.xml;

/**
 * An interface providing an API for converting an object
 * to a string. Thus instead of doing
 * <pre>
 *  Date today = new Date();
 *  element.addAttribute("today", date.toString());
 * </pre>
 * we can do:
 * <pre>
 *  Date today = new Date();
 *  element.addAttribute("today", XML.format(date));
 * </pre>
 * Or if you require a non-default format:
 * <pre>
 *  Date today = new Date();
 *  element.addAttribute("today", new DateTimeFormatter.format(today));
 * </pre>
 */
public interface Formatter {
    String format(Object value);
}
