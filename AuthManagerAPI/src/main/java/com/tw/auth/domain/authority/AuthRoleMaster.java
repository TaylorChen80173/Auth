package com.tw.auth.domain.authority;

import com.tw.auth.domain.AbstractAuditEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "AuthRoleMaster")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AuthRoleMaster extends AbstractAuditEntity {

    @Id @Column(name = "RoleMasterId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleMasterId;

    @Column(name = "RoleName")
    private String roleName;

    @Column(name = "IsValid")
    private String isValid = "Y";
}
