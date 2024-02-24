package com.example.demo.Repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.Entity.DataJDBCEntity;

public interface DataJDBCRepository extends CrudRepository<DataJDBCEntity, Integer> {
}