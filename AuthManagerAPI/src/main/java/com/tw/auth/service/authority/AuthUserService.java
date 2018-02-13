package com.tw.auth.service.authority;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tw.auth.domain.authority.AuthGroups;
import com.tw.auth.domain.authority.AuthGroupsDetail;
import com.tw.auth.domain.authority.AuthLdapProperties;
import com.tw.auth.domain.authority.AuthRoleMaster;
import com.tw.auth.domain.authority.AuthUsers;
import com.tw.auth.domain.authority.AuthUsersLdap;
import com.tw.auth.repository.AuthGroupDetailRepository;
import com.tw.auth.repository.AuthRoleRepository;
import com.tw.auth.repository.AuthUserLdapRepository;
import com.tw.auth.repository.AuthUserRepository;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
public class AuthUserService {

    private final AuthUserRepository authUserRepository;

    private final AuthGroupDetailRepository authGroupDetailRepository;

    private final AuthLdapProperties authLdapProperties;

    private final AuthUserLdapRepository authUserLdapRepository;

    private final AuthRoleRepository authRoleRepository;

    private final AuthRoleService authRoleService;

    private TypeMap<AuthUsers, AuthUsers> authUsersMapper;

    private TypeMap<AuthUsersLdap, AuthUsers> authLdapUsersMapper;

    private TypeMap<AuthUsers, AuthUsersLdap> authUsersLdapMapper;

//    private TypeMap<Students, AuthUsers> authStudentUsersMapper;

    // password 必須要有英數字
    private Pattern pwd = Pattern.compile("(?=.*[a-zA-Z])(?=.*[0-9])");

    public AuthUserService(AuthUserRepository authUserRepository,
                           AuthGroupDetailRepository authGroupDetailRepository,
                           AuthLdapProperties authLdapProperties,
                           AuthUserLdapRepository authUserLdapRepository,
                           AuthRoleRepository authRoleRepository,
                           AuthRoleService authRoleService) {
        this.authUserRepository = authUserRepository;
        this.authGroupDetailRepository = authGroupDetailRepository;
        this.authLdapProperties = authLdapProperties;
        this.authUserLdapRepository = authUserLdapRepository;
        this.authRoleRepository = authRoleRepository;
        this.authRoleService = authRoleService;

        // AuthUsers 物件轉換至 AuthUsers 物件
        authUsersMapper = new ModelMapper().createTypeMap(AuthUsers.class, AuthUsers.class);
        authUsersMapper.addMappings(m -> {
            m.map(AuthUsers::getUserAccount, AuthUsers::setUserAccount);
            m.map(AuthUsers::getDisplayName, AuthUsers::setDisplayName);
            m.map(AuthUsers::getEmail, AuthUsers::setEmail);
            m.map(AuthUsers::getIsActive, AuthUsers::setIsActive);
            m.map(AuthUsers::getMemo, AuthUsers::setMemo);
            m.skip(AuthUsers::setUserId);
            m.skip(AuthUsers::setCreator);
            m.skip(AuthUsers::setCreateDT);
        });

        // AuthUsersLdap 物件轉換至 AuthUsers 物件
        authLdapUsersMapper = new ModelMapper().createTypeMap(AuthUsersLdap.class, AuthUsers.class);
        authLdapUsersMapper.addMappings(m -> m.map(AuthUsersLdap::getUid, AuthUsers::setUserAccount));

        // AuthUsers 物件轉換至 AuthUsersLdap 物件
        authUsersLdapMapper = new ModelMapper().createTypeMap(AuthUsers.class, AuthUsersLdap.class);
        authUsersLdapMapper.addMappings(m -> {
            m.map(AuthUsers::getUserAccount, AuthUsersLdap::setUid);
            m.map(AuthUsers::getUserAccount, AuthUsersLdap::setSurName);
            m.map(AuthUsers::getUserAccount, AuthUsersLdap::setCommonName);
            m.skip(AuthUsersLdap::setDn);
            m.skip(AuthUsersLdap::setPassword);
        });

//        // Students 物件轉換至 AuthUsers 物件
//        authStudentUsersMapper = new ModelMapper().createTypeMap(Students.class, AuthUsers.class);
//        authStudentUsersMapper.addMappings(m -> {
//            m.map(Students::getStuCode, AuthUsers::setUserAccount);
//            m.map(Students::getStuName, AuthUsers::setDisplayName);
//            m.map(Students::getAccount, AuthUsers::setEmail);
//            m.map(Students::getMemo, AuthUsers::setMemo);
//            m.skip(AuthUsers::setUserId);
//            m.skip(AuthUsers::setCreator);
//            m.skip(AuthUsers::setCreateDT);
//        });
    }

