package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tw.auth.domain.authority.AuthLookups;
import com.tw.auth.domain.authority.AuthLookupsPK;

import java.util.List;

/**
 *  利用 Spring Data JPA repository 取得 AuthLookups 相關資料物件.
 */
public interface AuthLookupRepository extends JpaRepository<AuthLookups, AuthLookupsPK> {

    /**
     * 從 AuthLookups 取得所有符合 lookupType 條件的資料
     *
     * @param lookupType 類別代碼
     * @return Stream<AuthLookups>
     */
    List<AuthLookups> findByLookupType(String lookupType);

    /**
     * 從 AuthLookups 取得符合 LookupType 及 LookupCode 條件的資料
     *
     * @param lookupType 類別
     * @param lookupCode 類別代碼
     * @return AuConfig
     */
    AuthLookups findFirstByLookupTypeAndLookupCode(String lookupType, String lookupCode);
}
