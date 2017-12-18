package com.flipkart.sherlock.semantic.commons.ab;

import com.flipkart.abservice.models.exceptions.ABInitializationException;
import com.flipkart.abservice.models.exceptions.ABInvalidRequestException;
import com.flipkart.abservice.models.request.ABMultiGetRequest;
import com.flipkart.abservice.models.response.ABVariable;
import com.flipkart.abservice.pojo.UserID;
import com.flipkart.abservice.resources.ABService;
import com.flipkart.abservice.store.impl.ABApiConfigStore;
import com.flipkart.sherlock.semantic.commons.config.FkConfigServiceWrapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by dhruv.pancholi on 18/12/17.
 */
@Slf4j
@Singleton
public class ABServiceHandler {

    private static final String AB_CONFIG_KEY = "abConfig";

    private static final String SERVICE_ENABLED = AB_CONFIG_KEY + ".serviceEnabled";
    private static final String CONTINUE_ON_FAILIRE = AB_CONFIG_KEY + ".continueWithoutABOnFailure";
    private static final String LAYERS_LIST = AB_CONFIG_KEY + ".layersList";
    private static final String PROD_END_POINT = AB_CONFIG_KEY + ".prodEndpoint";
    private static final String CLIENT_ID = AB_CONFIG_KEY + ".clientId";

    private static final Map<String, ABVariable> DEFAULT_AB_PARAMS = ImmutableMap.of();

    private String clientId;

    private boolean serviceEnabled = true;

    private List<String> layersList = null;

    @Inject
    private ABServiceHandler(FkConfigServiceWrapper fkConfigServiceWrapper) {
        Boolean serviceEnabled_ = fkConfigServiceWrapper.getBoolean(SERVICE_ENABLED);
        serviceEnabled = (serviceEnabled_ == null) ? false : serviceEnabled_;

        Boolean continueOnABFailure_ = fkConfigServiceWrapper.getBoolean(CONTINUE_ON_FAILIRE);
        boolean continueOnABFailure = (continueOnABFailure_ == null) ? true : continueOnABFailure_;
        log.info("Continue on AB-service failure: {}", continueOnABFailure);


        String layersList_ = fkConfigServiceWrapper.getString(LAYERS_LIST);
        if (layersList_ == null || layersList_.isEmpty()) {
            log.info("Layers list is null/empty, disabling AB-service");
            serviceEnabled = false;
            return;
        } else {
            layersList = Arrays.asList(layersList_.split(","));
        }

        String prodEndpoint = fkConfigServiceWrapper.getString(PROD_END_POINT);
        if (prodEndpoint == null || prodEndpoint.isEmpty()) {
            log.info("Prod EndPoint is null/empty, disabling AB-service");
            serviceEnabled = false;
            return;
        }

        clientId = fkConfigServiceWrapper.getString(CLIENT_ID);
        if (clientId == null || clientId.isEmpty()) {
            log.info("Client ID is null/empty, disabling AB-service");
            serviceEnabled = false;
            return;
        }

        try {
            ABService.initialize(ABApiConfigStore.initializeForClients(prodEndpoint, Collections.singletonList(clientId)));
        } catch (ABInitializationException e) {
            log.error("AB initialization failure: {}", e);
            serviceEnabled = false;
            if (!continueOnABFailure) {
                throw new RuntimeException("Unable to start AB-service");
            }
        }
    }

    public Map<String, ABVariable> getABParams(String deviceId, String accountId) {
        if (!serviceEnabled) return DEFAULT_AB_PARAMS;

        Map<String, ABVariable> abParams = new HashMap<>();
        ABMultiGetRequest multiGetRequest = new ABMultiGetRequest(new UserID(accountId, deviceId), clientId);
        multiGetRequest.setLayersList(layersList);
        try {
            return ABService.getInstance().multiGetABVariables(multiGetRequest);
        } catch (ABInvalidRequestException e) {
            return DEFAULT_AB_PARAMS;
        }
    }
}
