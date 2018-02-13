package com.tw.auth.web.rest.controller.authority;

import com.codahale.metrics.annotation.Timed;
import com.tw.auth.domain.authority.AuthGroups;
import com.tw.auth.domain.authority.AuthUsers;
import com.tw.auth.service.authority.AuthUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing the AuthUsers management.
 */
@Slf4j
@Api(value = "系統權限帳號服務", description="系統權限帳號管理資訊、提供相關作業API")
@RestController
@RequestMapping("/api")
public class AuthUserResource {

    private final AuthUserService authUserService;

    public AuthUserResource(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    /**
     * GET  /getAuthUserList : 依據條件取得該權限使用者列表
     *
     * @param   userAccount 使用者帳號
     * @param   displayName 使用者顯示名稱
     * @return  使用者列表
     */
    @ApiOperation("依據條件取得該權限使用者列表")
    @GetMapping("/getAuthUserList")
    @Timed
    public Page<AuthUsers> getAuthUserList(String userAccount, String displayName, Pageable pageable) {
        return authUserService.getAuthUserList(userAccount, displayName, pageable);
    }

    /**
     * GET  /getAuthUserGroup : 取得權限使用者群組資料列表
     *
     * @param   userId  使用者代碼
     * @return  群組資料列表
     */
    @ApiOperation("取得權限使用者群組資料列表")
    @GetMapping("/getAuthUserGroup")
    @Timed
    public List<AuthGroups> getAuthUserGroup(Integer userId) {
        return authUserService.getAuthUserGroup(userId);
    }

    /**
     * PUT  /saveAuthUser : 儲存權限使用者資料
     *
     * @param   entity   使用者
     */
    @ApiOperation("儲存權限使用者資料")
    @PutMapping("/saveAuthUser")
    @Timed
    public AuthUsers saveAuthUser(@RequestBody AuthUsers entity) {
        return authUserService.saveAuthUser(entity);
    }

    /**
     * PUT  /saveAuthUser : 儲存權限使用者資料
     *
     * @param   params
     * userAccount    Ldap 使用者
     * password       使用者密碼
     */
    @ApiOperation("儲存權限使用者資料")
    @PutMapping("/updateAuthUserPassword")
    @Timed
    @SneakyThrows
    public void updateAuthUserPassword(@RequestBody Map<String, String> params) {
        authUserService.changeLdapPassword(params.get("userAccount"), params.get("password"));
    }

    /**
     * PUT  /saveAuthUserGroup : 儲存權限使用者群組資料
     *
     * @param   userId   使用者代碼
     * @param   groupIds 群組代碼列表
     */
    @ApiOperation("儲存權限使用者群組資料")
    @PostMapping("/saveAuthUserGroup/{userId}")
    @Timed
    public void saveAuthUserGroup(@PathVariable Integer userId, @RequestBody List<AuthGroups> groupIds) {
        if (groupIds.size() > 0) {
            authUserService.saveAuthUserGroup(userId,
                groupIds.stream().map(AuthGroups::getGroupId).collect(Collectors.toList()));
        }
    }

    /**
     * DELETE  /deleteAuthUser : 刪除權限使用者資料
     *
     * @param   userId  使用者代碼
     */
    @ApiOperation("刪除權限使用者資料")
    @DeleteMapping("/deleteAuthUser")
    @Timed
    public void deleteAuthUser(Integer userId) {
        authUserService.deleteAuthUser(userId);
    }

}
