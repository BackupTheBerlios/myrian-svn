package com.redhat.persistence.jdo;

import java.math.BigInteger;

public class Employee {
    private String name = null;
    private Float salary = new Float(1);
    private Department dept = null;

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
}
