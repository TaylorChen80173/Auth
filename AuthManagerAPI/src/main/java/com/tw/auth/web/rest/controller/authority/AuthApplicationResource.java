package com.tw.auth.web.rest.controller.authority;



import com.codahale.metrics.annotation.Timed;
import com.tw.auth.domain.authority.AuthApplication;
import com.tw.auth.domain.authority.AuthApplicationItem;
import com.tw.auth.service.authority.AuthApplicationService;
import com.tw.auth.web.rest.vm.LabelValueVM;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing the AuthApplication management.
 */
@Slf4j
@Api(value = "系統權限應用程式及功能限制服務", description="系統權限應用程式及功能限制管理資訊、提供相關作業API")
@RestController
@RequestMapping("/api")
public class AuthApplicationResource {

    private AuthApplicationService authApplicationService;

    public AuthApplicationResource(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    /**
     * GET  /getAuthApplicationPairs : 依據模組取得該應用程式選單列表
     *
     * @param   module 模組
     * @return  應用程式選單列表
     */
    @ApiOperation("依據模組取得該應用程式選單列表")
    @GetMapping("/getAuthApplicationPairs")
    @Timed
    public List<LabelValueVM> getAuthApplicationPairs(String module) {
        return authApplicationService.getAuthApplicationPairs(module);
    }

    /**
     * GET  /getAuthApplicationList : 依據模組、應用程式代碼取得該應用程式分頁列表
     *
     * @param   module          模組
     * @param   applicationId   應用程式代碼
     * @return  應用程式列表
     */
    @ApiOperation("依據模組、應用程式代碼取得該應用程式列表")
    @GetMapping("/getAuthApplicationList")
    @Timed
    public List<AuthApplication> getAuthApplicationList(String module, Integer applicationId, Pageable pageable) {
        return authApplicationService.getAuthApplicationList(module, applicationId);
    }

    /**
     * GET  /getAuthApplicationItemList : 依據應用程式代碼取得該應用程式功能限制分頁列表
     *
     * @param   applicationId   應用程式代碼
     * @return  應用程式功能限制列表
     */
    @ApiOperation("依據應用程式代碼取得該應用程式功能限制分頁列表")
    @GetMapping("/getAuthApplicationItemList")
    @Timed
    public List<AuthApplicationItem> getAuthApplicationItemList(Integer applicationId) {
        return authApplicationService.getAuthApplicationItemList(applicationId);
    }

    /**
     * PUT  /saveAuthApplication : 儲存權限應用程式資料
     *
     * @param   entity  應用程式資料
     */
    @ApiOperation("儲存權限應用程式資料")
    @PutMapping("/saveAuthApplication")
    @Timed
    public void saveAuthApplication(@RequestBody AuthApplication entity) {
        authApplicationService.saveAuthApplication(entity);
    }

    /**
     * PUT  /saveAuthApplicationItem : 儲存權限應用程式功能限制資料
     *
     * @param   entity  應用程式功能限制資料
     */
    @ApiOperation("儲存權限應用程式功能限制資料")
    @PutMapping("/saveAuthApplicationItem")
    @Timed
    public void saveAuthApplicationItem(@RequestBody AuthApplicationItem entity) {
        authApplicationService.saveAuthApplicationItem(entity);
    }

    /**
     * DELETE  /deleteAuthApplication : 刪除權限應用程式資料
     *
     * @param   applicationId  應用程式代碼
     * @return  true: 刪除成功 false: 刪除失敗，尚有角色設定無法刪除
     */
    @ApiOperation("刪除權限群組資料")
    @DeleteMapping("/deleteAuthApplication")
    @Timed
    public boolean deleteAuthApplication(Integer applicationId) {
        return authApplicationService.deleteAuthApplication(applicationId);
    }

    /**
     * DELETE  /deleteAuthApplicationItem : 刪除權限應用程式功能限制資料
     *
     * @param   aItemId  應用程式功能限制代碼
     * @return  true: 刪除成功 false: 刪除失敗，尚有角色設定無法刪除
     */
    @ApiOperation("刪除權限群組資料")
    @DeleteMapping("/deleteAuthApplicationItem")
    @Timed
    public boolean deleteAuthApplicationItem(Integer aItemId) {
        return authApplicationService.deleteAuthApplicationItem(aItemId);
    }
}
