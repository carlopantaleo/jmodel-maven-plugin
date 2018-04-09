package com.carlopantaleo.jmodel.dao;

import com.jmodel.generated.MyTestTable;
import com.jmodel.generated.TestEnum;
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
public class MyTestTableDaoTest {
    @Autowired
    private MyTestTableDao myTestTableDao;

    @Test
    public void extendedDaoWorks() {
        MyTestTable testTable = new MyTestTable();
        testTable.setPrimaryKey("myKey2");
        testTable.setEnumField(TestEnum.ITEM2);
        myTestTableDao.insert(testTable);
        assertEquals(1, myTestTableDao.findAll().size());

        MyTestTable myKey = myTestTableDao.findOne("myKey2");
        assertEquals(TestEnum.ITEM2, myKey.getEnumField());
        assertNull(myTestTableDao.findOne("myKey"));
    }
}