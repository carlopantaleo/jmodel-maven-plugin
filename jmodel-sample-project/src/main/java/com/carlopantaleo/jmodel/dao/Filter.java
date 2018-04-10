package com.carlopantaleo.jmodel.dao;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class Filter {
    private final ImmutableMap<String, Object> fields;
    private final ImmutableMap<String, Order> order;

    private Filter(ImmutableMap<String, Object> fields,
                   ImmutableMap<String, Order> order) {
        this.fields = fields;
        this.order = order;
    }

    public static Builder builder() {
        return new Builder();
    }

    ImmutableMap<String, Object> getFields() {
        return fields;
    }

    ImmutableMap<String, Order> getOrder() {
        return order;
    }

    public static class Builder {
        private final Map<String, Object> fields = new HashMap<>();
        private final Map<String, Order> order = new HashMap<>();

        public Builder addField(String name, Object value) {
            fields.put(name, value);
            return this;
        }

        public Builder addOrder(String name, Order order) {
            this.order.put(name, order);
            return this;
        }

        public Filter build() {
            return new Filter(ImmutableMap.copyOf(fields), ImmutableMap.copyOf(order));
        }
    }

    public enum Order {
        ASC,
        DESC
    }
}
