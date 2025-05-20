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
import org.tkit.onecx.ai.bff.rs.mappers.AIKnowledgeDocumentMapper;
import org.tkit.onecx.ai.bff.rs.mappers.ExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.ai.management.bff.client.api.AiKnowledgeDocumentInternalApi;
import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.AiKnowledgeDocumentBffServiceApiService;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class AIKnowledgeDocumentController implements AiKnowledgeDocumentBffServiceApiService {
    @Inject
    @RestClient
    AiKnowledgeDocumentInternalApi aiKnowledgeDocumentApi;

    @Inject
    AIKnowledgeDocumentMapper documentMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createAIKnowledgeDocument(CreateAIKnowledgeDocumentDTO createAIKnowledgeDocumentDTO, String id) {
        // Map dto to request
        CreateAIKnowledgeDocumentRequest createAIKnowledgeDocumentRequest = documentMapper
                .mapCreate(createAIKnowledgeDocumentDTO);
        // then send the creation request
        try (Response createResponse = aiKnowledgeDocumentApi.createKnowledgeDocument(id,
                createAIKnowledgeDocumentRequest)) {
            var createDocument = createResponse.readEntity(AIKnowledgeDocument.class);

            return Response.status(createResponse.getStatus()).entity(documentMapper.map(createDocument)).build();
        }
    }

    @Override
    public Response deleteAIKnowledgeDocument(String id) {
        try (Response response = aiKnowledgeDocumentApi.deleteKnowledgeDocument(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAIKnowledgeDocumentById(String id) {
        try (Response response = aiKnowledgeDocumentApi.getAIKnowledgeDocument(id)) {
            AIKnowledgeDocument aiKnowledgeDocument = response.readEntity(AIKnowledgeDocument.class);
            AIKnowledgeDocumentDTO aiKnowledgeDocumentDTO = documentMapper.map(aiKnowledgeDocument);
            return Response.status(response.getStatus()).entity(aiKnowledgeDocumentDTO).build();
        }
    }

    @Override
    public Response searchAIKnowledgeDocuments(AIKnowledgeDocumentSearchCriteriaDTO aiKnowledgeDocumentSearchCriteriaDTO) {

        AIKnowledgeDocumentSearchCriteria searchCriteria = documentMapper.mapSearch(aiKnowledgeDocumentSearchCriteriaDTO);
        try (Response searchResponse = aiKnowledgeDocumentApi.searchAIKnowledgeDocuments(searchCriteria)) {
            AIKnowledgeDocumentPageResult documentPageResult = searchResponse
                    .readEntity(AIKnowledgeDocumentPageResult.class);
            AIKnowledgeDocumentSearchPageResultDTO pageResultDTO = documentMapper.mapSearchPageResult(documentPageResult);
            return Response.status(searchResponse.getStatus()).entity(pageResultDTO).build();
        }

    }

    @Override
    public Response updateAIKnowledgeDocument(String id, UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO) {

        //  Map dto to request
        UpdateAIKnowledgeDocumentRequest updateAIKnowledgeDocumentRequest = documentMapper
                .mapUpdate(updateAIKnowledgeDocumentDTO);
        // and update AIKnowledge Document
        try (Response updateResponse = aiKnowledgeDocumentApi.updateKnowledgeDocument(id,
                updateAIKnowledgeDocumentRequest)) {
            return Response.status(updateResponse.getStatus()).entity(updateAIKnowledgeDocumentDTO).build();
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
