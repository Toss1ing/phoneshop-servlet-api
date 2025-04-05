package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ExistException;
import com.es.phoneshop.exception.NullDataException;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class GenericDao<T> {
    protected List<T> entities;
    protected ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    protected Long countId;

    protected abstract Long getId(T entity);

    protected abstract void setId(T entity);

    public void save(T entity) {
        reentrantReadWriteLock.writeLock().lock();
        try {
            if (entity == null) {
                throw new NullDataException("Entity cannot be null");
            }

            Long id = getId(entity);
            if (id == null) {
                setId(entity);
                entities.add(entity);
            } else {
                boolean exists = entities.stream().anyMatch(e -> getId(e).equals(id));
                if (exists) {
                    throw new ExistException("Entity already exists");
                }

                entities.add(entity);
            }
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }
}
