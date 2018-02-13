package com.tw.auth.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.tw.auth.domain.User;
import com.tw.auth.repository.UserRepository;
import com.tw.auth.security.SecurityUtils;
import com.tw.auth.service.MailService;
//import com.tw.auth.service.UserService;
import com.tw.auth.service.dto.UserDTO;
import com.tw.auth.web.rest.errors.*;
import com.tw.auth.web.rest.vm.KeyAndPasswordVM;
import com.tw.auth.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.tw.auth.web.rest.AbstractSdbResource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Api(value = "法會營隊活動管理", description = "法會營隊活動查詢、新增修改等行為，提供相關作業API")
@RestController
@RequestMapping("/api")
public class ActivitiesResource extends AbstractSdbResource{

    public ActivitiesResource() {

    }


    /**
     * GET  /getSwaggerString : get the current user.
     *
     * @return string resuit
     */
    @ApiOperation("Test")
    @GetMapping("/getSwaggerString")
    @Timed
    public String getSwaggerString() {
        return "aaa" + "BBB";
    }

}
