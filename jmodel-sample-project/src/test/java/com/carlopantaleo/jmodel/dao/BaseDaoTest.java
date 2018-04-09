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
public class BaseDaoTest {
    @Autowired
    public BaseDao<MyTestTable> myTestTableBaseDao;

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
}