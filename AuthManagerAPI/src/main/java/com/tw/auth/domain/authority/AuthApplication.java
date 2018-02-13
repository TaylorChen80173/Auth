package com.tw.auth.domain.authority;

import com.tw.auth.domain.AbstractAuditEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "AuthApplication")
public class AuthApplication extends AbstractAuditEntity  {

    @Id @Column(name = "ApplicationId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @Column(name = "ApplicationName")
    private String applicationName;

    @Column(name = "Module")
    private String module;

    @Column(name = "Route")
    private String route;
    
}
