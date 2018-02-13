package com.tw.auth.domain.authority;

import com.tw.auth.domain.AbstractAuditEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "AuthUsers")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AuthUsers extends AbstractAuditEntity {

    @Id @Column(name = "UserId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "UserAccount")
    private String userAccount;

    @Column(name = "DisplayName")
    private String displayName;

    @Column(name = "Email") @Email
    private String email;

    @Column(name = "SourceType")
    private String sourceType = "SDB";

    @Column(name = "DeviceToken")
    private String deviceToken;

    @Column(name = "DeviceName")
    private String deviceName;

    @Column(name = "Memo")
    private String memo;

    @Column(name = "IsActive")
    private String isActive = "Y";

    @Transient
    @JsonSerialize
    private Integer roleMasterId;

    @Transient
    @JsonSerialize
    private String roleName;

    @Transient
    @JsonSerialize
    private List<Ap> authorities;

    public AuthUsers(String userAccount, String displayName, String email, String memo) {
        this.userAccount = userAccount;
        this.displayName = displayName;
        this.email = email;
        this.memo = memo;
    }

    @Data
    @AllArgsConstructor
    public static class Ap {
        private Integer applicationId;
        private String applicationName;
        private List<AItem> items;

        @Data
        @AllArgsConstructor
        public static class AItem {
            private Integer aItemId;
            private String aItemCode;
            private String aItemName;
            private String aItemValue;
        }
    }
}
