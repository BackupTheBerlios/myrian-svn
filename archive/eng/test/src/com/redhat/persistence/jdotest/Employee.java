package com.redhat.persistence.jdotest;

import java.math.BigInteger;

public class Employee {
    private BigInteger id = null;
    private String name = null;
    private Float salary = new Float(1);
    private Department dept = null;

    public Employee() { }

    public Employee(BigInteger id, String name, Department dept) {
        this.id = id;
        this.name = name;
        this.dept = dept;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public BigInteger getId() {
        return id;
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

    public boolean equals(Object o) {
        if (o instanceof Employee) {
            Employee e = (Employee) o;
            if (id == null) {
                return (e.id == null);
            } else {
                return id.equals(e.id);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return getId() == null ? 0 : id.hashCode();
    }

    public String toString() {
        return "emp " + getId();
    }
}
