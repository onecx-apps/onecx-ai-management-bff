package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@Mapper(uses = {
        OffsetDateTime.class,
        AIKnowledgeVectorDbMapper.class,
        AIProviderMapper.class, AIKnowledgeUrlMapper.class,
        AIKnowledgeDatabaseMapper.class,
        AIKnowledgeDocumentMapper.class
})
public interface AIContextMapper {

    @Mapping(target = "description", ignore = true)
    @Mapping(target = "appId", ignore = true)
    @Mapping(target = "provider", source = "llmProvider")
    @Mapping(target = "aiKnowledgeBase", ignore = true) // doesn't exist in SVC
    @Mapping(target = "aiKnowledgeVectorDb", source = "vectorDb")
    @Mapping(target = "aiKnowledgeUrl", source = "urls")
    @Mapping(target = "aiKnowledgeDbs", source = "dbs")
    @Mapping(target = "aiKnowledgeDocuments", source = "documents")
    @Mapping(target = "removeAiKnowledgeUrlItem", ignore = true)
    @Mapping(target = "removeAiKnowledgeDocumentsItem", ignore = true)
    @Mapping(target = "removeAiKnowledgeDbsItem", ignore = true)
    AIContextDTO map(AIContext aiContext);

    @Mapping(target = "llmProvider", source = "provider")
    @Mapping(target = "vectorDb", source = "aiKnowledgeVectorDb")
    @Mapping(target = "urls", source = "aiKnowledgeUrl")
    @Mapping(target = "dbs", source = "aiKnowledgeDbs")
    @Mapping(target = "documents", source = "aiKnowledgeDocuments")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "llmSystemMessage", ignore = true)
    AIContext map(AIContextDTO aiContextDTO);

    @Mapping(target = "description", source = "dataObject.description")
    @Mapping(target = "appId", source = "dataObject.appId")
    @Mapping(target = "name", source = "dataObject.name")
    @Mapping(target = "llmSystemMessage", ignore = true)
    @Mapping(target = "llmProvider", source = "dataObject.provider")
    @Mapping(target = "vectorDb", source = "dataObject.aiKnowledgeVectorDb")
    @Mapping(target = "urls", source = "dataObject.aiKnowledgeUrl")
    @Mapping(target = "dbs", source = "dataObject.aiKnowledgeDbs")
    @Mapping(target = "documents", source = "dataObject.aiKnowledgeDocuments")
    CreateAIContextRequest mapCreate(CreateAIContextRequestDTO createAIContextRequestDTO);

    @Mapping(target = "name", source = "dataObject.name")
    @Mapping(target = "description", source = "dataObject.description")
    @Mapping(target = "appId", source = "dataObject.appId")
    @Mapping(target = "llmSystemMessage", ignore = true)
    @Mapping(target = "llmProvider", source = "dataObject.provider")
    @Mapping(target = "vectorDb", source = "dataObject.aiKnowledgeVectorDb")
    @Mapping(target = "urls", source = "dataObject.aiKnowledgeUrl")
    @Mapping(target = "dbs", source = "dataObject.aiKnowledgeDbs")
    @Mapping(target = "documents", source = "dataObject.aiKnowledgeDocuments")
    UpdateAIContextRequest mapUpdate(UpdateAIContextRequestDTO updateAIContextRequestDTO);

    AIContextSearchCriteria mapSearch(SearchAIContextRequestDTO searchAIContextRequestDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    SearchAIContextResponseDTO mapSearchPageResult(AIContextPageResult aiContextPageResult);
}
