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
package com.arsdigita.xml.formatters;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.xml.Formatter;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;

/**
 * An alternate formatter for java.util.Date objects,
 * outputing the date in 'medium' format. The time
 * is ommitted.
 */
public class DateFormatter implements Formatter {
    
    public String format(Object value) {
        Date date = (Date)value;
        
        Locale locale = Kernel.getContext().getLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        DateFormat format = DateFormat.getDateInstance
            (DateFormat.MEDIUM, locale);
        
        return format.format(date);
    }
}
