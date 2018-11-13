package com.github.carlopantaleo.jmodel.basedao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseDao<T extends Serializable> {
    private Class<T> clazz;
    private final SessionFactory sessionFactory;

    public BaseDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setClazz(Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    @Transactional
    public T findOne(Serializable id) {
        return getCurrentSession().get(clazz, id);
    }

    @Transactional
    public List<T> findByFilter(Filter filter) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(clazz);
        Root<T> entityRoot = criteriaQuery.from(clazz);
        criteriaQuery.select(entityRoot);

        for (Map.Entry<String, Object> entry : filter.getFields().entrySet()) {
            criteriaQuery.where(builder.equal(entityRoot.get(entry.getKey()), entry.getValue()));
        }

        List<Order> order = new ArrayList<>();
        for (Map.Entry<String, Filter.Order> entry : filter.getOrder().entrySet()) {
            Path<Object> objectPath = entityRoot.get(entry.getKey());
            Order objOrder = entry.getValue() == Filter.Order.ASC ? builder.asc(objectPath) : builder.desc(objectPath);
            order.add(objOrder);
        }
        criteriaQuery.orderBy(order);

        return getCurrentSession().createQuery(criteriaQuery).getResultList();
    }

    @Transactional
    public List<T> findAll() {
        return findByFilter(Filter.builder().build());
    }

    @Transactional
    public void insert(T entity) {
        getCurrentSession().persist(entity);
    }

    @Transactional
    public void update(T entity) {
        getCurrentSession().merge(entity);
    }

    @Transactional
    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    @Transactional
    public void deleteById(Serializable entityId) {
        T entity = findOne(entityId);
        delete(entity);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}