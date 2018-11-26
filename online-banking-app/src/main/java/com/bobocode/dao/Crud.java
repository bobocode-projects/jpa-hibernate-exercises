package com.bobocode.dao;

import java.util.List;

public interface Crud<T> {
    void save(T element);

    T find(Long id);

    List<T> findAll();

    void update(T element);

    void remove(T element);
}