    /**
     * 依據條件取得該 AuthUsers 所有 Entity List
     *
     * @param   userAccount
     * @param   displayName
     * @param   pageable
     * @return AuthUsers Entity List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public Page<AuthUsers> getAuthUserList(String userAccount, String displayName, Pageable pageable) {
        return authUserRepository.findByUserAccountAndDisplayNameContaining(
            userAccount == null ? null : "%" + userAccount + "%",
            displayName == null ? null : "%" + displayName + "%", pageable);
    }

    /**
     * 依據 email 取得該 AuthUsers 所屬之 AuthUsers Entity
     *
     * @param   email  使用者 Email
     * @return  AuthUsers Entity
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public Optional<AuthUsers> getAuthUserByEmail(String email) {
        return authUserRepository.findFirstByEmail(email);
    }

    /**
     * 依據 UserId 取得該 AuthUsers 所屬之 AuthGroups Entity List
     *
     * @param   id  使用者代碼
     * @return  AuthGroups Entity
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<AuthGroups> getAuthUserGroup(Integer id) {
        return authUserRepository.findGroupByUserId(id);
    }

    /**
     * 依據 使用者代碼及群組列表 加入該 AuthUsers 所屬之 AuthGroupsDetail Entity
     *
     * @param   uId      使用者代碼
     * @param   groupIds 群組代碼列表
     * @return  AuthGroups Entity
     */
    public void saveAuthUserGroup(Integer uId, List<Integer> groupIds) {
        authGroupDetailRepository.removeByUserId(uId);
        groupIds.stream().forEach(gId -> {
            authGroupDetailRepository.save(new AuthGroupsDetail(gId, uId));
        });
    }

//    /**
//     * 使用 Students 資訊儲存 AuthUsers Entity
//     *
//     * @param   entity    使用者資料
//     */
//    @SneakyThrows
//    public void saveAuthStudentUser(Students entity) {
//        AuthUsers user = getAuthUser(entity.getStuCode()).map(u -> {
//            authStudentUsersMapper.map(entity, u);
//            return u;
//        }).orElseGet(() -> {
//            AuthUsers u = new AuthUsers();
//            authStudentUsersMapper.map(entity, u);
//            return u;
//        });
//        try {
//            updateLdapUser(user);
//        } catch (Exception e) {
//            log.error("LDAP ERROR:", e);
//            throw new Exception("LDAP 資料儲存失敗，" + e.getMessage(), e);
//        }
//        authUserRepository.save(user);
//    }

    /**
     * 儲存 AuthUsers Entity
     *
     * @param   entity    使用者資料
     */
    @SneakyThrows
    public AuthUsers saveAuthUser(AuthUsers entity) {
        if (null != entity.getUserId()) {
            AuthUsers user = authUserRepository.findOne(entity.getUserId());
            authUsersMapper.map(entity, user);
            entity = user;
        }
        try {
            updateLdapUser(entity);
        } catch (Exception e) {
            log.error("LDAP ERROR:", e);
            throw new Exception("LDAP 資料儲存失敗，" + e.getMessage(), e);
        }
        return authUserRepository.save(entity);
    }

    /**
     * 刪除 AuthUsers Entity
     *
     * @param   id    使用者代碼
     */
    public void deleteAuthUser(Integer id) {
        AuthUsers user = authUserRepository.findOne(id);
        authUserRepository.delete(user);
        // 如果群組中有包括該使用者資料一併刪除
        authGroupDetailRepository.findFirstByUserId(id).ifPresent(d -> {
            authGroupDetailRepository.delete(d.getGroupDetailId());
        });
        try {
            deleteLdapUser(user.getUserAccount());
        } catch (Exception e) {
            log.error("刪除使用者LDAP資料錯誤", e);
        }
    }

    /**
     * 建立該 Ldap 下之使用者資訊
     *
     * @param   user    Ldap 使用者物件
     * @return  AuthUsersLdap
     */
    public AuthUsersLdap createLdapUser(AuthUsers user) throws Exception {
        Name newId = authLdapProperties.toUserSearchDn(user.getUserAccount());
        AuthUsersLdap existingUser = authUserLdapRepository.findOne(newId);
        if (existingUser != null) {
            throw new Exception("無法建立 Ldap 帳號，該帳號已存在");
        }
        AuthUsersLdap newUser = authUsersLdapMapper.map(user);
        newUser.setPassword(authLdapProperties.encodePassword(
            RandomStringUtils.randomAlphanumeric(8)));
        return authUserLdapRepository.save(newUser);
    }

    /**
     * 變更該 Ldap 下之使用者資訊
     *
     * @param   user    使用者物件
     * @return  AuthUsersLdap
     */
    public AuthUsersLdap updateLdapUser(AuthUsers user) throws Exception {
        Name originalId = authLdapProperties.toUserSearchDn(user.getUserAccount());
        AuthUsersLdap existingUser = authUserLdapRepository.findOne(originalId);
        if (existingUser == null) {
            return createLdapUser(user);
        }
        authUsersLdapMapper.map(user, existingUser);
        return authUserLdapRepository.save(existingUser);
    }

