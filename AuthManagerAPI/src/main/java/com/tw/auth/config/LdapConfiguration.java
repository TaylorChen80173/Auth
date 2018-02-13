package com.tw.auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import com.tw.auth.domain.authority.AuthLdapProperties;
import com.tw.auth.repository.AuthLookupRepository;
import com.tw.auth.repository.AuthUserLdapRepository;

import java.util.Arrays;

@Configuration
public class LdapConfiguration {

    private final AuthLookupRepository authLookupRepository;

    public LdapConfiguration(AuthLookupRepository authLookupRepository) {
        this.authLookupRepository = authLookupRepository;
    }

    /**
     * 從資料庫取得多個 Ldap 相關資料屬性
     */
    @Bean
    public AuthLdapProperties authLdapProperties() {
        AuthLdapProperties p = new AuthLdapProperties(new LdapSha2PasswordEncoder());
        authLookupRepository.findByLookupType("LDAP").forEach(info -> {
            switch (info.getLookupCode()) {
                case "AD_URLS"   : p.setUrls(new String[] { info.getLookupValues() }); break;
                case "BASE_DN"   : p.setBase(info.getLookupValues()); break;
                case "ROOT_DN"   : p.setUsername(info.getLookupValues()); break;
                case "ROOT_PW"   : p.setPassword(info.getLookupValues()); break;
                case "USERS_DN"  : p.setUsersDn(info.getLookupValues()); break;
            }
        });
        return p;
    }

    /**
     * 建立多個 Ldap ContextSource 提供給 Spring Security 及 Ldap Template 使用
     * @return
     */
    @Bean
    @DependsOn("authLdapProperties")
    public LdapContextSource authLdapContextSource() {
        AuthLdapProperties p = authLdapProperties();
        DefaultSpringSecurityContextSource contextSource =
            new DefaultSpringSecurityContextSource(Arrays.asList(p.getUrls()), p.getBase());
        contextSource.setUserDn(p.getUsername());
        contextSource.setPassword(p.getPassword());
        contextSource.setReferral("follow");
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    /**
     * 從 Ldap ContextSource Map 建立相對應之 Ldap Template
     */
    @Bean
    @DependsOn("authLdapContextSource")
    public LdapTemplate authLdapTemplate() {
        return new LdapTemplate(authLdapContextSource());
    }

    /**
     * 產生物件對應作業提供 Ldap Template 及 Ldap Repository 使用
     * @return
     */
    @Bean
    public ObjectDirectoryMapper objectDirectoryMapper() {
        return new DefaultObjectDirectoryMapper();
    }

    /**
     * AuUser Ldap repository 建立.
     * @return
     */
    @Bean
    @DependsOn({"authLdapTemplate", "objectDirectoryMapper" })
    public AuthUserLdapRepository authUserLdapRepository() {
        return new AuthUserLdapRepository(authLdapTemplate(), objectDirectoryMapper());
    }
}
