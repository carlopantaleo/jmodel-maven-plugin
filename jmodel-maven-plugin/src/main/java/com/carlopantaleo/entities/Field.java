package com.carlopantaleo.entities;

import java.util.Objects;

public class Field {
    private String name;
    private Class type;
    private int lenght;
    private boolean nullable = false;
    private String defaultVal;
    private String referredEnum;
    private boolean pk;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }

    public String getReferredEnum() {
        return referredEnum;
    }

    public void setReferredEnum(String referredEnum) {
        this.referredEnum = referredEnum;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Field field = (Field) o;
        return lenght == field.lenght &&
                nullable == field.nullable &&
                pk == field.pk &&
                Objects.equals(name, field.name) &&
                Objects.equals(type, field.type) &&
                Objects.equals(defaultVal, field.defaultVal) &&
                Objects.equals(referredEnum, field.referredEnum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, lenght, nullable, defaultVal, referredEnum, pk);
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", lenght=" + lenght +
                ", nullable=" + nullable +
                ", defaultVal='" + defaultVal + '\'' +
                ", referredEnum='" + referredEnum + '\'' +
                ", pk=" + pk +
                '}';
    }
}
