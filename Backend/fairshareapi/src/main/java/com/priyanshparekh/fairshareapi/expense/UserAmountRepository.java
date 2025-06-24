package com.priyanshparekh.fairshareapi.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAmountRepository extends JpaRepository<UserAmount, Long> {

    @Query("SELECT SUM(u.amount) FROM UserAmount u WHERE u.userId = :userId AND u.expenseId IN (SELECT e.id FROM Expense e WHERE e.groupId = :groupId)")
    double sumOwedByUserInGroup(Long id, Long groupId);
//    List<UserAmount> findAllByGroupId(Long groupId);
}
