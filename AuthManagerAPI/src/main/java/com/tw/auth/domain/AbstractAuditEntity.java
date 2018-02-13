package com.tw.auth.domain;

import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Data
@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditEntity implements Serializable {

    @CreatedBy
    @Column(name = "Creator")
    private String creator;

    @CreatedDate
    @Column(name = "CreateDT")
    private Instant createDT = Instant.now();

    @LastModifiedBy
    @Column(name = "Updater")
    private String updater;

    @LastModifiedDate
    @Column(name = "UpdateDT")
    private Instant updateDT = Instant.now();
}
