package com.example.leavesystem.Repository;
import com.example.leavesystem.Entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository <UsersEntity, Integer> {
}
