package com.tw.auth.web.rest.controller.authority;

import com.codahale.metrics.annotation.Timed;
import com.tw.auth.domain.authority.AuthGroups;
import com.tw.auth.domain.authority.AuthGroupsDetail;
import com.tw.auth.service.authority.AuthGroupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing the AuthGroups management.
 */
@Slf4j
@Api(value = "系統權限群組服務", description="系統權限群組管理資訊、提供相關作業API")
@RestController
@RequestMapping("/api")
public class AuthGroupResource {

    private final AuthGroupService authGroupService;

    public AuthGroupResource(AuthGroupService authGroupService) {
        this.authGroupService = authGroupService;
    }

    /**
     * GET  /getAuthGroupList : 取得該 AuthGroups 所有 Entity List
     *
     * @return  AuthGroups 列表
     */
    @ApiOperation("取得所有權限群組列表")
    @GetMapping("/getAuthGroupList")
    @Timed
    public List<AuthGroups> getAuthGroupList() {
        return authGroupService.getAuthGroupList();
    }

    /**
     * GET  /getAuthGroupPageList : 取得該 AuthGroups 所有 Entity List
     *
     * @param   groupName 群組名稱
     * @param   pageable
     * @return  AuthGroups 列表
     */
    @ApiOperation("取得所有權限群組列表")
    @GetMapping("/getAuthGroupPageList")
    @Timed
    public Page<AuthGroups> getAuthGroupPageList(String groupName, Pageable pageable) {
        return authGroupService.getAuthGroupPageList(groupName, pageable);
    }

    /**
     * GET  /getAuthGroupUserList : 取得該 AuthGroups 所屬之 AuthUsers Entity List
     *
     * @param   groupId 群組代碼
     * @return  AuthUsers 列表
     */
    @ApiOperation("取得權限群組所屬之使用者列表")
    @GetMapping("/getAuthGroupUserList")
    @Timed
    public List<Map<String, Object>> getAuthGroupUserList(Integer groupId) {
        return authGroupService.getAuthGroupUserList(groupId);
    }

    /**
     * PUT  /saveAuthGroup : 儲存權限群組資料
     *
     * @param   entity  群組資料
     */
    @ApiOperation("儲存權限群組資料")
    @PutMapping("/saveAuthGroup")
    @Timed
    public void saveAuthGroup(@RequestBody AuthGroups entity) {
        authGroupService.saveAuthGroup(entity);
    }

    /**
     * DELETE  /deleteAuthGroup : 刪除權限群組資料
     *
     * @param   groupId  群組代碼
     */
    @ApiOperation("刪除權限群組資料")
    @DeleteMapping("/deleteAuthGroup")
    @Timed
    public boolean deleteAuthGroup(Integer groupId) {
        return authGroupService.deleteAuthGroup(groupId);
    }

    /**
     * PUT  /saveAuthGroupDetails : 儲存權限群組明細資料
     *
     * @param   details  群組明細資料列表
     */
    @ApiOperation("儲存權限群組明細資料")
    @PutMapping("/saveAuthGroupDetails")
    @Timed
    public void saveAuthGroupDetails(@RequestBody List<AuthGroupsDetail> details) {
        authGroupService.saveAuthGroupDetails(details);
    }

    /**
     * POST  /deleteAuthGroupDetails : 刪除權限群組成員資料
     *
     * @param   details  群組成員資料
     */
    @ApiOperation("刪除權限群組成員資料")
    @PostMapping("/deleteAuthGroupDetails")
    @Timed
    public void deleteAuthGroupDetails(@RequestBody List<AuthGroupsDetail> details) {
        authGroupService.deleteAuthGroupDetails(details);
    }
}
