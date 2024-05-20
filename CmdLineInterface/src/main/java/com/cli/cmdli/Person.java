package com.cli.cmdli;

public class Person {
    private String name;
    private int age;
    private String designation;
    public Person(String name, int age, String designation) {
        this.name = name;
        this.age = age;
        this.designation = designation;
    }
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDesignation() {
        return designation;
    }
}
