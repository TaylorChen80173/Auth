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
@Table(name = "AuthMenu")
public class AuthMenu extends AbstractAuditEntity {

    @Id @Column(name = "MenuId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;

    @Column(name = "ParentMenuId")
    private Integer parentMenuId;

    @Column(name = "SeqId")
    private Integer seqId;

    @Column(name = "Module")
    private String module;

    @Column(name = "MenuName")
    private String menuName;

    @Column(name = "ApplicationId")
    private Integer applicationId;
}
