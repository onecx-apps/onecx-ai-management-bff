package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTime.class, AIContextMapper.class })
public interface AIKnowledgeBaseMapper {
    @Mapping(target = "aiContext", source = "contexts")
    @Mapping(target = "removeAiContextItem", ignore = true)
    AIKnowledgeBaseDTO map(AIKnowledgeBase aiContext);

    @Mapping(target = "description", source = "dataObject.description")
    @Mapping(target = "name", source = "dataObject.name")
    @Mapping(target = "appId", source = "dataObject.appId")
    @Mapping(target = "contexts", source = "dataObject.aiContext")
    CreateAIKnowledgeBaseRequest mapCreate(CreateAIKnowledgeBaseRequestDTO createAIKnowledgeBaseRequestDTO);

    @Mapping(target = "description", source = "dataObject.description")
    @Mapping(target = "name", source = "dataObject.name")
    @Mapping(target = "appId", source = "dataObject.appId")
    @Mapping(target = "contexts", source = "dataObject.aiContext")
    UpdateAIKnowledgeBaseRequest mapUpdate(UpdateAIKnowledgeBaseRequestDTO updateAIKnowledgeBaseRequestDTO);

    @Mapping(target = "appId", ignore = true)
    AIKnowledgeBaseSearchCriteria mapSearch(SearchAIKnowledgeBaseRequestDTO searchAIKnowledgeBaseRequestDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    SearchAIKnowledgeBaseResponseDTO mapSearchPageResult(AIKnowledgeBasePageResult aiKnowledgeBasePageResult);

}
