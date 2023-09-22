package com.us.mocker.misc;

import java.util.Arrays;
import java.util.List;

public class Constants {
    private Constants() {
    }

    public static final List<String> METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE");
    public static final String END_POINT_REGEX = "^[a-zA-Z0-9/-]*$";
    public static final String COLLECTIONS = "collections";
    public static final String COLLECTION_ID = "collectionId";
    public static final String EMAIL = "email";
    public static final String REDIRECT_TO_GET_COLLECTION_API = "redirect:/collection/%s/api";
    public static final String REDIRECT_TO_GET_COLLECTION = "redirect:/collection";
    public static final String REDIRECT_TO_UPDATE_API_PAGE = "redirect:/collection/%s/api/%s";
    public static final String MOCK_APIS = "mockApis";
    public static final String COLLECTION_HTML = "collection";
}
