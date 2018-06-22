package com.github.carlopantaleo.jmodel.dao;

import com.github.carlopantaleo.jmodel.basedao.BaseDao;
import com.jmodel.generated.MyTestTable;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyTestTableDao extends BaseDao<MyTestTable> {
    @Autowired
    public MyTestTableDao(SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(MyTestTable.class);
    }
}
