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
package com.redhat.persistence.metadata;

import java.util.AbstractList;
import java.util.List;

class UnionList extends AbstractList {
    private List m_left;
    private List m_right;

    UnionList(List left, List right) {
        m_left = left;
        m_right = right;
    }

    public Object get(int index) {
        // 0 1 2 (size 3), 3 4 5
        int leftsize = m_left.size();
        if (index >= leftsize) {
            return m_right.get(index - leftsize);
        } else {
            return m_left.get(index);
        }
    }

    public int size() {
        return m_left.size() + m_right.size();
    }
}
