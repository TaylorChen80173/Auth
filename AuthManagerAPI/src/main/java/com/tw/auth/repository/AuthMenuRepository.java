package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthMenu;

import java.util.List;

/**
 * 利用 Spring Data JPA repository 取得 AuthMenu 相關資料物件.
 */
public interface AuthMenuRepository extends JpaRepository<AuthMenu, Integer> {

    /**
     * 依據模組代號、父選單代號取得該 AuthMenu 所有 Entity List
     *
     * @param module        模組代號
     * @param parentMenuId  父選單代號
     * @return AuthMenu Entity List
     */
    @Query(value =
        "SELECT m " +
            "FROM AuthMenu m " +
            "WHERE m.module = ?1 AND (?2 IS NULL OR m.parentMenuId = ?2)"
    )
    List<AuthMenu> findByModuleAndParentMenuId(String module, Integer parentMenuId);

    /**
     * 依據選單代號刪除該 AuthMenu 所有 Entity List
     *
     * @param menuId    選單代號
     * @return 刪除數量
     */
    @Modifying
//    @Query(value =
//        "DELETE FROM AuthMenu m " +
//            "WHERE m.parentMenuId = ?1 OR m.menuId = ?1"
//    )
    //MySQl版
    @Query(value =
	  "DELETE FROM AuthMenu  " +
	      "WHERE parentMenuId = ?1 OR menuId = ?1"
	)
    void deleteMenu(Integer menuId);

    /**
     * 依據模組代號、角色代號取得該 AuthMenu 所有 Entity List
     *
     * @param module        模組代號
     * @param roleMasterId  角色代號
     * @return AuthMenu Entity List
     */
    @Query(value =
        "SELECT m.menuId, m.parentMenuId, m.seqId, m.menuName, m.applicationId, a.route " +
            "FROM AuthMenu m " +
            "JOIN AuthApplication a ON a.applicationId = m.applicationId " +
            "JOIN AuthApplicationItem ai ON ai.applicationId = a.applicationId AND ai.aItemType = 'V' " +
            "JOIN AuthRIMapping rim ON rim.applicationId = ai.applicationId AND m.applicationId = rim.applicationId AND ai.aItemId = rim.aItemId AND rim.aItemValue = 'Y' " +
            "WHERE m.module = ?1 AND rim.roleMasterId = ?2"
    )
    List<Object[]> findByModuleAndRoleMasterId(String module, Integer roleMasterId);
}
