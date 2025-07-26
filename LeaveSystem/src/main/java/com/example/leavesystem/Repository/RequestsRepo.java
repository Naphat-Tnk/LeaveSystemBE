package com.example.leavesystem.Repository;

import com.example.leavesystem.Entity.RequestsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestsRepo extends JpaRepository<RequestsEntity, Integer> {
}