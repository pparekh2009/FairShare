package com.priyanshparekh.fairshareapi.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadResponse {

    private boolean success;
    private String message;

}
