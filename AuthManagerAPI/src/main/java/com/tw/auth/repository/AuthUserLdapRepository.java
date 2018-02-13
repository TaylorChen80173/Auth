package com.tw.auth.repository;

import org.springframework.data.ldap.repository.support.SimpleLdapRepository;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;

import com.tw.auth.domain.authority.AuthUsersLdap;

/**
 * Spring Data Ldap repository 使用 AuthUsers 物件.
 */
public class AuthUserLdapRepository extends SimpleLdapRepository<AuthUsersLdap> {

    public AuthUserLdapRepository(LdapOperations ldapOperations, ObjectDirectoryMapper odm) {
        super(ldapOperations, odm, AuthUsersLdap.class);
    }
}
