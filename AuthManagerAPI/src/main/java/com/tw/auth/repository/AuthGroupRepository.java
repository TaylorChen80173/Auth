package com.tw.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthGroups;

/**
 * 利用 Spring Data JPA repository 取得 AuthGroups 相關資料物件.
 */
public interface AuthGroupRepository extends JpaRepository<AuthGroups, Integer> {

     /**
     * 依據 GroupName 從 AuthGroups 取得所有 AuthGroups 的資料
     *
     * @param   groupName 群組名稱
     * @param   pageable
     * @return  List<AuthGroups>
     */
    @Query(value =
        "SELECT g " +
            "FROM AuthGroups g " +
            "WHERE ?1 IS NULL OR g.groupName LIKE ?1"
    )
    Page<AuthGroups> findByGroupNameContaining(String groupName, Pageable pageable);
}
