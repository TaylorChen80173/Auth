package com.tw.auth.service.authority;

import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tw.auth.domain.authority.AuthApplicationItem;
import com.tw.auth.domain.authority.AuthRIMapping;
import com.tw.auth.domain.authority.AuthRIMappingPK;
import com.tw.auth.domain.authority.AuthRoleDetail;
import com.tw.auth.domain.authority.AuthRoleMaster;
import com.tw.auth.repository.AuthApplicationItemRepository;
import com.tw.auth.repository.AuthRIMappingRepository;
import com.tw.auth.repository.AuthRoleDetailRepository;
import com.tw.auth.repository.AuthRoleRepository;
import com.tw.auth.web.rest.util.ObjectExtensions;
import com.tw.auth.web.rest.vm.LabelValueVM;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
@ExtensionMethod(ObjectExtensions.class)
public class AuthRoleService {

    private final AuthRoleRepository authRoleRepository;

    private final AuthRoleDetailRepository authRoleDetailRepository;

    private final AuthRIMappingRepository authRIMappingRepository;

    private final AuthApplicationItemRepository authApplicationItemRepository;

    //  角色代碼、功角色名稱、模組名稱、應用程式代碼、應用程式名稱
    private static String[] mappingColumns = {"roleMasterId", "roleName", "moduleName", "applicationId", "applicationName"};
    // 角色代碼、功能限制代碼、功能限制參數、功能限制代號、功能限制名稱
    private static String[] mappingItemColumns = {"roleMasterId", "applicationId", "aItemId", "aItemValue", "aItemCode", "aItemName"};
    // 角色代碼、角色成員代碼、成員類別、成員代號、成員名稱
    private static String[] roleDetailColumns = {"roleMasterId", "roleDetailId", "roleType", "roleMemberId", "memberName"};

    public AuthRoleService(AuthRoleRepository authRoleRepository,
                           AuthRoleDetailRepository authRoleDetailRepository,
                           AuthRIMappingRepository authRIMappingRepository,
                           AuthApplicationItemRepository authApplicationItemRepository) {
        this.authRoleRepository = authRoleRepository;
        this.authRoleDetailRepository = authRoleDetailRepository;
        this.authRIMappingRepository = authRIMappingRepository;
        this.authApplicationItemRepository = authApplicationItemRepository;
    }

    /**
     * 取得該 AuthRoleMaster 所有 Entity List
     *
     * @return  AuthRoleMaster Entity List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<AuthRoleMaster> getAuthRoleList() {
        return authRoleRepository.findAll();
    }

    /**
     * 取得該 AuthRoleDetail 所有 Entity List
     *
     * @param   id  角色代碼
     * @return  AuthRoleDetail Entity List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<Map<String, Object>> getAuthRoleDetailList(Integer id) {
        return authRoleDetailRepository.findByRoleMasterId(id).mapping(roleDetailColumns);
    }

    /**
     * 依據 RoleMasterId 取得該 AuthRIMapping 所屬之 mappingColumn List
     *
     * @param   id  角色代碼
     * @return  mappingColumn List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<Map<String, Object>> getAuthRIMappingList(Integer id) {
        return authRIMappingRepository.findMappingNameByRoleId(id).mapping(mappingColumns);
    }

    /**
     * 依據 RoleMasterId 取得該 AuthRIMapping 所屬之 mappingColumn List
     *
     * @param   roleMasterid  角色代碼
     * @param   applicationId 應用程式代碼
     * @return  mappingItemColumn List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<Map<String, Object>> getAuthRIMappingItemList(Integer roleMasterid, Integer applicationId) {
        return authRIMappingRepository.findMappingItemByRoleIdAndApplicationId(roleMasterid, applicationId).mapping(mappingItemColumns);
    }

    /**
     * 依據 RoleMasterId、ApplicationId、aItemValue 取得該 AuthRIMapping 所屬之 itemValue
     *
     * @param   roleMasterid    角色代碼
     * @param   applicationId   應用程式代碼
     * @param   aItemCode       功能限制參數
     * @return  aItemValue
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public String getAuthRIMappingItemValue(Integer roleMasterid, Integer applicationId, String aItemCode) {
        return getAuthRIMappingItemList(roleMasterid, applicationId).stream().filter(m ->
            StringUtils.equalsAnyIgnoreCase((String) m.get("aItemCode"), aItemCode)).findFirst().
            map(a -> (String) a.get("aItemValue")).orElse(null);
    }

    /**
     * 依據 RoleMemberId 取得該 AuthRoleMaster 所屬之 LabelValueVM List
     *
     * @param   id  角色成員代碼
     * @return  LabelValueVM List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<LabelValueVM> getAuthRoleByMemberId(Integer id) {
        return authRoleRepository.findByRoleMemberId(id).map(r ->
            new LabelValueVM(r.getRoleName(), r.getRoleMasterId().toString())
        ).collect(Collectors.toList());
    }

    /**
     * 儲存 AuthRoleMaster Entity
     *
     * @param   entity    角色資料
     */
    public AuthRoleMaster saveAuthRole(AuthRoleMaster entity) {
        return authRoleRepository.save(entity);
    }

