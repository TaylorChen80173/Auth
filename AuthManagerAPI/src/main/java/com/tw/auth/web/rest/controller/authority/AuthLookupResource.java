package com.tw.auth.web.rest.controller.authority;

import com.codahale.metrics.annotation.Timed;
import com.tw.auth.domain.authority.AuthLookups;
import com.tw.auth.service.authority.AuthLookupService;
import com.tw.auth.web.rest.vm.LabelValueVM;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing the AuthLookup configuration .
 */
@Slf4j
@Api(value = "系統權限資料設定服務", description="系統權限資料相關設定資訊、提供相關作業API")
@RestController
@RequestMapping("/api")
public class AuthLookupResource {

    private final AuthLookupService authLookupService;

    public AuthLookupResource(AuthLookupService authLookupService) {
        this.authLookupService = authLookupService;
    }

    /**
     * GET  /getAuthLookupTypePairs : 取得該 AuthLookup LookupType 所屬之 AuthLookups Entity List
     *
     * @param   type    lookupType
     * @return  List<LabelValueVM>
     */
    @ApiOperation("取得該 AuthLookup LookupType 選單列表")
    @GetMapping("/getAuthLookupTypePairs")
    @Timed
    public List<LabelValueVM> getAuthLookupTypePairs(String type) {
        return authLookupService.getLookupTypePairs(type);
    }

    /**
     * GET  /getAuthLookupValues : 取得該 AuthLookup LookupType 及 LookupCode 所屬之 AuthLookups Entity
     *
     * @param   type    lookupType
     * @param   code    lookupCode
     * @return  AuthLookups
     */
    @ApiOperation("取得單筆 AuthLookups 資料")
    @GetMapping("/getAuthLookupValues")
    @Timed
    public AuthLookups getAuthLookupValues(String type, String code) {
        return authLookupService.getLookupValues(type, code);
    }
}
