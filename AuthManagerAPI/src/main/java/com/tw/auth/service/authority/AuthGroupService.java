package com.tw.auth.service.authority;


import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tw.auth.domain.authority.AuthGroups;
import com.tw.auth.domain.authority.AuthGroupsDetail;
import com.tw.auth.repository.AuthGroupDetailRepository;
import com.tw.auth.repository.AuthGroupRepository;
import com.tw.auth.repository.AuthRoleDetailRepository;
import com.tw.auth.web.rest.util.ObjectExtensions;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ExtensionMethod(ObjectExtensions.class)
public class AuthGroupService {

    private final AuthGroupRepository authGroupRepository;

    private final AuthGroupDetailRepository authGroupDetailRepository;

    private final AuthRoleDetailRepository authRoleDetailRepository;

    // 群組代碼、群組成員代碼、成員代碼、成員帳號、成員顯示名稱、成員EMAIL
    private static String[] groupsDetailColumn = {"groupId", "groupDetailId", "userId", "userAccount", "displayName", "email"};

    public AuthGroupService(AuthGroupRepository authGroupRepository,
                            AuthGroupDetailRepository authGroupDetailRepository,
                            AuthRoleDetailRepository authRoleDetailRepository) {
        this.authGroupRepository = authGroupRepository;
        this.authGroupDetailRepository = authGroupDetailRepository;
        this.authRoleDetailRepository = authRoleDetailRepository;
    }

    /**
     * 取得該 AuthGroups 所有 Entity List
     *
     * @return AuthGroups Entity List
     */
    public List<AuthGroups> getAuthGroupList() {
        return authGroupRepository.findAll();
    }

    /**
     * 取得該 AuthGroups 所有 Entity List
     *
     * @param   groupName 群組名稱
     * @param   pageable
     * @return AuthGroups Entity List
     */
    public Page<AuthGroups> getAuthGroupPageList(String groupName, Pageable pageable) {
        return authGroupRepository.findByGroupNameContaining(groupName.sqlLike(), pageable);
    }

    /**
     * 依據 GroupId 取得該 AuthGroups 所屬之 AuthUsers Entity List
     *
     * @param   id  群組代碼
         * @return AuthUsers Entity List
     */
    public List<Map<String, Object>> getAuthGroupUserList(Integer id) {
        return authGroupDetailRepository.findGroupUsers(id, null).mapping(groupsDetailColumn);
    }

    /**
     * 依據 GroupId 取得該 AuthGroups 所屬之 AuthGroups Entity
     *
     * @param   id  群組代碼
     * @return  AuthGroups Entity
     */
    public AuthGroups getAuthGroup(Integer id) {
        return authGroupRepository.findOne(id);
    }

    /**
     * 儲存 AuthGroups Entity
     *
     * @param   entity    群組資料
     */
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void saveAuthGroup(AuthGroups entity) {
        authGroupRepository.save(entity);
    }

    /**
     * 刪除 AuthGroups Entity
     *
     * @param   id    群組代碼
     * @return  true: 刪除完成 false: 群組尚有角色無法刪除
     */
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteAuthGroup(Integer id) {
        if (authRoleDetailRepository.countByRoleTypeAndRoleMemberId("G", id) == 0) {
            authGroupRepository.delete(id);
            return true;
        }
        return false;
    }

    /**
     * 刪除 AuthGroupsDetail Entity
     *
     * @param   details   群組成員代碼列表
     */
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void deleteAuthGroupDetails(List<AuthGroupsDetail> details) {
        authGroupDetailRepository.delete(details);
    }

    /**
     * 儲存 AuthGroups 所屬之 AuthGroupsDetail Entity List
     *
     * @param   details 群組之明細資料列表
     */
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void saveAuthGroupDetails(List<AuthGroupsDetail> details) {
        authGroupDetailRepository.save(details);
    }
}
