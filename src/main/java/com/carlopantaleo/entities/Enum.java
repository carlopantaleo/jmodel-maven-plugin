package com.carlopantaleo.entities;

import java.util.List;

public class Enum {
    private String name;
    private List<String> options;

    public void addOption(String option) {
        options.add(option);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
