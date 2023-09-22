package com.us.mocker.requestresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequest {
    private String id;
    private String endPoint;
    private String method;
    private int httpStatus;
    private String responseHeaders;
    private String responseBody;
}
