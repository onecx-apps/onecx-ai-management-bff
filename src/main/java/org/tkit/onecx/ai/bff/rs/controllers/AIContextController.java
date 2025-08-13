package org.tkit.onecx.ai.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.ai.bff.rs.mappers.AIContextMapper;
import org.tkit.onecx.ai.bff.rs.mappers.ExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.ai.management.bff.client.api.AiContextInternalApi;
import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.AiContextBffServiceApiService;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class AIContextController implements AiContextBffServiceApiService {
    @Inject
    @RestClient
    AiContextInternalApi aiContextInternalApi;

    @Inject
    AIContextMapper aiContextMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createAIContext(CreateAIContextRequestDTO createAIContextRequestDTO) {
        CreateAIContextRequest createAIContextRequest = aiContextMapper.mapCreate(createAIContextRequestDTO);
        try (Response createResponse = aiContextInternalApi.createAIContext(createAIContextRequest)) {
            var createAiContext = createResponse.readEntity(AIContext.class);

            return Response.status(createResponse.getStatus()).entity(aiContextMapper.map(createAiContext)).build();
        }
        return null;
    }

    @Override
    public Response deleteAIContext(String id) {
        try (Response response = aiContextInternalApi.deleteAIContext(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAIContextById(String id) {
        try (Response response = aiContextInternalApi.getAIContext(id)) {
            AIContext aiContext = response.readEntity(AIContext.class);
            AIContextDTO aiContextDTO = aiContextMapper.map(aiContext);
            return Response.status(response.getStatus()).entity(aiContextDTO).build();
        }
    }

    @Override
    public Response searchAIContexts(SearchAIContextRequestDTO searchAIContextRequestDTO) {
        //        AIContextSearchCriteria searchCriteria = aiContextMapper.mapSearch(searchAIContextRequestDTO);
        //        try (Response searchResponse = aiContextInternalApi.findAIContextBySearchCriteria(searchCriteria)) {
        //            AIContextPageResult aiContextPageResult = searchResponse
        //                    .readEntity(AIContextPageResult.class);
        //            SearchAIContextResponseDTO pageResultDTO = aiContextMapper.mapSearchPageResult(aiContextPageResult);
        //            return Response.status(searchResponse.getStatus()).entity(pageResultDTO).build();
        //        }
        return null;
    }

    @Override
    public Response updateAIContext(String id, UpdateAIContextRequestDTO updateAIContextRequestDTO) {
        //        UpdateAIContextRequest updateAIContextRequest = aiContextMapper
        //                .mapUpdate(updateAIContextRequestDTO);
        //        try (Response updateResponse = aiContextInternalApi.updateAIContext(id,
        //                updateAIContextRequest)) {
        //            return Response.status(updateResponse.getStatus()).entity(updateAIContextRequestDTO).build();
        //        }
        return null;
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraintException(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
