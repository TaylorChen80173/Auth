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
@Table(name = "AuthApplicationItem")
public class AuthApplicationItem extends AbstractAuditEntity {

    @Id @Column(name = "AItemId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("aItemId")
    private Integer aItemId;

    @Column(name = "ApplicationId")
    private Integer applicationId;

    @Column(name = "AItemCode")
    @JsonProperty("aItemCode")
    private String aItemCode;

    @Column(name = "AItemType")
    @JsonProperty("aItemType")
    private String aItemType;

    @Column(name = "AItemName")
    @JsonProperty("aItemName")
    private String aItemName;

    @Column(name = "AItemDefaultValue")
    @JsonProperty("aItemDefaultValue")
    private String aItemDefaultValue;

    public AuthApplicationItem(Integer applicationId, String aItemCode, String aItemType, String aItemName, String aItemDefaultValue) {
        this.applicationId = applicationId;
        this.aItemCode = aItemCode;
        this.aItemType = aItemType;
        this.aItemName = aItemName;
        this.aItemDefaultValue = aItemDefaultValue;
        String userName = SecurityUtils.getCurrentUserName();
        userName = userName != null && !userName.equals("anonymousUser") ? userName : Constants.SYSTEM_ACCOUNT;
        this.setCreator(userName);
        this.setUpdater(userName);
    }
}
