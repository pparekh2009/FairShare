package com.priyanshparekh.fairshareapi.balanceinfo;

import com.priyanshparekh.fairshareapi.dashboard.NetBalanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceInfoRepository extends JpaRepository<BalanceInfo, Long> {

    Optional<BalanceInfo> findByGroupIdAndUserIdAndOtherUserId(Long groupId, Long userId, Long otherUserId);

    List<BalanceInfo> findAllByGroupIdAndUserId(Long groupId, Long userId);

    @Query(value = """
    SELECT user_id,
           SUM(CASE WHEN direction = 'RECEIVES_FROM' THEN amount ELSE 0 END) -
           SUM(CASE WHEN direction = 'OWES_TO' THEN amount ELSE 0 END) AS netBalance
    FROM balance_info
    WHERE user_id IN (:userIds) AND group_id = :groupId
    GROUP BY user_id
    """, nativeQuery = true)
    List<NetBalanceProjection> findAllNetBalanceByUserIdsAndGroupId(@Param("userIds") List<Long> userIds, @Param("groupId") Long groupId);

    void deleteAllByGroupId(Long groupId);

    List<BalanceInfo> findAllByGroupId(Long groupId);

    BalanceInfo findByGroupIdAndUserIdAndOtherUserIdAndDirection(Long groupId, Long userId, Long otherUserId, BalanceInfo.Direction direction);
}
