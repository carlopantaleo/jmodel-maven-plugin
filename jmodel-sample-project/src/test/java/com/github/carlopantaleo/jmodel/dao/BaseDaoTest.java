package com.github.carlopantaleo.jmodel.dao;

import com.github.carlopantaleo.jmodel.basedao.BaseDao;
import com.github.carlopantaleo.jmodel.basedao.Filter;
import com.jmodel.generated.MyTestTable;
import com.jmodel.generated.TestEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class BaseDaoTest {
    @Autowired
    BaseDao<MyTestTable> myTestTableBaseDao;

    @Test
    public void simpleOperationsWork() {
        MyTestTable testTable = new MyTestTable();
        testTable.setPrimaryKey("myKey");
        testTable.setEnumField(TestEnum.ITEM2);
        myTestTableBaseDao.insert(testTable);
        assertEquals(1, myTestTableBaseDao.findAll().size());

        MyTestTable myKey = myTestTableBaseDao.findOne("myKey");
        assertEquals(TestEnum.ITEM2, myKey.getEnumField());
        assertNull(myTestTableBaseDao.findOne("myKey2"));
    }

    @Test
    public void complexOperationsWork() {
        MyTestTable testTable1 = new MyTestTable();
        testTable1.setPrimaryKey("pk1");
        testTable1.setSecondField("a");
        testTable1.setDoubleField(2);
        myTestTableBaseDao.insert(testTable1);

        MyTestTable testTable2 = new MyTestTable();
        testTable2.setPrimaryKey("pk2");
        testTable2.setSecondField("b");
        testTable2.setDoubleField(1);
        testTable2.setTimeField(LocalTime.of(11, 30));
        myTestTableBaseDao.insert(testTable2);

        List<MyTestTable> byFilter = myTestTableBaseDao.findByFilter(Filter.builder()
                .addField("primaryKey", "pk2")
                .build());
        assertEquals(1, byFilter.size());
        assertEquals("b", byFilter.get(0).getSecondField());
        assertEquals(LocalTime.of(11, 30), byFilter.get(0).getTimeField());

        List<MyTestTable> byOrder = myTestTableBaseDao.findByFilter(Filter.builder()
                .addOrder("doubleField", Filter.Order.ASC)
                .build());
        assertEquals(2, byOrder.size());
        assertEquals("b", byOrder.get(0).getSecondField());
    }
}