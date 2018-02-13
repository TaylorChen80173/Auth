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
@Table(name = "AuthGroups")
public class AuthGroups extends AbstractAuditEntity {

    @Id @Column(name = "GroupId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupId;

    @Column(name = "GroupName")
    private String groupName;

    @Column(name = "Description")
    private String description;
}