    /**
     * 刪除該 Ldap 下之使用者資訊
     *
     * @param   userAccount    使用者帳號
     */
    public void deleteLdapUser(String userAccount) {
        Name originalId = authLdapProperties.toUserSearchDn(userAccount);
        AuthUsersLdap existingUser = authUserLdapRepository.findOne(originalId);
        if (existingUser != null) {
            authUserLdapRepository.delete(originalId);
        }
    }

    /**
     * 建立該 Ldap 下之使用者亂數密碼資訊
     *
     * @param   user    使用者物件
     * @return  new password
     */
    public String generateLdapUserPassword(AuthUsers user) throws Exception {
        Name originalId = authLdapProperties.toUserSearchDn(user.getUserAccount());
        AuthUsersLdap existingUser = authUserLdapRepository.findOne(originalId);
        if (existingUser == null) {
            throw new Exception("無法變更使用者資訊，原LDAP使用者資訊不存在");
        }
        String password;
        while (true) {
            password = RandomStringUtils.randomAlphanumeric(8);
            if (pwd.matcher(password).find()) {
                break;
            }
        }
        existingUser.setPassword(authLdapProperties.encodePassword(password));
        authUserLdapRepository.save(existingUser);
        return password;
    }

    /**
     * 變更該 Ldap 下之使用者密碼
     *
     * @param   userAccount    Ldap 使用者
     * @param   password       使用者密碼
     * @return  AuthUsersLdap
     */
    public void changeLdapPassword(String userAccount, String password) throws Exception {
        Name originalId = authLdapProperties.toUserSearchDn(userAccount);
        AuthUsersLdap existingUser = authUserLdapRepository.findOne(originalId);
        if (existingUser == null) {
            throw new Exception("無法變更使用者密碼，原使用者資訊不存在");
        }
        existingUser.setPassword(authLdapProperties.encodePassword(password));
        authUserLdapRepository.save(existingUser);
    }

    /**
     * 以 userAccount 取得該 Ldap 下之使用者資訊
     *
     * @param   userAccount    使用者帳號
     * @return  AuthUsersLdap
     */
    public AuthUsersLdap findLdapUser(String userAccount) {
        return authUserLdapRepository.findOne(authLdapProperties.toUserSearchDn(userAccount));
    }

    /**
     * 取得登入之 Ldap 使用者資訊
     *
     * @param   authentication 認證資訊
     * @return  AuthUsers
     */
    @SneakyThrows(Exception.class)
    public AuthUsers getLoginAuUser(Authentication authentication) {
        return getAuthUser(authentication.getName()).map(u -> {
            authentication.getAuthorities().stream().findFirst().ifPresent(a -> {
                Integer roleId = Integer.parseInt(a.getAuthority());
                AuthRoleMaster role = authRoleRepository.findOne(roleId);

                u.setRoleMasterId(roleId);
                u.setRoleName(role.getRoleName());

                List<AuthUsers.Ap> authAps = new ArrayList<>();
                authRoleService.getAuthRIMappingList(roleId).forEach(ap -> {
                    List<AuthUsers.Ap.AItem> authItems = new ArrayList<>();
                    Integer apId = (Integer) ap.get("applicationId");
                    authAps.add(new AuthUsers.Ap(apId, (String) ap.get("applicationName"), authItems));
                    authRoleService.getAuthRIMappingItemList(roleId, apId).forEach(i -> {
                        authItems.add(new AuthUsers.Ap.AItem((Integer) i.get("aItemId"), (String) i.get("aItemCode"), (String) i.get("aItemName"), (String) i.get("aItemValue")));
                    });
                });
                u.setAuthorities(authAps);
            });
            return u;
        }).orElseThrow(() -> new Exception("無此學員代號 [" + authentication.getName() + "]，請重新登入"));
    }

    /**
     * 儲存 APP 設備相關資訊
     *
     * @param stuCode       調入班級班幹部代碼
     * @param deviceToken   設備 Notification 代碼
     * @param deviceName    設備名稱
     */
    public boolean saveNotificationToken(String stuCode, String deviceToken, String deviceName) {
        getAuthUser(stuCode).ifPresent(u -> {
            u.setDeviceToken(deviceToken);
            u.setDeviceName(deviceName);
            authUserRepository.save(u);
        });
        return true;
    }

    /**
     * 取得使用者資訊
     *
     * @param   stuCode 學員代碼
     * @return  Optional AuthUsers
     */
    public Optional<AuthUsers> getAuthUser(String stuCode) {
        return authUserRepository.findFirstByUserAccount(stuCode);
    }
}
