//package com.tw.auth.web.rest.controller.authority;
//
//import cn.apiclub.captcha.Captcha;
//import com.codahale.metrics.annotation.Timed;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.tw.auth.domain.authority.AuthLdapProperties;
//import com.tw.auth.domain.authority.AuthUsers;
//import com.tw.auth.domain.authority.AuthUsersLdap;
//import com.tw.auth.security.jwt.JWTConfigurer;
//import com.tw.auth.security.jwt.TokenProvider;
//import com.tw.auth.service.authority.AuthRoleService;
//import com.tw.auth.service.authority.AuthUserService;
//import com.tw.auth.service.authority.LoginLogService;
//import com.tw.auth.web.rest.util.ObjectExtensions;
//import com.tw.auth.web.rest.vm.LabelValueVM;
//import com.tw.auth.web.rest.vm.LoginVM;
//import com.unboundid.util.Base64;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.SneakyThrows;
//import lombok.experimental.ExtensionMethod;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//import org.thymeleaf.util.StringUtils;
//import sun.management.resources.agent;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.Valid;
//import java.util.List;
//import java.util.Map;
//
///**
// * 登入之使用者相關驗證管理.
// */
//@Slf4j
//@Api(value = "登入服務", description="帳號登入、帳號權限選單等，提供相關作業API")
//@RestController
//@RequestMapping("/api")
//@ExtensionMethod(ObjectExtensions.class)
//public class LoginController {
//
//    private final AuthLdapProperties authLdapProperties;
//
//    private final TokenProvider tokenProvider;
//
//    private final AuthenticationManager authenticationManager;
//
//    private final AuthRoleService authRoleService;
//
//    private final CaptchaUtils captchaUtils;
//
//    private final AuthUserService authUserService;
//
//    private final LoginLogService loginLogService;
//
//    private final EmailService emailService;
//
//    public LoginController(AuthLdapProperties authLdapProperties,
//                           TokenProvider tokenProvider,
//                           AuthenticationManager authenticationManager,
//                           AuthRoleService authRoleService,
//                           CaptchaUtils captchaUtils,
//                           AuthUserService authUserService,
//                           LoginLogService loginLogService,
//                           EmailService emailService) {
//        this.authLdapProperties = authLdapProperties;
//        this.tokenProvider = tokenProvider;
//        this.authenticationManager = authenticationManager;
//        this.authRoleService = authRoleService;
//        this.captchaUtils = captchaUtils;
//        this.authUserService = authUserService;
//        this.loginLogService = loginLogService;
//        this.emailService = emailService;
//    }
//
//    /**
//     * GET  /login/roleList : 取得權限 Role 選單列表.
//     *
//     * @return 下拉選單列表
//     */
//    @ApiOperation("取得權限 Role 選單列表")
//    @GetMapping("/login/roleList")
//    @Timed
//    @SneakyThrows(Exception.class)
//    public List<LabelValueVM> getAuthorityRoleList(@RequestParam String username) {
//        // 查詢該EMail對應之使用者帳號
//        Integer userId = authUserService.getAuthUserByEmail(username).orElseThrow(() ->
//            new Exception("無此使用者EMail")).getUserId();
//
//        log.debug("Get Authority Role Name Value Pair List");
//        // 依據角色成員代碼取得角色相關資料(須排除無效之角色)
//        return authRoleService.getAuthRoleByMemberId(userId);
//    }
//
//    /**
//     * GET  /login/getCaptcha : 取得 captcha 影像資料
//     *
//     * @return
//     */
//    @ApiOperation("取得 captcha 影像")
//    @GetMapping(path = "/login/getCaptcha")
//    @Timed
//    public String getCaptcha() {
//        Captcha captcha = captchaUtils.createCaptcha(110, 60, true);
//        return new StringBuffer(Base64.encode(captcha.getAnswer())).reverse() + "|" + captchaUtils.encodeBase64(captcha);
//    }
//
//    /**
//     * GET  /login/validateCaptcha : 檢核 captcha 字串
//     *
//     * @param   captcha 影像資料
//     * @return
//     */
//    @ApiOperation("取得 captcha 影像")
//    @GetMapping(path = "/login/validateCaptcha")
//    @Timed
//    @SneakyThrows()
//    public String validateCaptcha(String captcha, String encodeCaptcha) {
//        return StringUtils.equals(captcha, new String(Base64.decode(new StringBuffer(encodeCaptcha).reverse().toString()), "UTF-8")) ?
//            "" : getCaptcha();
//    }
//
//    /**
//     * Post  /login/authenticate : 登入之驗證作業並回應 JWT Token.
//     *
//     * @param loginVM   登入之傳遞參數物件
//     * @param response  Http Servlet Response
//     * @return
//     */
//    @ApiOperation("登入之驗證作業")
//    @PostMapping("/login/authenticate")
//    @Timed
//    public Object authorize(
//        @Valid @RequestBody LoginVM loginVM, HttpServletRequest request, HttpServletResponse response) {
//        // 查詢該EMail對應之使用者帳號
//        loginVM.setUsername(authUserService.getAuthUserByEmail(loginVM.getUsername()).get().getUserAccount());
//        // 取得對應之帳號TOKEN
//        UsernamePasswordAuthenticationToken authenticationToken =
//            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
//        // 取得登入之設備資訊
//        String ip = request.getHeader("X-FORWARDED-FOR");
//        if (StringUtils.isEmpty(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        final String clientIP = ip;
//        final String fromDevice = request.getHeader("User-Agent").indexOf("Mobile") > -1 ? "Mobile" : "PC";
//        try {
//            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            boolean rememberMe = loginVM.getRememberMe().nonNull(false);
//            String jwt = tokenProvider.createToken(authentication, rememberMe, loginVM.getRoleCode());
//            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
//            // 記錄登入成功資訊
//            try {
//                loginLogService.saveLoginLog(loginVM.getUsername(), clientIP, fromDevice, "S", "");
//            } catch(Exception e) {}
//            return new JWTToken(jwt);
//        } catch (AuthenticationException ae) {
//            log.trace("Authentication exception trace: {}", ae);
//            // 記錄登入失敗資訊
//            try {
//                loginLogService.saveLoginLog(loginVM.getUsername(), clientIP, fromDevice, "F", "密碼錯誤");
//            } catch(Exception e) {}
//            return getCaptcha();
//        }
//    }
//
//    /**
//     * Post  /login/forgetPassword : 忘記密碼
//     *
//     * @param username  忘記密碼之帳號(Email)
//     * @param captcha   Captcha 數字
//     * @return
//     */
//    @ApiOperation("忘記密碼")
//    @GetMapping("/login/forgetPassword")
//    @Timed
//    @SneakyThrows(Exception.class)
//    public String forgetPassword(String username) {
//        return authUserService.getAuthUserByEmail(username).map(u -> {
//            String password;
//            try {
//                password = authUserService.generateLdapUserPassword(u);
//            } catch (Exception e) {
//                return e.getMessage();
//            }
//            String subject = String.format("忘記密碼通知: %s同學", u.getDisplayName());
//            String content = String.format("%s同學, 新密碼為 %s 特此通知! 淨智組謹上",
//                u.getDisplayName(), password);
//            emailService.send(subject, content, u.getEmail());
//            return "";
//        }).orElseGet(() -> "抱歉！無法識別該email，請洽您的班幹部，確認您的帳號");
//    }
//
//    /**
//     * Post  /login/changePassword : 變更密碼
//     *
//     * @param username      變更密碼之帳號(Email)
//     * @param oldPassword   舊密碼
//     * @param newPassword   新密碼
//     * @param captcha   Captcha 數字
//     * @return
//     */
//    @ApiOperation("變更密碼")
//    @PostMapping("/login/changePassword")
//    @Timed
//    @SneakyThrows(Exception.class)
//    public String changePassword(@RequestBody Map<String, String> params) {
//        return authUserService.getAuthUserByEmail(params.get("username")).map(u -> {
//            try {
//                AuthUsersLdap ldap = authUserService.findLdapUser(u.getUserAccount());
//                if (ldap == null) {
//                    throw new Exception("無法變更密碼，原LDAP使用者資訊不存在");
//                }
//                if (StringUtils.equals(authLdapProperties.transferPassword(ldap.getPassword()),
//                    authLdapProperties.encodePassword(params.get("oldPassword")))) {
//                    authUserService.changeLdapPassword(u.getUserAccount(), params.get("newPassword"));
//                } else {
//                    throw new Exception("舊密碼不正確，請重新輸入!!");
//                }
//            } catch (Exception e) {
//                return e.getMessage();
//            }
//            return "";
//        }).orElseGet(() -> "抱歉！無法識別該email，請洽您的班幹部，確認您的帳號");
//    }
//
//    /**
//     * GET  /login/account : 取得目前登入之 Ldap 使用者資訊.
//     *
//     * @return AuUserVM
//     */
//    @ApiOperation("取得目前登入之使用者資訊")
//    @GetMapping("/login/account")
//    @Timed
//    public AuthUsers getAccount(Authentication authentication) {
//        return authUserService.getLoginAuUser(authentication);
//    }
//
//    /**
//     * JWT Authentication 回應之物件.
//     */
//    static class JWTToken {
//
//        private String idToken;
//
//        JWTToken(String idToken) {
//            this.idToken = idToken;
//        }
//
//        @JsonProperty("id_token")
//        String getIdToken() {
//            return idToken;
//        }
//
//        void setIdToken(String idToken) {
//            this.idToken = idToken;
//        }
//    }
//}
