package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthRoleDetail;

import java.util.List;

/**
 * 利用 Spring Data JPA repository 取得 AuthRoleDetail 相關資料物件.
 */
public interface AuthRoleDetailRepository extends JpaRepository<AuthRoleDetail, Integer> {

    /**
     * 移除該角色下之所有成員(使用者與群組)資料
     *
     * @param   roleMasterId    角色代碼
     * @return  移除數量
     */
    Long removeByRoleMasterId(Integer roleMasterId);

    /**
     * 計算該角色種類及成員下之所有數量 (供群組及使用者管理移除判斷使用)
     *
     * @param   roleType        角色種類
     * @param   roleMemberId    角色包含之成員代碼
     * @return  數量
     */
    Integer countByRoleTypeAndRoleMemberId(String roleType, Integer roleMemberId);

    /**
     * 取得該角色下之所有成員(使用者與群組)分頁資料
     *
     * @param   roleMasterId    角色代碼
     * @return  移除數量
     */
    @Query(value =
        "SELECT rd.roleMasterId, rd.roleDetailId, rd.roleType, rd.roleMemberId, " +
            "CASE WHEN (rd.roleType = 'G') THEN g.groupName ELSE u.displayName END AS memberName " +
            "FROM AuthRoleDetail rd " +
            "LEFT JOIN AuthUsers u ON rd.roleType = 'U' AND u.userId = rd.roleMemberId " +
            "LEFT JOIN AuthGroups g ON rd.roleType = 'G' AND g.groupId = rd.roleMemberId " +
            "WHERE rd.roleMasterId = ?1"
    )
    List<Object[]> findByRoleMasterId(Integer roleMasterId);
}
