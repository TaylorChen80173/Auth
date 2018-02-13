package com.tw.auth.service.authority;


import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tw.auth.domain.authority.AuthMenu;
import com.tw.auth.repository.AuthMenuRepository;
import com.tw.auth.web.rest.util.ObjectExtensions;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
@ExtensionMethod(ObjectExtensions.class)
public class AuthMenuService {

    private final AuthMenuRepository authMenuRepository;

    //  選單代碼、父選單代碼、選單順序、選單名稱、應用程式代碼、應用程式路徑
    private static String[] menuColumns = {"menuId", "parentMenuId", "seqId", "menuName", "applicationId", "route"};

    public AuthMenuService(AuthMenuRepository authMenuRepository) {
        this.authMenuRepository = authMenuRepository;
    }

    /**
     * 依據模組代號、父選單代號取得該 AuthMenu 所有 Entity List
     *
     * @param module        模組代號
     * @param parentMenuId  父選單代號
     * @return AuthMenu Entity List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<AuthMenu> getAuthMenuList(String module, Integer parentMenuId) {
        return authMenuRepository.findByModuleAndParentMenuId(module, parentMenuId);
    }

    /**
     * 儲存 AuthMenu Entity List
     *
     * @param   entity    選單資料
     */
    public void saveAuthMenu(List<AuthMenu> entity) {
        authMenuRepository.save(entity);
    }

    /**
     * 刪除 AuthMenu Entity
     *
     * @param   id    選單代碼
     */
    public void deleteAuthMenu(Integer id) {
        authMenuRepository.deleteMenu(id);
    }

    /**
     * 依據角色代碼及應用程式功能限制代碼刪除 AuthRIMapping Entity
     *
     * @param module        模組代號
     * @param roleMasterId  角色代碼
     * @return AuthMenu Entity List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<Map<String, Object>> getAuthMenuByRoleList(String module, Integer roleMasterId) {
        return authMenuRepository.findByModuleAndRoleMasterId(module, roleMasterId).mapping(menuColumns);
    }
}
