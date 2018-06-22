package com.github.carlopantaleo.jmodel.dao;

import com.github.carlopantaleo.jmodel.basedao.BaseDao;
import com.jmodel.generated.AnotherTable;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AnotherTableDao extends BaseDao<AnotherTable> {
    @Autowired
    public AnotherTableDao(SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(AnotherTable.class);
    }
}
