package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthApplicationItem;

import java.util.List;

/**
 * 利用 Spring Data JPA repository 取得 AuthApplicationItem 相關資料物件.
 */
public interface AuthApplicationItemRepository extends JpaRepository<AuthApplicationItem, Integer> {

    /**
     * 依據 applicationId 從 AuthApplicationItem 取得分頁資料
     *
     * @param applicationId 應用程式代碼
     * @return AuthApplicationItem Page List
     */
    List<AuthApplicationItem> findByApplicationId(Integer applicationId);

    /**
     * 依據 applicationId、aItemType 從 AuthApplicationItem 取得資料
     *
     * @param applicationId 應用程式代碼
     * @param aItemType     功能限制屬性
     * @return AuthApplicationItem
     */
    AuthApplicationItem findFirstByApplicationIdAndAItemType(Integer applicationId, String aItemType);

    /**
     * 依據 applicationId、roleMasterid 從 AuthApplicationItem 取得頁面上權限角色所有的功能屬性
     *
     * @param applicationId 應用程式代碼
     * @param roleMasterid  登入者權限角色代碼
     * @return AuthApplicationItem
     */
//    @Query(nativeQuery = true, value =
//    "  SELECT aai1.AItemId, \n" +
//        "         aai1.ApplicationId, \n" +
//        "         aai1.AItemType, \n" +
//        "         aai1.AItemCode,\n" +
//        "         aai1.AItemName,\n" +
//        "         CASE WHEN aai1.AItemType <> 'D' THEN ISNULL(arim1.AItemValue, 'N') \n" +
//        "         ELSE ISNULL(arim1.AItemValue, aai1.AItemDefaultValue) END AS AitemDefaultValue,\n" +
//        "         aai1.Memo, \n" +
//        "         aai1.Creator, \n" +
//        "         aai1.CreateDT, \n" +
//        "         aai1.Updater, \n" +
//        "         aai1.UpdateDT\n" +
//        "  FROM AuthApplicationItem aai1\n" +
//        "    FULL OUTER JOIN AuthRIMapping arim1 ON arim1.AItemId = aai1.AItemId\n" +
//        "                                     AND aai1.ApplicationId = arim1.ApplicationId\n" +
//        "                                     AND arim1.RoleMasterId = ?1\n" +
//        "  WHERE aai1.ApplicationId =1\n" +
//        "UNION\n" +
//        "  SELECT aai2.AItemId, \n" +
//        "         aai2.ApplicationId, \n" +
//        "         aai2.AItemType, \n" +
//        "         aai2.AItemCode,\n" +
//        "         aai2.AItemName,\n" +
//        "         CASE WHEN aai2.AItemType <> 'D' THEN ISNULL(arim2.AItemValue, 'N') \n" +
//        "         ELSE ISNULL(arim2.AItemValue, aai2.AItemDefaultValue) END AS AitemDefaultValue,\n" +
//        "         aai2.Memo, \n" +
//        "         aai2.Creator, \n" +
//        "         aai2.CreateDT, \n" +
//        "         aai2.Updater, \n" +
//        "         aai2.UpdateDT\n" +
//        "  FROM AuthApplicationItem aai2\n" +
//        "    FULL OUTER JOIN AuthRIMapping arim2 ON arim2.AItemId = aai2.AItemId\n" +
//        "                                     AND aai2.ApplicationId = arim2.ApplicationId\n" +
//        "                                     AND arim2.RoleMasterId = ?1\n" +
//        "  WHERE aai2.ApplicationId =?2")
    //MYSQL版
    @Query(nativeQuery = true, value ="(SELECT  aai1.AItemId, \n" +
                 "aai1.ApplicationId,\n" + 
                 "aai1.AItemType,\n" +
                 "aai1.AItemCode,\n" +
                 "aai1.AItemName,\n" +
                 "CASE WHEN aai1.AItemType <> 'D' THEN IFNULL(arim1.AItemValue, 'N')\n" + 
                 "ELSE IFNULL(arim1.AItemValue, aai1.AItemDefaultValue) END AS AitemDefaultValue,\n" +
                 "aai1.Memo, \n" +
                 "aai1.Creator, \n" +
                 "aai1.CreateDT, \n" +
                 "aai1.Updater, \n" +
                 "aai1.UpdateDT \n" +
		"FROM AuthApplicationItem aai1\n" + 
        "LEFT JOIN AuthRIMapping arim1 ON arim1.AItemId = aai1.AItemId\n" +
			"AND aai1.ApplicationId = arim1.ApplicationId\n" +
			"AND arim1.RoleMasterId = ?1\n" +
            "AND aai1.ApplicationId =1)\n" +
		"UNION\n" +
	   "(SELECT  aai1.AItemId,\n" + 
                 "aai1.ApplicationId,\n" + 
                 "aai1.AItemType,\n" +
                 "aai1.AItemCode,\n" +
                 "aai1.AItemName,\n" +
                 "CASE WHEN aai1.AItemType <> 'D' THEN IFNULL(arim1.AItemValue, 'N')\n" + 
                 "ELSE IFNULL(arim1.AItemValue, aai1.AItemDefaultValue) END AS AitemDefaultValue,\n" +
                 "aai1.Memo, \n" +
                 "aai1.Creator, \n" +
                 "aai1.CreateDT, \n" +
                 "aai1.Updater, \n" +
                 "aai1.UpdateDT \n" +
		"FROM AuthApplicationItem aai1\n" + 
        "RIGHT JOIN AuthRIMapping arim1 ON arim1.AItemId = aai1.AItemId\n" +
			"AND aai1.ApplicationId = arim1.ApplicationId\n" +
			"AND arim1.RoleMasterId = ?1\n" +
            "AND aai1.ApplicationId =1)\n" +
      "UNION\n" +
          "(SELECT \n" +
                 "aai2.AItemId,\n" + 
                 "aai2.ApplicationId,\n" + 
                 "aai2.AItemType, \n" +
                 "aai2.AItemCode,\n" +
                 "aai2.AItemName,\n" +
                 "CASE WHEN aai2.AItemType <> 'D' THEN IFNULL(arim2.AItemValue, 'N')\n" + 
                 "ELSE IFNULL(arim2.AItemValue, aai2.AItemDefaultValue) END AS AitemDefaultValue,\n" +
                 "aai2.Memo, \n" +
                 "aai2.Creator, \n" +
                 "aai2.CreateDT, \n" +
                 "aai2.Updater, \n" +
                 "aai2.UpdateDT\n" +
          "FROM AuthApplicationItem aai2\n" + 
          "LEFT JOIN AuthRIMapping arim2 ON arim2.AItemId = aai2.AItemId\n" +
				"AND aai2.ApplicationId = arim2.ApplicationId\n" +
			    "AND arim2.RoleMasterId = ?1\n" +
                "AND aai2.ApplicationId =?2)\n" +
		  "UNION\n" +
		   "(SELECT\n" +  
				 "aai2.AItemId,\n" + 
                 "aai2.ApplicationId,\n" + 
                 "aai2.AItemType,\n" + 
                 "aai2.AItemCode,\n" +
                 "aai2.AItemName,\n" +
                 "CASE WHEN aai2.AItemType <> 'D' THEN IFNULL(arim2.AItemValue, 'N') \n" +
                 "ELSE IFNULL(arim2.AItemValue, aai2.AItemDefaultValue) END AS AitemDefaultValue,\n" +
                 "aai2.Memo,\n" + 
                 "aai2.Creator,\n" + 
                 "aai2.CreateDT,\n" + 
                 "aai2.Updater,\n" + 
                 "aai2.UpdateDT\n" +
          "FROM AuthApplicationItem aai2\n" + 
          "RIGHT JOIN AuthRIMapping arim2  ON arim2.AItemId = aai2.AItemId\n" +
				"AND aai2.ApplicationId = arim2.ApplicationId\n" +
                "AND arim2.RoleMasterId = ?1\n" +
                "AND aai2.ApplicationId =?2)")
    List<AuthApplicationItem> findAllByApplicationIdAndRoleMasterid(Integer applicationId, Integer roleMasterid);
}
