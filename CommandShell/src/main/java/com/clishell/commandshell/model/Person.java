package com.clishell.commandshell.model;


public record Person(String name, int age, String address) {

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

}
