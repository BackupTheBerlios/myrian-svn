/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.jdo;

import java.util.List;

interface ClassInfo {
    List getAllFields(Class pcClass);
    List getAllTypes(Class pcClass);
    String numberToName(Class pcClass, int fieldNumber);
    Class numberToType(Class pcClass, int fieldNumber);
    /**
     * Returns the first occurrence of the specified field in the most derived
     * class.
     **/
    int nameToNumber(Class pcClass, String fieldName);
}
