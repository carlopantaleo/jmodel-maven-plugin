package com.carlopantaleo.jmodel.configuration;

import com.carlopantaleo.jmodel.basedao.BaseDao;
import com.jmodel.generated.MyTestTable;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntitiesConfiguration {
    @Bean
    BaseDao<MyTestTable> myTestTableBaseDao(SessionFactory sessionFactory) {
        BaseDao<MyTestTable> myTestTableBaseDao = new BaseDao<>(sessionFactory);
        myTestTableBaseDao.setClazz(MyTestTable.class);
        return myTestTableBaseDao;
    }
}
