package com.tw.auth.domain.authority;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper=false)
public class AuthLdapProperties extends LdapProperties {

    private String usersDn;

    private String groupDn;

    private boolean updatable;

    private final PasswordEncoder passwordEncoder;

    public AuthLdapProperties(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Name toUserSearchDn(String username) {
        return LdapNameBuilder.newInstance(usersDn)
            .add("uid=" + username)
            .build();
    }

    public Name toAbsoluteUserDn(String username) {
        return LdapNameBuilder.newInstance(getBase())
            .add(usersDn)
            .add("uid=" + username)
            .build();
    }

    public String toRelativeUserId(Name absoluteDn) {
        return LdapUtils.getStringValue(absoluteDn,"uid");
    }

    public LdapName toAbsoluteDn(Name relativeName) {
        return LdapNameBuilder.newInstance(getBase())
            .add(relativeName)
            .build();
    }

    public Iterable<Name> toRelativeIds(Iterable<Name> absoluteIds) {
        return Iterables.transform(absoluteIds, new Function<Name, Name>() {
            @Override
            public Name apply(Name input) {
                return LdapUtils.removeFirst(input, LdapUtils.newLdapName(getBase()));
            }
        });
    }

    /**
     * 加密使用者密碼
     *
     * @param password
     * @return
     */
    public String encodePassword(String password) {
        return passwordEncoder.encodePassword(password, null);
    }

    /**
     * 使用者密碼轉換
     *
     * @param password character number string with comma join
     * @return password
     */
    public String transferPassword(String password) {
        return Arrays.stream(password.split(",")).
            map(p -> Character.toString((char)Integer.parseInt(p))).
            collect(Collectors.joining(""));
    }
}
