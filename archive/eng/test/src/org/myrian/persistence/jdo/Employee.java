/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
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
 */
package org.myrian.persistence.jdo;

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

    public void setDepartment(Department dept) {
        this.dept = dept;
    }

    public Department getDept() {
        return dept;
    }

    public Address getAddress() {
        return address;
    }

}
