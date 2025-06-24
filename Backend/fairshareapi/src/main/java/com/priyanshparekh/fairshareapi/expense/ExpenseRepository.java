package com.priyanshparekh.fairshareapi.expense;

import com.priyanshparekh.fairshareapi.dashboard.DailyExpenseEntry;
import com.priyanshparekh.fairshareapi.dashboard.DailyExpenseProjection;
import com.priyanshparekh.fairshareapi.dashboard.ExpenseShareProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByGroupId(Long groupId);

    @Query(value = "SELECT SUM(amount) FROM expenses WHERE paid_by = :userId AND group_id = :groupId", nativeQuery = true)
    Double sumPaidByUserInGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);

//    List<Expense> findByGroupId(Long groupId);

    @Query(value = """
            SELECT\s
                DATE(FROM_UNIXTIME(created_at / 1000)) AS expense_date,
                SUM(amount) AS total_amount
            FROM expenses
            WHERE group_id = :groupId
            GROUP BY expense_date
            ORDER BY expense_date;
            """, nativeQuery = true)
    List<DailyExpenseProjection> findExpenseByDateAndGroupId(@Param("groupId") Long groupId);

    @Query(value = """
            SELECT\s
                                    e.paid_by AS paidBy,
                                    SUM(e.amount) AS userTotal,
                                    ROUND((SUM(e.amount) /\s
                                        (SELECT SUM(amount) FROM expenses WHERE group_id = :groupId)
                                    ) * 100, 2) AS percentShare
                                FROM expenses e
                                WHERE e.group_id = :groupId
                                GROUP BY e.paid_by
            """, nativeQuery = true)
    List<ExpenseShareProjection> getExpenseShareByGroupId(@Param("groupId") Long groupId);
}
