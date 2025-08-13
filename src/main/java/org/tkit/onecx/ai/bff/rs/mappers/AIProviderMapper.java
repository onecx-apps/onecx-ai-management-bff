package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;

import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTime.class })
public interface AIProviderMapper {
    AIProviderDTO map(AIProvider aiProvider);

    CreateAIProviderRequest mapCreate(CreateAIProviderDTO createAIProviderDTO);

    UpdateAIProviderRequest mapUpdate(UpdateAIProviderDTO updateAIKnowledgeBaseRequestDTO);

    AIProviderSearchRequestDTO mapSearch(AIProviderSearchRequestDTO searchAIKnowledgeBaseRequestDTO);

    SearchAIKnowledgeBaseResponseDTO mapSearchPageResult(AIKnowledgeBasePageResult aiKnowledgeBasePageResult);

}