    /**
     * 刪除 AuthRoleMaster Entity
     *
     * @param   id    角色代碼
     * @return  true: 刪除完成 false: 角色尚有成員對應至應用程式無法刪除
     */
    public boolean deleteAuthRole(Integer id) {
        // 確認角色是否尚有與應用程式之對應列表
        if (authRIMappingRepository.countByRoleMasterId(id) == 0) {
            authRoleRepository.delete(id);
            authRoleDetailRepository.removeByRoleMasterId(id);
            return true;
        }
        return false;
    }

    /**
     * 儲存 AuthRoleMaster 所屬之 AuthRoleDetail Entity
     *
     * @param   roleMasterId    角色代碼
     * @param   details         角色成員之明細資料
     */
    public void saveAuthRoleDetail(Integer roleMasterId, List<AuthRoleDetail> details) {
        authRoleDetailRepository.removeByRoleMasterId(roleMasterId);
        authRoleDetailRepository.save(details);
    }

    /**
     * 刪除 AuthRoleDetail Entity
     *
     * @param   id    角色成員代碼
     */
    public void deleteAuthRoleDetail(Integer id) {
        authRoleDetailRepository.delete(id);
    }

    /**
     * 儲存 AuthRIMapping Entity List
     *
     * @param   mappings 角色與對應之應用程式資料
     */
    @SneakyThrows(Exception.class)
    public void saveAuthRIMapping(Integer roleMasterId, Integer applicationId, List<AuthRIMapping> mappings) {
        deleteAllAuthRIMapping(roleMasterId, applicationId);
        if (mappings.size() == 0) {
            // 挑選 aItemId 為VIEW之資料為預設資料表明該 Mapping 尚存在於 角色之應用程式項目中
            AuthApplicationItem item = authApplicationItemRepository.findFirstByApplicationIdAndAItemType(applicationId, "V");
            if (item == null) {
                throw new Exception("無法取得預設之[選單檢視]應用程式限制功能，請確認後再行新增應用");
            }
            authRIMappingRepository.save(new AuthRIMapping(roleMasterId, item.getAItemId(), applicationId, item.getAItemDefaultValue()));
        } else {
            authRIMappingRepository.save(mappings);
        }
    }

    /**
     * 依據角色代碼及應用程式代碼初始化 AuthRIMapping Entity
     *
     * @param   roleMasterId  角色代碼
     * @param   applicationId 應用程式代碼
     * @return  true: 儲存完成 false: 已存在於該 AuthRIMapping 中
     */
    @SneakyThrows(Exception.class)
    public boolean initAuthRIMapping(Integer roleMasterId, Integer applicationId) {
        // 依據角色代碼及應用程式代碼計算數量判斷應用程式代碼是否該存在
        if (authRIMappingRepository.countByRoleMasterIdAndApplicationId(roleMasterId, applicationId) == 0) {
            // 挑選 aItemId 為VIEW之資料為預設資料表明該 Mapping 尚存在於 角色之應用程式項目中
            AuthApplicationItem item = authApplicationItemRepository.findFirstByApplicationIdAndAItemType(applicationId, "V");
            if (item == null) {
                throw new Exception("無法取得預設之[選單檢視]應用程式限制功能，請確認後再行新增應用");
            }
            authRIMappingRepository.save(new AuthRIMapping(roleMasterId, item.getAItemId(), applicationId, item.getAItemDefaultValue()));
            return true;
        }
        return false;
    }

    /**
     * 依據角色代碼及應用程式代碼刪除所有 AuthRIMapping Entity
     *
     * @param   roleMasterId    角色代碼
     * @param   applicationId   應用程式代碼
     */
    public void deleteAllAuthRIMapping(Integer roleMasterId, Integer applicationId) {
        authRIMappingRepository.removeByRoleMasterIdAndApplicationId(roleMasterId, applicationId);
    }

    /**
     * 依據角色代碼及應用程式功能限制代碼刪除 AuthRIMapping Entity
     *
     * @param   roleMasterId    角色代碼
     * @param   aItemId         應用程式功能限制代碼
     */
    public void deleteAuthRIMapping(Integer roleMasterId, Integer aItemId) {
        authRIMappingRepository.delete(new AuthRIMappingPK(aItemId, roleMasterId));
    }
}
