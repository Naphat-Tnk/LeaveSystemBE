package com.example.leavesystem.Repository;

import com.example.leavesystem.Entity.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepo extends JpaRepository<TypeEntity, Integer> {
}
