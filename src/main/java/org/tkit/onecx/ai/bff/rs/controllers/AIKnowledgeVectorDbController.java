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
import gen.org.tkit.onecx.ai.management.bff.client.model.AIKnowledgeVectorDb;
import gen.org.tkit.onecx.ai.management.bff.client.model.CreateAIKnowledgeVectorDbRequest;
import gen.org.tkit.onecx.ai.management.bff.client.model.UpdateAIKnowledgeVectorDbRequest;
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
    public Response createAIKnowledgeVectorDb(CreateAIKnowledgeVectorDbRequestDTO createAIKnowledgeVectorDbRequestDTO) {
        CreateAIKnowledgeVectorDbRequest createAIKnowledgeVectorDbRequest = aiKnowledgeVectorDbMapper
                .mapCreate(createAIKnowledgeVectorDbRequestDTO);
        //  this functionality doesn't work:
        // 	needs endpoint to create AIKnowledgeVectorDb directly and not under ai-context
        try (Response createResponse = aiKnowledgeVectorDbInternalApi.createKnowledgeVectorDb("",
                createAIKnowledgeVectorDbRequest)) {
            var createAiKnowledgeVectorDb = createResponse.readEntity(AIKnowledgeVectorDb.class);

            return Response.status(createResponse.getStatus()).entity(aiKnowledgeVectorDbMapper.map(createAiKnowledgeVectorDb))
                    .build();
        }
    }

    @Override
    public Response deleteAIKnowledgeVectorDb(String id) {
        try (Response response = aiKnowledgeVectorDbInternalApi.deleteKnowledgeVectorDb(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAIKnowledgeVectorDbById(String id) {
        try (Response response = aiKnowledgeVectorDbInternalApi.getAIKnowledgeVectorDb(id)) {
            AIKnowledgeVectorDb aiKnowledgeVectorDb = response.readEntity(AIKnowledgeVectorDb.class);
            AIKnowledgeVectorDbDTO aiKnowledgeVectorDbDTO = aiKnowledgeVectorDbMapper.map(aiKnowledgeVectorDb);
            return Response.status(response.getStatus()).entity(aiKnowledgeVectorDbDTO).build();
        }
    }

    @Override
    public Response searchAIKnowledgeVectorDbs(SearchAIKnowledgeVectorDbRequestDTO searchAIKnowledgeVectorDbRequestDTO) {
        // this functionality doesn't work:
        // svc doesn't have search functionality for AIKnowledgeVectorDbs
        return null;
    }

    @Override
    public Response updateAIKnowledgeVectorDb(String id,
            UpdateAIKnowledgeVectorDbRequestDTO updateAIKnowledgeVectorDbRequestDTO) {
        UpdateAIKnowledgeVectorDbRequest updateAIKnowledgeVectorDbRequest = aiKnowledgeVectorDbMapper
                .mapUpdate(updateAIKnowledgeVectorDbRequestDTO);
        try (Response updateResponse = aiKnowledgeVectorDbInternalApi.updateKnowledgeVectorDb(id,
                updateAIKnowledgeVectorDbRequest)) {
            return Response.status(updateResponse.getStatus()).entity(updateAIKnowledgeVectorDbRequestDTO).build();
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
