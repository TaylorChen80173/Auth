package com.tw.auth.domain.authority;

import com.tw.auth.domain.AbstractAuditEntity;
import com.tw.auth.config.Constants;
import com.tw.auth.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "AuthRIMapping")
@IdClass(AuthRIMappingPK.class)
public class AuthRIMapping extends AbstractAuditEntity {

    @Id @Column(name = "RoleMasterId")
    private Integer roleMasterId;

    @Id @Column(name = "AItemId")
    @JsonProperty("aItemId")
    private Integer aItemId = 0;

    @Column(name = "AItemValue")
    @JsonProperty("aItemValue")
    private String aItemValue;

    @Column(name = "ApplicationId")
    private Integer applicationId;

    public AuthRIMapping(Integer roleMasterId, Integer aItemId, Integer applicationId, String aItemValue) {
        this.roleMasterId = roleMasterId;
        this.aItemId = aItemId;
        this.applicationId = applicationId;
        this.aItemValue = aItemValue;
        String userName = SecurityUtils.getCurrentUserName();
        userName = userName != null && !userName.equals("anonymousUser") ? userName : Constants.SYSTEM_ACCOUNT;
        this.setCreator(userName);
        this.setUpdater(userName);
    }
}
