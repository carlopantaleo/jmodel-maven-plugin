package com.github.carlopantaleo.jmodel.dao;

import com.jmodel.generated.AnotherTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class AnotherTableDaoTest {
    @Autowired
    private AnotherTableDao anotherTableDao;

    @Test
    public void extendedDaoWorks() {
        AnotherTable anotherTable = new AnotherTable();
        anotherTable.setAnotherPkField1("a");
        anotherTable.setAnotherPkField2("b");
        anotherTable.setAnotherNonPkField("c");
        anotherTableDao.insert(anotherTable);
        assertEquals(1, anotherTableDao.findAll().size());

        AnotherTable myKey = new AnotherTable();
        myKey.setAnotherPkField1("a");
        myKey.setAnotherPkField2("b");
        myKey = anotherTableDao.findOne(myKey);
        assertEquals("c", myKey.getAnotherNonPkField());

        myKey.setAnotherPkField2("c");
        myKey = anotherTableDao.findOne(myKey);
        assertNull(myKey);
    }
}