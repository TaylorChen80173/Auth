package com.tw.auth.domain.authority;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@QueryEntity
@Data
@Entry(objectClasses = { "inetOrgPerson", "organizationalPerson", "person", "top" }, base = "ou=User")
public class AuthUsersLdap {

    @Id
    private Name dn;

    @DnAttribute(value = "uid", index = 1)
    private String uid;

    @Attribute(name = "sn")
    private String surName;

    @Attribute(name = "cn")
    private String commonName;

    @Attribute(name = "displayName")
    private String displayName;

    @Attribute(name = "userPassword")
    private String password;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "description")
    private String memo;
}
