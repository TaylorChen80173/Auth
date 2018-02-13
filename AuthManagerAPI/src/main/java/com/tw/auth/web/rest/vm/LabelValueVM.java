package com.tw.auth.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelValueVM {

    private String label;

    private String value;

    private boolean selectable = true;

    public LabelValueVM (String label, String value) {
        this.label = label;
        this.value = value;
    }

}
