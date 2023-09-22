package com.us.mocker.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.us.mocker.model.Apis;
import com.us.mocker.repo.ApiRepo;
import com.us.mocker.requestresponse.ApiRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Component
public class MockApiFilter implements Filter {

    @Autowired
    ApiRepo apiRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api")) {

            String[] uriBreak = requestUri.split("/");
            String endPoint = String.join("/", Arrays.copyOfRange(uriBreak, 3, uriBreak.length));
            endPoint = "/" + endPoint;

            String method = request.getMethod();

            Apis apis = apiRepo.getApi(uriBreak[2], endPoint, method);

            response.setStatus(apis.getResponseHttpStatus());
            setResponseHeaders(response, apis.getResponseHeaders());
            response.getWriter().write(apis.getResponseBody());

            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setResponseHeaders(HttpServletResponse response, String headers) {
        try {
            Map<String, String> headersMap = objectMapper
                    .readValue(headers, new TypeReference<>() {});

            for(Map.Entry<String, String> header: headersMap.entrySet()) {
                response.setHeader(header.getKey(), header.getValue());
            }
        } catch (JsonProcessingException e) {
           response.setHeader("header", headers);
        }
    }
}
