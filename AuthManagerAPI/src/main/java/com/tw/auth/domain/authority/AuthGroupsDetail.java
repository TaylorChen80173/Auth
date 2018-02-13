package com.tw.auth.domain.authority;

import com.tw.auth.domain.AbstractAuditEntity;
import com.tw.auth.config.Constants;
import com.tw.auth.security.SecurityUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "AuthGroupsDetail")
public class AuthGroupsDetail extends AbstractAuditEntity {

    @Id @Column(name = "GroupDetailId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupDetailId;

    @Column(name = "GroupId")
    private Integer groupId;

    @Column(name = "UserId")
    private Integer userId;

    public AuthGroupsDetail(Integer groupId, Integer userId) {
        this.groupId = groupId;
        this.userId = userId;
        String userName = SecurityUtils.getCurrentUserName();
        userName = userName != null && !userName.equals("anonymousUser") ? userName : Constants.SYSTEM_ACCOUNT;
        this.setCreator(userName);
        this.setUpdater(userName);
    }
}
