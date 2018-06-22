package com.github.carlopantaleo.jmodel.entities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TableTest {
    @Test
    public void fieldAreAddedAndRetrieved() {
        Field field1 = new Field();
        field1.setName("F1");
        Field field2 = new Field();
        field2.setName("F2");
        Field field3 = new Field();
        field3.setName("F3");

        Table table = new Table();
        table.addField(field1);
        table.addField(field2);
        table.addField(field3);
        table.addPkField(field1);
        table.addPkField(field2);

        assertEquals("F1", table.getField("F1").getName() );
        assertEquals("F2", table.getField("F2").getName() );
        assertEquals("F3", table.getField("F3").getName() );
        assertEquals("F1", table.getPkField("F1").getName() );
        assertEquals("F2", table.getPkField("F2").getName() );
        assertNull(table.getPkField("F3"));
    }
}