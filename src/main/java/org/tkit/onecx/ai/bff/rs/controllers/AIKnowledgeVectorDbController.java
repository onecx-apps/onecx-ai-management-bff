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
import org.tkit.onecx.ai.bff.rs.mappers.AIKnowledgeVectorDbMapper;
import org.tkit.onecx.ai.bff.rs.mappers.ExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.ai.management.bff.client.api.AiKnowledgeVectorDbInternalApi;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.AiKnowledgeVectorDbBffServiceApiService;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class AIKnowledgeVectorDbController implements AiKnowledgeVectorDbBffServiceApiService {
    @Inject
    @RestClient
    AiKnowledgeVectorDbInternalApi aiKnowledgeVectorDbInternalApi;

    @Inject
    AIKnowledgeVectorDbMapper aiKnowledgeVectorDbMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createAIKnowledgeVectorDb(String id,
            CreateAIKnowledgeVectorDbRequestDTO createAIKnowledgeVectorDbRequestDTO) {
        return null;
    }

    @Override
    public Response deleteAIKnowledgeVectorDb(String id) {
        try (Response response = aiKnowledgeVectorDbInternalApi.deleteKnowledgeVectorDb(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAIKnowledgeVectorDbById(String id) {
        //        try (Response response = aiKnowledgeVectorDbInternalApi.getAIKnowledgeVectorDb(id)) {
        //            AIKnowledgeVectorDb aiKnowledgeVectorDb = response.readEntity(AIKnowledgeVectorDb.class);
        //            AIKnowledgeVectorDbDTO aiKnowledgeVectorDbDTO = aiKnowledgeVectorDbMapper.map(aiKnowledgeVectorDb);
        //            return Response.status(response.getStatus()).entity(aiKnowledgeVectorDbDTO).build();
        //        }
        return null;
    }

    @Override
    public Response searchAIKnowledgeVectorDbs(SearchAIKnowledgeVectorDbRequestDTO searchAIKnowledgeVectorDbRequestDTO) {
        return null;
    }

    @Override
    public Response updateAIKnowledgeVectorDb(String id,
            UpdateAIKnowledgeVectorDbRequestDTO updateAIKnowledgeVectorDbRequestDTO) {
        //        UpdateAIKnowledgeVectorDbRequest updateAIKnowledgeVectorDbRequest = aiKnowledgeVectorDbMapper
        //            .mapUpdate(updateAIKnowledgeVectorDbRequestDTO);
        //        try (Response updateResponse = aiKnowledgeVectorDbInternalApi.updateKnowledgeVectorDb(id,
        //            updateAIKnowledgeVectorDbRequest)) {
        //            return Response.status(updateResponse.getStatus()).entity(updateAIKnowledgeVectorDbRequestDTO).build();
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
