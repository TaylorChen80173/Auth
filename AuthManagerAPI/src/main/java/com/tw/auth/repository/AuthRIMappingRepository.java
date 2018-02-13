package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthRIMapping;
import com.tw.auth.domain.authority.AuthRIMappingPK;

import java.util.List;

/**
 * 利用 Spring Data JPA repository 取得 AuthRIMapping 相關資料物件.
 */
public interface AuthRIMappingRepository extends JpaRepository<AuthRIMapping, AuthRIMappingPK> {

    /**
     * 依據角色代碼及應用程式代碼計算數量判斷應用程式代碼是否該存在
     *
     * @param   roleMasterId  角色代碼
     * @param   applicationId 應用程式代碼
     * @return  是否存在於 AuthRIMapping 中
     */
    Integer countByRoleMasterIdAndApplicationId(Integer roleMasterId, Integer applicationId);

    /**
     * 依據角色代碼取得應用程式相關資訊列表 (角色名稱、模組名稱、應用程式代碼、應用程式名稱)
     *
     * @param   roleMasterId    角色代碼
     * @return  應用程式相關資訊列表
     */
    @Query(value =
        "SELECT DISTINCT rm.roleMasterId, rm.roleName, l.lookupValues, a.applicationId, a.applicationName " +
            "FROM AuthRIMapping rim " +
            "JOIN AuthRoleMaster rm ON rm.roleMasterId = rim.roleMasterId " +
            "JOIN AuthApplication a ON a.applicationId = rim.applicationId " +
            "JOIN AuthLookups l ON l.lookupType = 'Module' AND l.lookupCode = a.module " +
            "WHERE rim.roleMasterId = ?1"
    )
    List<Object[]> findMappingNameByRoleId(Integer roleMasterId);

    /**
     * 依據角色代碼及應用程式代碼取得應用程式功能限制相關資訊列表
     *
     * @param   roleMasterId    角色代碼
     * @param   applicationId   應用程式代碼
     * @return  應用程式功能限制相關資訊列表
     */
    @Query(value =
        "SELECT rim.roleMasterId, rim.applicationId, rim.aItemId, rim.aItemValue, ai.aItemCode, ai.aItemName " +
            "FROM AuthRIMapping rim " +
            "JOIN AuthApplicationItem ai ON ai.aItemId = rim.aItemId " +
            "WHERE rim.roleMasterId = ?1 AND rim.applicationId = ?2 AND rim.aItemId > 0"
    )
    List<Object[]> findMappingItemByRoleIdAndApplicationId(Integer roleMasterId, Integer applicationId);

    /**
     * 依據角色代碼取得應用程式功能限制數量
     *
     * @param   roleMasterId    角色代碼
     * @return  角色對應之應用程式功能限制數量
     */
    Integer countByRoleMasterId(Integer roleMasterId);

    /**
     * 依據角色代碼及應用程式功能限制代碼刪除 AuthRIMapping Entity
     *
     * @param   roleMasterId    角色代碼
     * @param   applicationId   應用程式代碼
     * @return  刪除數量
     */
    Long removeByRoleMasterIdAndApplicationId(Integer roleMasterId, Integer applicationId);
}
