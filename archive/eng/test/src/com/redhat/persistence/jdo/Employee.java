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

import java.math.BigInteger;

public class Employee {
    private String name = null;
    private Float salary = new Float(1);
    private Department dept = null;
    private Address address = new Address();

    public Employee() { }

    public Employee(String name, Department dept) {
        this.name = name;
        this.dept = dept;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public Float getSalary() {
        return salary;
    }

    public Department getDept() {
        return dept;
    }

    public Address getAddress() {
        return address;
    }

}
