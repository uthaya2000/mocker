package com.us.mocker.service;

import com.us.mocker.misc.Constants;
import com.us.mocker.model.Apis;
import com.us.mocker.model.Collections;
import com.us.mocker.repo.ApiRepo;
import com.us.mocker.repo.CollectionRepo;
import com.us.mocker.requestresponse.ApiRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class MockerService {

    @Autowired
    CollectionRepo collectionRepo;
    @Autowired
    ApiRepo apiRepo;

    private static final String ERROR_KEY = "error";
    public String getCollectionByEmail(HttpSession session, String email, Model model, String name) {
        List<Collections> collections;

        if (StringUtils.isEmpty(name))
            collections = collectionRepo.getCollectionsByEmail(email);
        else
            collections = collectionRepo.searchCollectionByName(name.toLowerCase(), email);

        String error = (String) session.getAttribute(ERROR_KEY);
        model.addAttribute(Constants.COLLECTIONS, collections);
        model.addAttribute(Constants.EMAIL, email);
        if (!StringUtils.isEmpty(error)) {
            model.addAttribute(ERROR_KEY, error);
            session.removeAttribute(ERROR_KEY);
        }
        return Constants.COLLECTION_HTML;
    }

    public String createCollection(String collectionName, HttpSession session, String email) {
        Collections duplicateColl = collectionRepo
                .getCollectionByNameAndEmail(collectionName.toLowerCase(), email);
        if (duplicateColl != null) {
            session.setAttribute(ERROR_KEY, "Collection Already Present");
            return Constants.REDIRECT_TO_GET_COLLECTION;
        }

        Collections collections = new Collections();
        collections.setCollectionName(collectionName);
        collections.setId(UUID.randomUUID().toString());
        collections.setUserEmail(email);
        collections = collectionRepo.save(collections);

        return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, collections.getId());
    }

    public String getAPI(String id, Model model, HttpSession session) {
        Collections collections = collectionRepo.findById(id).orElse(null);
        if (collections == null) {
            session.setAttribute(ERROR_KEY, "Collection Not Present");
            return Constants.REDIRECT_TO_GET_COLLECTION;
        }
        String error = (String) session.getAttribute(ERROR_KEY);
        if (!StringUtils.isEmpty(error)) {
            model.addAttribute(ERROR_KEY, error);
            session.removeAttribute(ERROR_KEY);
        }
        model.addAttribute(Constants.COLLECTION_ID, collections.getId());
        model.addAttribute(Constants.MOCK_APIS, formEditApiRequest(collections.getApis()));
        model.addAttribute("createApiRequest", new ApiRequest());
        return "api";
    }

    public String createApi(String id, ApiRequest createApiRequest, HttpSession session) {
        Collections collections = collectionRepo.findById(id).orElse(null);
        if (collections == null) {
            session.setAttribute(ERROR_KEY, "Collection Not Present");
            return Constants.REDIRECT_TO_GET_COLLECTION;
        }

        validateCreateApiReq(createApiRequest, session);
        if (session.getAttribute(ERROR_KEY) != null)
            return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);

        String endPoint = createApiRequest.getEndPoint();
        endPoint = endPoint.startsWith("/") ? endPoint : "/" + endPoint;

        Apis apis = new Apis();
        apis.setId(UUID.randomUUID().toString());
        apis.setEndPoint(endPoint);
        apis.setMethod(createApiRequest.getMethod());
        apis.setResponseHeaders(createApiRequest.getResponseHeaders());
        apis.setResponseBody(createApiRequest.getResponseBody());
        apis.setResponseHttpStatus(createApiRequest.getHttpStatus());
        apis.setCollection(collections);

        apiRepo.save(apis);
        return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);
    }

    public String getUpdateApiPage(String id, String apiId, String email, Model model, HttpSession session) {
        Collections collections =  collectionRepo.findById(id).orElse(null);
        if (collections == null
                || !email.equals(collections.getUserEmail())) {
            session.setAttribute(ERROR_KEY,
                    "Collection not Present or You don't have permission to edit");
            return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);
        }

        Apis apis = apiRepo.findById(apiId).orElse(null);
        if (apis == null) {
            session.setAttribute(ERROR_KEY, "Api not found");
            return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);
        }

        String error = (String) session.getAttribute(ERROR_KEY);
        if (!StringUtils.isEmpty(error)) {
            model.addAttribute(ERROR_KEY, error);
            session.removeAttribute(ERROR_KEY);
        }

        model.addAttribute("updateApiRequest", modelToPojo(apis));
        model.addAttribute(Constants.COLLECTION_ID, id);
        return "edit-api-form";
    }

    public String updateApi(String id, String apiId, String email,
                            ApiRequest updateApiRequest, HttpSession session) {
        Collections collections = collectionRepo.findById(id).orElse(null);
        if (collections == null
                || !email.equals(collections.getUserEmail())) {
            session.setAttribute(ERROR_KEY,
                    "Collection not Present or You don't have permission to edit");
            return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);
        }

        validateCreateApiReq(updateApiRequest, session);
        if (session.getAttribute(ERROR_KEY) != null)
            return String.format(Constants.REDIRECT_TO_UPDATE_API_PAGE, id, apiId);

        Apis apis = apiRepo.findById(apiId).orElse(null);
        if (apis == null) {
            session.setAttribute(ERROR_KEY, "Api not found");
            return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);
        }

        String endPoint = updateApiRequest.getEndPoint();
        endPoint = endPoint.startsWith("/") ? endPoint : "/" + endPoint;

        apis.setEndPoint(endPoint);
        apis.setMethod(updateApiRequest.getMethod());
        apis.setResponseHeaders(updateApiRequest.getResponseHeaders());
        apis.setResponseBody(updateApiRequest.getResponseBody());
        apis.setResponseHttpStatus(updateApiRequest.getHttpStatus());

        apiRepo.save(apis);

        return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);
    }

    public String deleteCollection(String email, String id) {
        Collections collections = collectionRepo.findById(id).orElse(null);
        if (collections != null
                && email.equals(collections.getUserEmail())) {
            collectionRepo.delete(collections);
        }

        return Constants.REDIRECT_TO_GET_COLLECTION;
    }

    public String deleteApi(String email, String id, String apiId) {
        Apis apis = apiRepo.findById(apiId).orElse(null);
        if (apis == null)
            return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);

        Collections collections = apis.getCollection();
        if (collections.getUserEmail().equals(email))
            apiRepo.delete(apis);
        return String.format(Constants.REDIRECT_TO_GET_COLLECTION_API, id);
    }


    /*Helper Functions*/
    private List<ApiRequest> formEditApiRequest(List<Apis> apis) {
        if (apis == null || apis.isEmpty())
            return new ArrayList<>();

        List<ApiRequest> apiRequests = new ArrayList<>();
        for (Apis api: apis) {
            apiRequests.add(modelToPojo(api));
        }
        return apiRequests;
    }

    private ApiRequest modelToPojo(Apis api) {
        return new ApiRequest(api.getId(), api.getEndPoint(), api.getMethod()
                , api.getResponseHttpStatus(), api.getResponseHeaders(), api.getResponseBody());
    }

    private void validateCreateApiReq(ApiRequest createApiRequest, HttpSession session) {

        String endPoint = createApiRequest.getEndPoint();
        String method = createApiRequest.getMethod();
        String headers = createApiRequest.getResponseHeaders();
        int statusCode = createApiRequest.getHttpStatus();

        if (StringUtils.isEmpty(endPoint) || StringUtils.isEmpty(method)
                || StringUtils.isEmpty(headers) || statusCode == 0) {
            session.setAttribute(ERROR_KEY, "Mandatory values missing");
            return;
        }

        if (!Pattern.matches(Constants.END_POINT_REGEX, endPoint)) {
            session.setAttribute(ERROR_KEY, "Endpoint is not in proper format");
            return;
        }

        if (!Constants.METHODS.contains(method)) {
            session.setAttribute(ERROR_KEY,
                    "Invalid HTTP method. Method should be GET, POST, PUT, DELETE");
            return;
        }


        if (statusCode <= 100 || statusCode >= 599) {
            session.setAttribute(ERROR_KEY, "Invalid Http Status code");
            return;
        }

        if (isNotValidJson(headers)) {
            session.setAttribute(ERROR_KEY, "Response header should be JSON format");
        }
    }

    private boolean isNotValidJson(String s) {
        try {
            new JSONObject(s);
        } catch (JSONException e) {
            return true;
        }
        return false;
    }

}
