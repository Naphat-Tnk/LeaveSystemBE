package com.example.leavesystem.Repository;

import com.example.leavesystem.Entity.BalancesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalancesRepo extends JpaRepository<BalancesEntity, Integer> {
    BalancesEntity findByUserIdAndLeaveIdAndYear(int userId, int leaveId, String year);

}
