package com.tw.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tw.auth.domain.authority.AuthApplication;

import java.util.List;
import java.util.stream.Stream;

/**
 * 利用 Spring Data JPA repository 取得 AuthApplication 相關資料物件.
 */
public interface AuthApplicationRepository extends JpaRepository<AuthApplication, Integer> {

    /**
     * 依據 module 從 AuthApplication 取得所有資料
     *
     * @param module 模組
     * @return AuthApplication Entity List
     */
    Stream<AuthApplication> findByModule(String module);

    /**
     * 依據 module、applicationId 從 AuthApplication 取得所有應用程式的資料
     *
     * @param   module 群組代碼
     * @param   applicationId 應用程式代碼
     * @return  AuthApplication Page List
     */
    @Query(value =
        "SELECT a FROM AuthApplication a " +
            "WHERE a.module = ?1 " +
            "  AND (?2 IS NULL OR a.applicationId = ?2)"
    )
    List<AuthApplication> findByModuleAndApplicationId(String module, Integer applicationId);
}
