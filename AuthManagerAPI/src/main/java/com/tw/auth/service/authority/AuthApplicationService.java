package com.tw.auth.service.authority;


import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tw.auth.domain.authority.AuthApplication;
import com.tw.auth.domain.authority.AuthApplicationItem;
import com.tw.auth.repository.AuthApplicationItemRepository;
import com.tw.auth.repository.AuthApplicationRepository;
import com.tw.auth.web.rest.util.ObjectExtensions;
import com.tw.auth.web.rest.vm.LabelValueVM;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
@ExtensionMethod(ObjectExtensions.class)
public class AuthApplicationService {

    private final AuthApplicationRepository authApplicationRepository;

    private final AuthApplicationItemRepository authApplicationItemRepository;

    public AuthApplicationService(AuthApplicationRepository authApplicationRepository,
                                  AuthApplicationItemRepository authApplicationItemRepository) {
        this.authApplicationRepository = authApplicationRepository;
        this.authApplicationItemRepository = authApplicationItemRepository;
    }

    /**
     * 依據模組條件取得該 AuthApplication 所有 LabelValuePair List
     *
     * @param   module 模組
     * @return  LabelValuePair List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<LabelValueVM> getAuthApplicationPairs(String module) {
        return authApplicationRepository.findByModule(module).map(a ->
            new LabelValueVM(a.getApplicationName(), a.getApplicationId().toString())
        ).collect(Collectors.toList());
    }

    /**
     * 依據條件取得該 AuthApplication 所有 Page List
     *
     * @param   module          模組
     * @param   applicationId   應用程式代碼
     * @return  AuthApplication  List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<AuthApplication> getAuthApplicationList(String module, Integer applicationId) {
        return authApplicationRepository.findByModuleAndApplicationId(module, applicationId);
    }

    /**
     * 依據條件取得該 AuthApplicationItem 所有 Page List
     *
     * @param   applicationId   應用程式代碼
     * @return  AuthApplicationItem  List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<AuthApplicationItem> getAuthApplicationItemList(Integer applicationId) {
        return authApplicationItemRepository.findByApplicationId(applicationId);
    }

    /**
     * 依據 UserId 取得該 AuthUsers 所屬之 AuthUsers Entity
     *
     * @param   id  使用者代碼
     * @return  AuthUsers Entity
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public AuthApplication getAuthApplication(Integer id) {
        return authApplicationRepository.findOne(id);
    }

    /**
     * 依據 applicationId 取得該 AuthApplicationItem 所屬之 AuthApplicationItem Page List
     *
     * @param   id  應用程式代碼
     * @return  AuthApplicationItem Page List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<AuthApplicationItem> getAuthApplicationItem(Integer id) {
        return authApplicationItemRepository.findByApplicationId(id);
    }

    /**
     * 儲存 AuthApplication Entity
     *
     * @param   entity  應用程式資料
     */
    public void saveAuthApplication(AuthApplication entity) {
        Integer id = entity.getApplicationId();
        AuthApplication ap = authApplicationRepository.save(entity);
        if (id == null) {
            authApplicationItemRepository.save(
                new AuthApplicationItem(ap.getApplicationId(), "View", "V", "選單檢視", "Y"));
        }
    }

    /**
     * 儲存 AuthApplicationItem Entity
     *
     * @param   entity  應用程式功能限制資料
     */
    public void saveAuthApplicationItem(AuthApplicationItem entity) {
        authApplicationItemRepository.save(entity);
    }

    /**
     * 刪除 AuthApplication Entity
     *
     * @param   id  應用程式代碼
     */
    public boolean deleteAuthApplication(Integer id) {
        // TODO:  避免角色設定中上存在相關連結及不可刪除
        authApplicationRepository.delete(id);
        // 如果應用程式資料中有包括該功能限制一併刪除
        authApplicationItemRepository.delete(
            authApplicationItemRepository.findByApplicationId(id));
        return true;
    }

    /**
     * 刪除 AuthApplicationItem Entity
     *
     * @param   id  功能限制代碼
     */
    public boolean deleteAuthApplicationItem(Integer id) {
        // TODO:  避免角色設定中上存在相關連結及不可刪除
        authApplicationItemRepository.delete(id);
        return true;
    }
}
