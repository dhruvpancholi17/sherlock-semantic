package com.flipkart.sherlock.semantic.autosuggest.views;

import com.flipkart.sherlock.semantic.autosuggest.dao.AutoSuggestCacheRefresher;
import com.flipkart.sherlock.semantic.autosuggest.dataGovernance.Ingester;
import com.flipkart.sherlock.semantic.autosuggest.flow.*;
import com.flipkart.sherlock.semantic.autosuggest.models.*;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4FlashAutoSuggestResponse;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.request.V4FlashPrivateAutoSuggestRequest;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.metrics.MetricsManager;
import com.flipkart.sherlock.semantic.commons.ab.ABServiceHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Map;
import java.util.UUID;

import static com.flipkart.sherlock.semantic.common.metrics.MetricsManager.Service.Autosuggest;

/**
 * Created by dhruv.pancholi on 30/05/17.
 */
@Slf4j
@Path("/")
@Singleton
public class AutoSuggestView {

    public static final String COSMOS_AUTO_SUGGEST_COMPONENT = "api_autosuggest";

    public static final String COSMOS_AUTO_SUGGEST_V4_COMPONENT = "api_autosuggest_v4";

    @Inject
    private JsonSeDe jsonSeDe;

    @Inject
    private AutoSuggestCacheRefresher autoSuggestCacheRefresher;

    @Inject
    private QueryRequestHandler queryRequestHandler;

    @Inject
    private ProductRequestHandler productRequestHandler;

    @Inject
    private ParamsHandler paramsHandler;

    @Inject
    private V4ParamsHandler v4ParamsHandler;

    @Inject
    private V4RequestHandler v4RequestHandler;

    @Inject
    private ABServiceHandler abServiceHandler;


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String serverRunning() {
        return "Server is up and running!";
    }


    @GET
    @Path("/sherlock/stores/{store : .+}/autosuggest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pathMethod(@PathParam("store") String store, @Context UriInfo uriInfo, @Context HttpHeaders headers) {

        MetricsManager.Service service = Autosuggest;
        String component = COSMOS_AUTO_SUGGEST_COMPONENT;

        MetricsManager.logRequests(service, component);

        try {
            Response response = MetricsManager.logTime(service, component, () -> {

                final String payloadId = UUID.randomUUID().toString();

                Params params = paramsHandler.getParams(store, uriInfo);

                QueryResponse queryResponse = queryRequestHandler
                        .getQuerySuggestions(params.getQuery(), new QueryRequest(params, null));

                ProductResponse productResponse = productRequestHandler
                        .getProductSuggestions(params.getQuery(),
                                new ProductRequest(params, queryResponse.getAutoSuggestSolrResponse()));

                try {
                    new Ingester().publishData(queryResponse, params, productResponse, headers, uriInfo);
                } catch (Exception e) {
                    log.error("AutoSuggest DG Ingestion Error", e);
                }

                AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse(
                        payloadId,
                        queryResponse.getQuerySuggestions(),
                        productResponse.getProductSuggestions(),
                        params.isDebug() ? params : null,
                        params.isDebug() ? queryResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                        params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getSolrQuery() : null,
                        params.isDebug() ? productResponse.getAutoSuggestSolrResponse().getAutoSuggestDocs() : null);

                if (autoSuggestResponse.getQuerySuggestions() == null || autoSuggestResponse.getQuerySuggestions().isEmpty()) {
                    log.info("Empty response for query: {}", params.getQuery());
                    MetricsManager.logNullResponse(Autosuggest, COSMOS_AUTO_SUGGEST_COMPONENT);
                }


                return Response.status(Response.Status.OK)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .entity(jsonSeDe.writeValueAsString(autoSuggestResponse))
                        .build();
            });
            MetricsManager.logSuccess(service, component);
            return response;
        } catch (Exception e) {
            MetricsManager.logError(service, component, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/sherlock/v4/stores/{store : .+}/autosuggest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response autoSuggestV4(@PathParam("store") String store, @Context UriInfo uriInfo) {
        MetricsManager.Service service = Autosuggest;
        String component = COSMOS_AUTO_SUGGEST_V4_COMPONENT;

        MetricsManager.logRequests(service, component);

        try {
            Response response = MetricsManager.logTime(service, component, () -> {

                Params params = paramsHandler.getParams(store, uriInfo);

                V4FlashAutoSuggestResponse v4Response = v4RequestHandler.getV4Response(params);

                if (v4Response.getSuggestions() == null || v4Response.getSuggestions().isEmpty()) {
                    log.info("Empty response for query: {}", jsonSeDe.writeValueAsString(uriInfo.getQueryParameters()));
                    MetricsManager.logNullResponse(Autosuggest, COSMOS_AUTO_SUGGEST_V4_COMPONENT);
                }

                return Response.status(Response.Status.OK)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .entity(jsonSeDe.writeValueAsString(v4Response))
                        .build();
            });
            MetricsManager.logSuccess(service, component);
            return response;
        } catch (Exception e) {
            MetricsManager.logError(service, component, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/sherlock/v4/autosuggest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getV4AutoSuggest(String postBody, @Context HttpHeaders headers) {

        MetricsManager.Service service = Autosuggest;
        String component = COSMOS_AUTO_SUGGEST_V4_COMPONENT;

        MetricsManager.logRequests(service, component);

        try {
            Response response = MetricsManager.logTime(service, component, () -> {

                V4FlashPrivateAutoSuggestRequest v4FlashPrivateAutoSuggestRequest = jsonSeDe.readValue(postBody, V4FlashPrivateAutoSuggestRequest.class);

                Params params = v4ParamsHandler.getParams(v4FlashPrivateAutoSuggestRequest);

                V4FlashAutoSuggestResponse v4Response = v4RequestHandler.getV4Response(params);

                if (v4Response.getSuggestions() == null || v4Response.getSuggestions().isEmpty()) {
                    log.info("Empty response for query: {}", jsonSeDe.writeValueAsString(null));
                    MetricsManager.logNullResponse(Autosuggest, COSMOS_AUTO_SUGGEST_V4_COMPONENT);
                }

                return Response.status(Response.Status.OK)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .entity(jsonSeDe.writeValueAsString(v4Response))
                        .build();
            });
            MetricsManager.logSuccess(service, component);
            return response;
        } catch (Exception e) {
            MetricsManager.logError(service, component, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("sherlock/cacherefresh")
    @Produces(MediaType.APPLICATION_JSON)
    public String cacheRefresh(@QueryParam("dao") String dao) {
        Map<String, Integer> cachedMaps = autoSuggestCacheRefresher.refreshCache(dao);
        return jsonSeDe.writeValueAsString(cachedMaps);
    }

    @GET
    @Path("sherlock/cacheget")
    @Produces(MediaType.APPLICATION_JSON)
    public String cacheGet(@QueryParam("dao") String dao, @QueryParam("keys") String keys) {
        return jsonSeDe.writeValueAsString(autoSuggestCacheRefresher.cacheGet(dao, keys));
    }
}
