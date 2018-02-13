package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthRoleMaster;

import java.util.stream.Stream;

/**
 * 利用 Spring Data JPA repository 取得 AuthRoleMaster 相關資料物件.
 */
public interface AuthRoleRepository extends JpaRepository<AuthRoleMaster, Integer> {

    /**
     * 依據角色成員代碼取得角色相關資料(須排除無效之角色)
     *
     * @param   userId  角色成員使用者代碼
     * @return  Stream<AuthRoleMaster>
     */
    @Query(value =
        "SELECT DISTINCT r " +
            "FROM AuthRoleMaster r " +
            "WHERE r.roleMasterId IN ( " +
                "SELECT rm.roleMasterId " +
                "FROM AuthRoleMaster rm " +
                "JOIN AuthRoleDetail rd ON rd.roleMasterId = rm.roleMasterId " +
                "JOIN AuthUsers u ON rd.roleType = 'U' AND u.userId = rd.roleMemberId AND u.userId = ?1 " +
                "WHERE rm.isValid = 'Y' ) " +
            "OR r.roleMasterId IN (" +
                "SELECT rm.roleMasterId " +
                "FROM AuthRoleMaster rm " +
                "JOIN AuthRoleDetail rd ON rd.roleMasterId = rm.roleMasterId " +
                "JOIN AuthGroupsDetail gd ON rd.roleType = 'G' AND gd.groupId = rd.roleMemberId AND gd.userId = ?1 " +
                "WHERE rm.isValid = 'Y' )"
    )
//	//MySQL版
//	  @Query(value =
//		        "SELECT DISTINCT r " +
//		            "FROM AuthRoleMaster r " +
//		            "WHERE r.roleMasterId IN ( " +
//		                "SELECT rm.roleMasterId " +
//		                "FROM AuthRoleMaster rm " +
//		                "JOIN AuthRoleDetail rd ON rd.roleMasterId = rm.roleMasterId " +
//		                "JOIN AuthUsers u ON rd.roleType = 'U' AND u.userId = rd.roleMemberId AND u.userId = ?1 " +
//		                "WHERE rm.isValid = 'Y' ) " +
//		            "OR r.roleMasterId IN (" +
//		                "SELECT rm.roleMasterId " +
//		                "FROM AuthRoleMaster rm " +
//		                "JOIN AuthRoleDetail rd ON rd.roleMasterId = rm.roleMasterId " +
//		                "JOIN AuthGroupsDetail gd ON rd.roleType = 'G' AND gd.groupId = rd.roleMemberId AND gd.userId = ?1 " +
//		                "WHERE rm.isValid = 'Y' )"
//		    )
    Stream<AuthRoleMaster> findByRoleMemberId(Integer userId);
}
