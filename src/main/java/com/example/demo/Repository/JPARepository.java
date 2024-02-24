package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.JPAEntity;

public interface JPARepository extends JpaRepository<JPAEntity, Integer>{
}
