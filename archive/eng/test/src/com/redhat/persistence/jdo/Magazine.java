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

import java.util.HashMap;
import java.util.Map;

public class Magazine {
    private int id;
    private String title;
    private Map index;

    public Magazine() {}

    public Magazine(int id) {
        this.id = id;
        index = new HashMap();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map getIndex() {
        return index;
    }

    public void setMap(Map index) {
        this.index = index;
    }

    public String toString() {
        return "<magazine #" + id + ">";
    }
}
