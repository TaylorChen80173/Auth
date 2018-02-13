package com.tw.auth.service.authority;


import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tw.auth.domain.authority.AuthLookups;
import com.tw.auth.repository.AuthLookupRepository;
import com.tw.auth.web.rest.util.ObjectExtensions;
import com.tw.auth.web.rest.vm.LabelValueVM;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@ExtensionMethod(ObjectExtensions.class)
public class AuthLookupService {

    private final AuthLookupRepository authLookupRepository;

    public AuthLookupService(AuthLookupRepository authLookupRepository) {
        this.authLookupRepository = authLookupRepository;
    }

    /**
     * 取得該 AuthLookup LookupType 所屬之 AuthLookups Entity List
     *
     * @return AuthLookups Entity List
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<LabelValueVM> getLookupTypePairs(String lookupType) {
        return authLookupRepository.findByLookupType(lookupType).stream().map(l ->
            new LabelValueVM(l.getLookupValues(), l.getLookupCode())
        ).collect(Collectors.toList());
    }

    /**
     * 取得該 AuthLookup LookupType 及 LookupCode 所屬之 AuthLookups Entity
     *
     * @return AuthLookups Entity
     */
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public AuthLookups getLookupValues(String lookupType, String lookupCode) {
        return authLookupRepository.findFirstByLookupTypeAndLookupCode(lookupType, lookupCode);
    }
}
