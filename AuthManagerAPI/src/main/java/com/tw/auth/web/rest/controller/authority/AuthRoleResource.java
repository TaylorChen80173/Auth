package com.tw.auth.web.rest.controller.authority;

import com.codahale.metrics.annotation.Timed;
import com.tw.auth.domain.authority.AuthRIMapping;
import com.tw.auth.domain.authority.AuthRoleDetail;
import com.tw.auth.domain.authority.AuthRoleMaster;
import com.tw.auth.service.authority.AuthRoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing the AuthRoleMaster/AuthRoleDetail/AuthRIMapping management.
 */
@Slf4j
@Api(value = "系統權限角色管理服務", description="系統權限角色管理及操作資訊、提供相關作業API")
@RestController
@RequestMapping("/api")
public class AuthRoleResource {

    private AuthRoleService authRoleService;

    public AuthRoleResource(AuthRoleService authRoleService) {
        this.authRoleService = authRoleService;
    }

    /**
     * GET  /getAuthRoleList : 取得角色所有資料列表
     *
     * @return  角色所有資料列表
     */
    @ApiOperation("取得角色所有資料列表")
    @GetMapping("/getAuthRoleList")
    @Timed
    public List<AuthRoleMaster> getAuthRoleList() {
        return authRoleService.getAuthRoleList();
    }

    /**
     * GET  /getAuthRIMappingList : 依據角色代碼取得該角色與應用程式對應列表
     *
     * @param   roleMasterId    角色代碼
     * @return  角色與應用程式對應列表
     */
    @ApiOperation("依據角色代碼取得該角色與應用程式功能限制對應列表")
    @GetMapping("/getAuthRIMappingList")
    @Timed
    public List<Map<String, Object>> getAuthRIMappingList(Integer roleMasterId) {
        return authRoleService.getAuthRIMappingList(roleMasterId);
    }

    /**
     * GET  /getAuthRIMappingItemList : 依據角色代碼取得該角色與應用程式功能限制對應列表
     *
     * @param   roleMasterId    角色代碼
     * @param   applicationId   角色代碼
     * @return  角色與應用程式功能限制對應列表
     */
    @ApiOperation("依據角色代碼取得該角色與應用程式功能限制對應列表")
    @GetMapping("/getAuthRIMappingItemList")
    @Timed
    public List<Map<String, Object>> getAuthRIMappingItemList(Integer roleMasterId, Integer applicationId) {
        return authRoleService.getAuthRIMappingItemList(roleMasterId, applicationId);
    }

    /**
     * GET  /getAuthRoleDetailList : 依據角色代碼取得該角色成員列表
     *
     * @param   roleMasterId    角色代碼
     * @return  角色成員列表
     */
    @ApiOperation("依據角色代碼取得該角色成員列表")
    @GetMapping("/getAuthRoleDetailList")
    @Timed
    public List<Map<String, Object>> getAuthRoleDetailList(Integer roleMasterId) {
        return authRoleService.getAuthRoleDetailList(roleMasterId);
    }

    /**
     * PUT  /saveAuthRole : 儲存權限角色資料
     *
     * @param   entity  角色資料
     */
    @ApiOperation("儲存權限應用程式資料")
    @PutMapping("/saveAuthRole")
    @Timed
    public AuthRoleMaster saveAuthRole(@RequestBody AuthRoleMaster entity) {
        return authRoleService.saveAuthRole(entity);
    }

    /**
     * PUT  /saveAuthRoleDetail : 儲存權限角色所屬成員資料
     *
     * @param   details  角色所屬成員資料
     */
    @ApiOperation("儲存權限角色所屬成員資料")
    @PostMapping("/saveAuthRoleDetail/{roleMasterId}")
    @Timed
    public void saveAuthRoleDetail(@PathVariable Integer roleMasterId, @RequestBody List<AuthRoleDetail> details) {
        authRoleService.saveAuthRoleDetail(roleMasterId, details);
    }

    /**
     * GET  /initAuthRIMapping : 儲存權限角色所屬成員資料
     *
     * @param   roleMasterId  角色代碼
     * @param   applicationId 應用程式代碼
     */
    @ApiOperation("儲存權限角色所屬成員資料")
    @GetMapping("/initAuthRIMapping")
    @Timed
    public void initAuthRIMapping(Integer roleMasterId, Integer applicationId) {
        authRoleService.initAuthRIMapping(roleMasterId, applicationId);
    }

    /**
     * Post  /saveAuthRIMapping : 儲存權限角色對應之應用程式功能限制資料
     *
     * @param   mappings    角色對應之應用程式功能限制
     */
    @ApiOperation("儲存權限角色對應之應用程式功能限制資料")
    @PostMapping("/saveAuthRIMapping/{roleMasterId}/{applicationId}")
    @Timed
    public void saveAuthRIMapping(@PathVariable Integer roleMasterId, @PathVariable Integer applicationId, @RequestBody List<AuthRIMapping> mappings) {
        authRoleService.saveAuthRIMapping(roleMasterId, applicationId, mappings);
    }

    /**
     * DELETE  /deleteAuthRole : 刪除權限角色資料
     *
     * @param   roleMasterId    角色代碼
     * @return  true: 刪除成功 false: 刪除失敗，尚有角色對應無法刪除
     */
    @ApiOperation("刪除權限角色資料")
    @DeleteMapping("/deleteAuthRole")
    @Timed
    public boolean deleteAuthRole(Integer roleMasterId) {
        return authRoleService.deleteAuthRole(roleMasterId);
    }

    /**
     * DELETE  /deleteAuthRoleDetail : 刪除權限角色所屬成員資料
     *
     * @param   roleDetailId  角色所屬成員代碼
     */
    @ApiOperation("刪除權限角色所屬成員資料")
    @DeleteMapping("/deleteAuthRoleDetail")
    @Timed
    public void deleteAuthRoleDetail(Integer roleDetailId) {
        authRoleService.deleteAuthRoleDetail(roleDetailId);
    }

    /**
     * DELETE  /deleteAuthRIMapping : 刪除權限角色所對應之應用程式功能限制資料
     *
     * @param   roleMasterId  角色代碼
     * @param   aItemId       應用程式功能限制代碼
     */
    @ApiOperation("刪除權限角色所對應之應用程式功能限制資料")
    @DeleteMapping("/deleteAuthRIMapping")
    @Timed
    public void deleteAuthRIMapping(Integer roleMasterId, Integer aItemId) {
        authRoleService.deleteAuthRIMapping(roleMasterId, aItemId);
    }

    /**
     * DELETE  /deleteAllAuthRIMapping : 刪除權限角色所對應之應用程式資料
     *
     * @param   roleMasterId    角色代碼
     * @param   applicationId   應用程式代碼
     */
    @ApiOperation("刪除權限角色所對應之應用程式資料")
    @DeleteMapping("/deleteAllAuthRIMapping")
    @Timed
    public void deleteAllAuthRIMapping(Integer roleMasterId, Integer applicationId) {
        authRoleService.deleteAllAuthRIMapping(roleMasterId, applicationId);
    }
}
