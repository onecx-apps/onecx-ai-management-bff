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
import org.tkit.onecx.ai.bff.rs.mappers.AIKnowledgeBaseMapper;
import org.tkit.onecx.ai.bff.rs.mappers.ExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.ai.management.bff.client.api.AiKnowledgeBaseInternalApi;
import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.AiKnowledgeBaseApiService;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class AIKnowledgeBaseController implements AiKnowledgeBaseApiService {
    @Inject
    @RestClient
    AiKnowledgeBaseInternalApi aiKnowledgeBaseInternalApi;

    @Inject
    AIKnowledgeBaseMapper aiKnowledgeBaseMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createAIKnowledgeBase(CreateAIKnowledgeBaseRequestDTO createAIKnowledgeBaseRequestDTO) {
        CreateAIKnowledgeBaseRequest createAIKnowledgeBaseRequest = aiKnowledgeBaseMapper
                .mapCreate(createAIKnowledgeBaseRequestDTO);
        try (Response createResponse = aiKnowledgeBaseInternalApi.createAIKnowledgeBase(createAIKnowledgeBaseRequest)) {
            var createAiKnowledgeBase = createResponse.readEntity(AIKnowledgeBase.class);

            return Response.status(createResponse.getStatus()).entity(aiKnowledgeBaseMapper.map(createAiKnowledgeBase)).build();
        }
    }

    @Override
    public Response deleteAiKnowledgeBase(String id) {
        try (Response response = aiKnowledgeBaseInternalApi.deleteAIKnowledgeBase(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAIKnowledgeBaseById(String id) {
        try (Response response = aiKnowledgeBaseInternalApi.getAIKnowledgeBase(id)) {
            AIKnowledgeBase aiKnowledgeBase = response.readEntity(AIKnowledgeBase.class);
            AIKnowledgeBaseDTO aiKnowledgeBaseDTO = aiKnowledgeBaseMapper.map(aiKnowledgeBase);
            return Response.status(response.getStatus()).entity(aiKnowledgeBaseDTO).build();
        }
    }

    @Override
    public Response searchAIKnowledgeBases(SearchAIKnowledgeBaseRequestDTO searchAIKnowledgeBaseRequestDTO) {
        AIKnowledgeBaseSearchCriteria searchCriteria = aiKnowledgeBaseMapper.mapSearch(searchAIKnowledgeBaseRequestDTO);
        try (Response searchResponse = aiKnowledgeBaseInternalApi.findAIKnowlegeBaseBySearchCriteria(searchCriteria)) {
            AIKnowledgeBasePageResult aiKnowledgeBasePageResult = searchResponse
                    .readEntity(AIKnowledgeBasePageResult.class);
            SearchAIKnowledgeBaseResponseDTO pageResultDTO = aiKnowledgeBaseMapper
                    .mapSearchPageResult(aiKnowledgeBasePageResult);
            return Response.status(searchResponse.getStatus()).entity(pageResultDTO).build();
        }
    }

    @Override
    public Response updateAiKnowledgeBase(String id, UpdateAIKnowledgeBaseRequestDTO updateAIKnowledgeBaseRequestDTO) {
        UpdateAIKnowledgeBaseRequest updateAIKnowledgeBaseRequest = aiKnowledgeBaseMapper
                .mapUpdate(updateAIKnowledgeBaseRequestDTO);
        try (Response updateResponse = aiKnowledgeBaseInternalApi.updateAIKnowledgeBase(id,
                updateAIKnowledgeBaseRequest)) {
            return Response.status(updateResponse.getStatus()).entity(updateAIKnowledgeBaseRequestDTO).build();
        }
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
