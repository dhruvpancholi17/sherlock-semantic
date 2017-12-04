package com.flipkart.sherlock.semantic.norm.resources;

import com.codahale.metrics.annotation.Timed;
import com.flipkart.sherlock.semantic.norm.core.INormalise;
import com.flipkart.sherlock.semantic.norm.core.IndividualTokenNormalisationService;
import com.flipkart.sherlock.semantic.norm.core.NormalisationAlgoFactory;
import com.flipkart.sherlock.semantic.norm.entity.api.NormQueryReq;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Created by anurag.laddha on 28/11/17.
 */

/**
 * Normalisation REST resources
 */
@Path("/norm")
@Produces(MediaType.APPLICATION_JSON)
public class NormResource {

    @Inject
    private NormalisationAlgoFactory normalisationAlgoFactory;

    @Timed
    @POST
    @Path("/string")
    public List<String> normQueries(NormQueryReq queryReq, @Context UriInfo uriInfo){
        return this.normalisationAlgoFactory.getNormaliseImpl(null).normalise(queryReq.getData(),
            ImmutableMap.of(INormalise.Context.Store, queryReq.getStore(),
                INormalise.Context.MarketplaceId, queryReq.getMarketPlaceId()));
    }
}
