package com.carlopantaleo.jmodel.entities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name;
    private String className;
    private List<Field> fields = new ArrayList<>();
    private List<Field> pk = new ArrayList<>();

    public void addField(Field field) {
        fields.add(field);
    }

    public void addPkField(Field pkField) {
        pk.add(pkField);
    }

    public @Nullable Field getField(String name) {
        return getFieldFromList(name, fields);
    }

    public @Nullable Field getPkField(String name) {
        return getFieldFromList(name, pk);
    }

    private @Nullable Field getFieldFromList(String name, List<Field> fieldList) {
        for (Field field : fieldList) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getPk() {
        return pk;
    }

    public void setPk(List<Field> pk) {
        this.pk = pk;
    }
}
