package com.bookstore.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.service.BaseService;

public abstract class BaseServiceImpl<T, ID, R extends JpaRepository<T, ID>> implements BaseService<T, ID> {
    
    protected final R repository;
    
    protected BaseServiceImpl(R repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }
    
    @Override
    @Transactional
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }
    
    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }
    
    @Override
    public List<T> findAll() {
        return repository.findAll();
    }
    
    @Override
    @Transactional
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void delete(T entity) {
        repository.delete(entity);
    }
    
    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
} 