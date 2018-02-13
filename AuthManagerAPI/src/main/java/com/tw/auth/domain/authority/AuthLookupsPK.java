package com.tw.auth.domain.authority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLookupsPK implements Serializable {

    @Id @Column(name = "LookupType")
    private String lookupType;

    @Id @Column(name = "LookupCode")
    private String lookupCode;
}
