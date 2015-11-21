package com.centro.dao;

import com.centro.model.CentroQuery;


public interface CentroQueryDao {
    public void insert(CentroQuery centroQuery);
    public CentroQuery findById(int id);
}
