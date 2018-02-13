package com.tw.auth.domain.authority;

import com.tw.auth.domain.AbstractAuditEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "AuthRoleDetail")
public class AuthRoleDetail extends AbstractAuditEntity {

    @Id @Column(name = "RoleDetailId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleDetailId;

    @Column(name = "RoleMasterId")
    private Integer roleMasterId;

    @Column(name = "RoleType")
    private String roleType;

    @Column(name = "RoleMemberId")
    private Integer roleMemberId;
}
