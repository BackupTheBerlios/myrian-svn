/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.persistence;

/**
 * StaticLinkTest
 *
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public class StaticLinkTest extends LinkTest {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/StaticLinkTest.java#4 $ by $Author: dennis $, $DateTime: 2004/08/16 18:10:38 $";

    public StaticLinkTest(String name) {
        super(name);
    }

    String getModel() {
        return "linkTest";
    }
}
