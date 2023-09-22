package com.us.mocker.controller;

import com.us.mocker.misc.Constants;
import com.us.mocker.requestresponse.ApiRequest;
import com.us.mocker.security.GoogleOauthUser;
import com.us.mocker.service.MockerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MockerController {

    @Autowired
    MockerService mockerService;

    @GetMapping("/")
    public String indexPage(Model model) {
        if ("anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            return "index";
        return Constants.REDIRECT_TO_GET_COLLECTION;
    }

    /*Collection Endpoints*/
    @GetMapping("/collection")
    public String getCollectionByEmail(@ModelAttribute("error") String error,
                                       @AuthenticationPrincipal GoogleOauthUser oauthUser,
                                       @RequestParam(name = "c", defaultValue = "") String name,
                                       Model model, HttpSession session, HttpServletRequest request) {
        return mockerService.getCollectionByEmail(request, session, oauthUser.getEmail(), model, name);
    }

    @PostMapping("/collection")
    public String createCollection(@AuthenticationPrincipal GoogleOauthUser oauthUser,
                                   @RequestParam("collectionName") String collectionName, HttpSession session) {
        return mockerService.createCollection(collectionName, session, oauthUser.getEmail());
    }

    @DeleteMapping("/collection/{id}")
    public String deleteCollection(@AuthenticationPrincipal GoogleOauthUser oauthUser,
                                   @PathVariable("id") String id) {
        return mockerService.deleteCollection(oauthUser.getEmail(), id);
    }

    /*API Endpoints*/
    @GetMapping("/collection/{id}/api")
    public String getApi(@PathVariable("id") String id, Model model, HttpSession session, HttpServletRequest request) {
        return mockerService.getAPI(id, model, session, request);
    }

    @PostMapping("/collection/{id}/api")
    public String createApi(@PathVariable("id") String id,
                            @ModelAttribute ApiRequest createApiRequest, HttpSession session) {
        return mockerService.createApi(id, createApiRequest, session);
    }

    @GetMapping("/collection/{id}/api/{apiId}")
    public String getUpdateApiPage(@AuthenticationPrincipal GoogleOauthUser oauthUser,
                                   @PathVariable("id") String id, @PathVariable("apiId") String apiId,
                                   Model model, HttpSession session) {
        return mockerService.getUpdateApiPage(id, apiId, oauthUser.getEmail(), model, session);
    }

    @PostMapping("/collection/{id}/api/{apiId}")
    public String updateApi(@AuthenticationPrincipal GoogleOauthUser oauthUser,
                            @PathVariable("id") String id, @PathVariable("apiId") String apiId,
                            @ModelAttribute ApiRequest updateApiRequest, HttpSession session) {
        return mockerService.updateApi(id, apiId, oauthUser.getEmail(), updateApiRequest, session);
    }

    @DeleteMapping("/collection/{id}/api/{apiId}")
    public String deleteApi(@AuthenticationPrincipal GoogleOauthUser oauthUser,
                            @PathVariable("id") String id, @PathVariable("apiId") String apiId) {
        return mockerService.deleteApi(oauthUser.getEmail(), id, apiId);
    }
}
