package com.tw.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthGroups;
import com.tw.auth.domain.authority.AuthUsers;

import java.util.List;
import java.util.Optional;

/**
 * 利用 Spring Data JPA repository 取得 AuthUsers 相關資料物件.
 */
public interface AuthUserRepository extends JpaRepository<AuthUsers, Integer> {

    /**
     * 依據使用者帳號及顯示名稱取得使用者列表資料
     *
     * @param   userAccount   使用者帳號
     * @param   displayName   顯示名稱
     * @return  使用者列表資料
     */
    @Query(value =
        "SELECT u FROM AuthUsers u " +
            "WHERE (?1 is null OR u.userAccount LIKE ?1) " +
            "  AND (?2 is null OR u.displayName LIKE ?2)"
    )
    Page<AuthUsers> findByUserAccountAndDisplayNameContaining(String userAccount, String displayName, Pageable pageable);

    /**
     * 依據名稱及種類取得使用者及群組相關資料列表
     * @param   name  使用者名稱或群組名稱
     * @param   type  種類 U:使用者 G:群組
     * @param   pageable
     * @return  使用者及群組相關資料列表
     */
//    @Query(value =
//        "SELECT * FROM (" +
//            "SELECT UserId AS Id, DisplayName AS Label, 'U' AS Kind FROM AuthUsers " +
//            "UNION " +
//            "SELECT GroupId AS Id, GroupName AS Label, 'G' AS Kind FROM AuthGroups g) " +
//            "WHERE (?1 IS NULL OR Label LIKE ?1) " +
//            "  AND (?2 IS NULL OR Kind = ?2) " +
//            "ORDER BY Label " +
//            "/*#pageable*/",
//        countQuery =
//        "SELECT COUNT(*) FROM (" +
//            "SELECT UserId AS Id, DisplayName AS Label, 'U' AS Kind FROM AuthUsers " +
//            "UNION " +
//            "SELECT GroupId AS Id, GroupName AS Label, 'G' AS Kind FROM AuthGroups g) " +
//            "WHERE (?1 IS NULL OR Label LIKE ?1) " +
//            "  AND (?2 IS NULL OR Kind = ?2)",
//        nativeQuery = true
//    )
	//MySQL版
	 @Query(value =
		        "SELECT * FROM (" +
		            "SELECT UserId AS Id, DisplayName AS Label, 'U' AS Kind FROM AuthUsers " +
		            "UNION " +
		            "SELECT GroupId AS Id, GroupName AS Label, 'G' AS Kind FROM AuthGroups g) as a" +
		            "WHERE (?1 IS NULL OR Label LIKE ?1) " +
		            "  AND (?2 IS NULL OR Kind = ?2) " +
		            "ORDER BY Label " +
		            "/*#pageable*/",
		        countQuery =
		        "SELECT COUNT(*) FROM (" +
		            "SELECT UserId AS Id, DisplayName AS Label, 'U' AS Kind FROM AuthUsers " +
		            "UNION " +
		            "SELECT GroupId AS Id, GroupName AS Label, 'G' AS Kind FROM AuthGroups g) as a " +
		            "WHERE (?1 IS NULL OR Label LIKE ?1) " +
		            "  AND (?2 IS NULL OR Kind = ?2)",
		        nativeQuery = true
		    )
    List<Object[]> findAllGroupAndUsersByNameAndType(String name, String type, Pageable pageable);

    /**
     * 依據使用者ID取得群組列表
     *
     * @param   userId  使用者ID
     * @return  群組列表
     */
    @Query(value =
        "SELECT g " +
            "FROM AuthGroups g " +
            "JOIN AuthGroupsDetail d ON d.groupId = g.groupId " +
            "JOIN AuthUsers u ON u.userId = d.userId AND u.userId = ?1"
    )
    List<AuthGroups> findGroupByUserId(Integer userId);

    /**
     * 依據使用者 Email 取得使用者資料
     *
     * @param   email   郵件地址
     * @return  AuthUsers
     */
    Optional<AuthUsers> findFirstByEmail(String email);

    /**
     * 依據使用者帳號取得使用者資料
     *
     * @param   userAccount   帳號
     * @return  AuthUsers
     */
    Optional<AuthUsers> findFirstByUserAccount(String userAccount);
}
