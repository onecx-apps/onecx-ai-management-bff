package org.tkit.onecx.ai.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.onecx.ai.bff.rs.mappers.AIKnowledgeDocumentMapper;
import org.tkit.onecx.ai.bff.rs.mappers.ExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.ai.bff.rs.internal.AiKnowledgeDocumentBffServiceApiService;
import gen.org.tkit.onecx.ai.bff.rs.internal.model.*;
import gen.org.tkit.onecx.ai.mgmt.client.api.AiKnowledgeDocumentInternalApi;
import gen.org.tkit.onecx.ai.mgmt.client.model.AIKnowledgeDocument;
import gen.org.tkit.onecx.ai.mgmt.client.model.UpdateAIKnowledgeDocumentRequest;
import gen.org.tkit.onecx.permission.model.ProblemDetailResponse;

/**
 * @author tchanad
 * @project onecx-ai-management-bff
 */
@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class AIKnowledgeDocumentController implements AiKnowledgeDocumentBffServiceApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AIKnowledgeDocumentController.class);

    @Inject
    @RestClient
    AiKnowledgeDocumentInternalApi aiKnowledgeDocumentApi;

    @Inject
    AIKnowledgeDocumentMapper documentMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createAIKnowledgeDocument(CreateAIKnowledgeDocumentDTO createAIKnowledgeDocumentDTO) {
        return null;
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
            AIKnowledgeDocumentDTO aiKnowledgeDocumentDTO = documentMapper.mapById(aiKnowledgeDocument);
            return Response.status(response.getStatus()).entity(aiKnowledgeDocumentDTO).build();
        }
    }

    @Override
    public Response searchAIKnowledgeDocuments(AIKnowledgeDocumentSearchRequestDTO aiKnowledgeDocumentSearchRequestDTO) {
        return null;
    }

    @Override
    public Response updateAIKnowledgeDocument(String id, UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO) {
        try {
            // Map dto to request
            UpdateAIKnowledgeDocumentRequest updateAIKnowledgeDocumentRequest = documentMapper
                    .mapUpdate(updateAIKnowledgeDocumentDTO);
            try (Response updateResponse = aiKnowledgeDocumentApi.updateKnowledgeDocument(id,
                    updateAIKnowledgeDocumentRequest)) {
                return Response.status(updateResponse.getStatus()).build();
            }
        } catch (WebApplicationException ex) {
            return Response.status(ex.getResponse().getStatus())
                    .entity(documentMapper.mapErrorDetails(ex.getResponse().readEntity(ProblemDetailResponse.class))).build();
        }
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }

}
