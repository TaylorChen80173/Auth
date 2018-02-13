package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthGroupsDetail;

import java.util.List;
import java.util.Optional;

/**
 * 利用 Spring Data JPA repository 取得 AuthGroupsDetail 相關資料物件.
 */
public interface AuthGroupDetailRepository extends JpaRepository<AuthGroupsDetail, Integer> {

    /**
     * 移除該 GroupId 下之所有 AuthGroupsDetail
     *
     * @param groupId 群組代碼
     * @return
     */
    Long removeByGroupId(Integer groupId);

    /**
     * 移除該 UserId 下之所有 AuthGroupsDetail
     *
     * @param userId 使用者代碼
     * @return
     */
    Long removeByUserId(Integer userId);

    /**
     * 計算該 GroupId 下之 AuthGroupsDetail 數量(為確認是否給予刪除準備)
     *
     * @param groupId 群組代碼
     * @return
     */
    Integer countByGroupId(Integer groupId);

    /**
     * 依據 UserId 取得 AuthGroupsDetail
     *
     * @param userId 使用者代碼
     * @return
     */
    Optional<AuthGroupsDetail> findFirstByUserId(Integer userId);

    /**
     * 依據 GroupId 或 User displayName 從 AuthGroups 取得所有 AuthGroupsDetail Users 使用者的資料
     *
     * @param   id          群組代碼
     * @param   displayName 成員顯示名稱
     * @return  List<Object[]>
     */
    @Query(value =
        "SELECT d.groupId, d.groupDetailId, u.userId, u.userAccount, u.displayName, u.email " +
            "FROM AuthGroups g " +
            "JOIN AuthGroupsDetail d ON d.groupId = g.groupId " +
            "JOIN AuthUsers u ON u.userId = d.userId AND (?2 = null OR u.displayName LIKE ?2) " +
            "WHERE (?1 IS NULL OR g.groupId = ?1)"
    )
    List<Object[]> findGroupUsers(Integer id, String displayName);
}
