package com.tw.auth.domain.authority;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRIMappingPK implements Serializable {

    @Id @Column(name = "AItemId")
    @JsonProperty("aItemId")
    private Integer aItemId;

    @Id @Column(name = "RoleMasterId")
    private Integer roleMasterId;
}
