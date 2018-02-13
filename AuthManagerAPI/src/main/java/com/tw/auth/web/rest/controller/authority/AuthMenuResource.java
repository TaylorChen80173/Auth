package com.tw.auth.web.rest.controller.authority;

import com.codahale.metrics.annotation.Timed;
import com.tw.auth.domain.authority.AuthMenu;
import com.tw.auth.service.authority.AuthMenuService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing the AuthMenu management.
 */
@Slf4j
@Api(value = "系統權限選單管理服務", description="系統權限選單管理及操作資訊、提供相關作業API")
@RestController
@RequestMapping("/api")
public class AuthMenuResource {

    private final AuthMenuService authMenuService;

    public AuthMenuResource(AuthMenuService authMenuService) {
        this.authMenuService = authMenuService;
    }

    /**
     * GET  /getAuthMenuList : 依據模組代號、父選單代號取得該選單列表
     *
     * @return  AuthMenu 列表
     */
    @ApiOperation("依據模組代號、父選單代號取得該選單列表")
    @GetMapping("/getAuthMenuList")
    @Timed
    public List<AuthMenu> getAuthMenuList(String module, Integer parentMenuId) {
        return authMenuService.getAuthMenuList(module, parentMenuId);
    }

    /**
     * PUT  /saveAuthMenu : 儲存權限選單資料
     *
     * @param   entity  選單資料
     */
    @ApiOperation("儲存權限選單資料")
    @PutMapping("/saveAuthMenu")
    @Timed
    public void saveAuthMenu(@RequestBody List<AuthMenu> entity) {
        authMenuService.saveAuthMenu(entity);
    }

    /**
     * DELETE  /deleteAuthMenu : 刪除權限選單資料
     *
     * @param   menuId  選單代碼
     */
    @ApiOperation("刪除權限選單資料")
    @DeleteMapping("/deleteAuthMenu")
    @Timed
    public void deleteAuthMenu(Integer menuId) {
        authMenuService.deleteAuthMenu(menuId);
    }

    /**
     * GET  /getAuthMenuByRoleList : 依據模組代號、角色代號取得該選單列表
     *
     * @param module        模組代號
     * @param roleMasterId  角色代號
     * @return AuthMenu 列表
     */
    @ApiOperation("依據模組代號、角色代號取得該選單列表")
    @GetMapping("/getAuthMenuByRoleList")
    @Timed
    public List<Map<String, Object>> getAuthMenuByRoleList(String module, Integer roleMasterId) {
        return authMenuService.getAuthMenuByRoleList(module, roleMasterId);
    }
}
