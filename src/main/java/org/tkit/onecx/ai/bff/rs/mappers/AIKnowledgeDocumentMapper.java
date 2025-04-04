package org.tkit.onecx.ai.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.ai.bff.rs.internal.model.*;
import gen.org.tkit.onecx.ai.mgmt.client.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AIKnowledgeDocumentMapper {

    AIKnowledgeDocumentDTO map(AIKnowledgeDocument aiKnowledgeDocument);

    UpdateAIKnowledgeDocumentRequest mapUpdate(UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO);

    @Mapping(target = "name", source = "aIKnowledgeDocumentData.name")
    @Mapping(target = "status", source = "aIKnowledgeDocumentData.status")
    @Mapping(target = "documentRefId", source = "aIKnowledgeDocumentData.documentRefId")
    CreateAIKnowledgeDocumentRequest mapCreate(CreateAIKnowledgeDocumentDTO createAIKnowledgeDocumentDTO);

    AIKnowledgeDocumentSearchCriteria mapSearch(AIKnowledgeDocumentSearchCriteriaDTO searchCriteriaDTO);

    @Mapping(target = "results", source = "stream")
    @Mapping(target = "removeResultsItem", ignore = true)
    AIKnowledgeDocumentSearchPageResultDTO mapSearchPageResult(AIKnowledgeDocumentPageResult knowledgeDocumentPageResult);
}
