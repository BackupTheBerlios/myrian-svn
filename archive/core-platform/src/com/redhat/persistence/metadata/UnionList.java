/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
