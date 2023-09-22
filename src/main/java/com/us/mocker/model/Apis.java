package com.us.mocker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "apis")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Apis {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    private Collections collection;

    @Column(name = "end_point")
    private String endPoint;

    @Column(name = "method")
    private String method;

    @Column(name = "response_http_status")
    private int responseHttpStatus;

    @Column(name = "response_headers")
    private String responseHeaders;

    @Column(name = "response_body")
    private String responseBody;

}
