package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTime.class })
public interface AIProviderMapper {
    AIProviderDTO map(AIProvider aiProvider);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    AIProvider map(AIProviderDTO aiProviderDTO);

    @Mapping(target = "name", source = "dataObject.name")
    @Mapping(target = "description", source = "dataObject.description")
    @Mapping(target = "llmUrl", source = "dataObject.llmUrl")
    @Mapping(target = "modelName", source = "dataObject.modelName")
    @Mapping(target = "modelVersion", source = "dataObject.modelVersion")
    @Mapping(target = "apiKey", source = "dataObject.apiKey")
    @Mapping(target = "appId", source = "dataObject.appId")
    CreateAIProviderRequest mapCreate(CreateAIProviderDTO createAIProviderDTO);

    @Mapping(target = "name", source = "dataObject.name")
    @Mapping(target = "description", source = "dataObject.description")
    @Mapping(target = "llmUrl", source = "dataObject.llmUrl")
    @Mapping(target = "modelName", source = "dataObject.modelName")
    @Mapping(target = "modelVersion", source = "dataObject.modelVersion")
    @Mapping(target = "appId", source = "dataObject.appId")
    @Mapping(target = "apiKey", source = "dataObject.apiKey")
    UpdateAIProviderRequest mapUpdate(UpdateAIProviderDTO updateAIKnowledgeBaseRequestDTO);

    @Mapping(target = "apiKey", ignore = true)
    AIProviderSearchCriteria mapSearch(AIProviderSearchRequestDTO searchAIKnowledgeBaseRequestDTO);

    @Mapping(target = "removeResultsItem", ignore = true)
    @Mapping(target = "results", source = "stream")
    @Mapping(target = "totalNumberOfResults", source = "totalElements")
    AIProviderSearchResponseDTO mapSearchPageResult(AIProviderPageResult aiProviderPageResult);
}
