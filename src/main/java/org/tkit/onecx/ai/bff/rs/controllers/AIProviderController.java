package org.tkit.onecx.ai.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.tkit.onecx.ai.bff.rs.mappers.AIProviderMapper;
import org.tkit.onecx.ai.bff.rs.mappers.ExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.ai.management.bff.client.api.AiProviderInternalApi;
import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.AiProviderApiService;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class AIProviderController implements AiProviderApiService {
    @Inject
    @RestClient
    AiProviderInternalApi aiProviderInternalApi;

    @Inject
    AIProviderMapper aiProviderMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createAIProvider(CreateAIProviderDTO createAIProviderDTO) {
        CreateAIProviderRequest createAIProviderRequest = aiProviderMapper.mapCreate(createAIProviderDTO);
        try (Response createResponse = aiProviderInternalApi.createAIProvider(createAIProviderRequest)) {
            var createAiContext = createResponse.readEntity(AIProvider.class);

            return Response.status(createResponse.getStatus()).entity(aiProviderMapper.map(createAiContext)).build();
        }
    }

    @Override
    public Response deleteAIProvider(String id) {
        try (Response response = aiProviderInternalApi.deleteAIProvider(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getAIProviderById(String id) {
        try (Response response = aiProviderInternalApi.getAIProvider(id)) {
            AIProvider aiProvider = response.readEntity(AIProvider.class);
            AIProviderDTO aiProviderDTO = aiProviderMapper.map(aiProvider);
            return Response.status(response.getStatus()).entity(aiProviderDTO).build();
        }
    }

    @Override
    public Response searchAIProvider(AIProviderSearchRequestDTO aiProviderSearchRequestDTO) {
        AIProviderSearchCriteria searchCriteria = aiProviderMapper.mapSearch(aiProviderSearchRequestDTO);
        try (Response searchResponse = aiProviderInternalApi.findAIProviderBySearchCriteria(searchCriteria)) {
            AIProviderPageResult aiProviderPageResult = searchResponse
                    .readEntity(AIProviderPageResult.class);
            AIProviderSearchResponseDTO pageResultDTO = aiProviderMapper
                    .mapSearchPageResult(aiProviderPageResult);
            return Response.status(searchResponse.getStatus()).entity(pageResultDTO).build();
        }
    }

    @Override
    public Response updateAIProvider(String id, UpdateAIProviderDTO updateAIProviderDTO) {
        UpdateAIProviderRequest updateAIContextRequest = aiProviderMapper
                .mapUpdate(updateAIProviderDTO);
        try (Response updateResponse = aiProviderInternalApi.updateAIProvider(id,
                updateAIContextRequest)) {
            return Response.status(updateResponse.getStatus()).entity(updateAIProviderDTO).build();
        }
    }
}
