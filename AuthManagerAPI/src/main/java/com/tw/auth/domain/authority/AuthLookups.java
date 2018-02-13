package com.tw.auth.domain.authority;

import com.tw.auth.domain.AbstractAuditEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "AuthLookups")
@IdClass(AuthLookupsPK.class)
public class AuthLookups extends AbstractAuditEntity {

    @Id @Column(name = "LookupType")
    private String lookupType;

    @Id @Column(name = "LookupCode")
    private String lookupCode;

    @Column(name = "LookupValues")
    private String lookupValues;

    @Column(name = "ParentLookupType")
    private String parentLookupType;

    @Column(name = "ParentLookupCode")
    private String parentLookupCode;

    @Column(name = "Seq")
    private Short seq;

    @Column(name = "Memo")
    private String memo;
}
