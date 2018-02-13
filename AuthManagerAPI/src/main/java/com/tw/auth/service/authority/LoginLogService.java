//package com.tw.auth.service.authority;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Slf4j
//@Service
//@Transactional(rollbackFor = Exception.class)
//public class LoginLogService {
//
//    private final LoginLogRepository loginLogRepository;
//
//    public LoginLogService(LoginLogRepository loginLogRepository) {
//        this.loginLogRepository = loginLogRepository;
//    }
//
//    /**
//     * 儲存登入記錄
//     *
//     * @param stuCode           學員代號
//     * @param loginIP           使用者 IP
//     * @param fromDevice        來源設備
//     * @param loginStatus       登入狀態
//     * @param loginFailedReason 登入錯誤原因
//     */
//    public void saveLoginLog(String stuCode, String loginIP, String fromDevice, String loginStatus, String loginFailedReason) {
//        java.sql.Timestamp now = new java.sql.Timestamp(new java.util.Date().getTime());
//        LoginLog log = new LoginLog();
//        log.setStuCode(stuCode);
//        log.setLoginDate(now);
//        log.setLoginIP(loginIP);
//        log.setFromDevice(fromDevice);
//        log.setLoginStatus(loginStatus);
//        log.setLoginFailedReason(loginFailedReason);
//        log.setCreator("System");
//        log.setCreateDT(now);
//        loginLogRepository.save(log);
//    }
//}
