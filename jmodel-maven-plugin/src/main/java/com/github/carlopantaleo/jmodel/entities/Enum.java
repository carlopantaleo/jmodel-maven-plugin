package com.github.carlopantaleo.jmodel.entities;

import java.util.ArrayList;
import java.util.List;

public class Enum {
    private String name;
    private List<String> items = new ArrayList<>();

    public void addItem(String item) {
        items.add(item);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
