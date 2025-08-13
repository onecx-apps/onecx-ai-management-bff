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

    @Mapping(target = "provider", source = "llmProvider")
    @Mapping(target = "aiKnowledgeBase", ignore = true) // doesn't exist in SVC
    @Mapping(target = "aIKnowledgeVectorDb", source = "vectorDb")
    @Mapping(target = "aIKnowledgeUrl", source = "urls")
    @Mapping(target = "aIKnowledgeDbs", source = "dbs")
    @Mapping(target = "aIKnowledgeDocuments", source = "documents")
    @Mapping(target = "removeAIKnowledgeUrlItem", ignore = true)
    @Mapping(target = "removeAIKnowledgeDbsItem", ignore = true)
    @Mapping(target = "removeAIKnowledgeDocumentsItem", ignore = true)
    AIContextDTO map(AIContext aiContext);

    @Mapping(target = "llmProvider", source = "provider")
    @Mapping(target = "vectorDb", source = "aIKnowledgeVectorDb")
    @Mapping(target = "urls", source = "aIKnowledgeUrl")
    @Mapping(target = "dbs", source = "aIKnowledgeDbs")
    @Mapping(target = "documents", source = "aIKnowledgeDocuments")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "llmSystemMessage", ignore = true)
    AIContext map(AIContextDTO aiContextDTO);

    CreateAIContextRequest mapCreate(CreateAIContextRequestDTO createAIContextRequestDTO);
    //
    //    UpdateAIContextRequest mapUpdate(UpdateAIContextRequestDTO updateAIContextRequestDTO);
    //
    //    AIContextSearchCriteria mapSearch(SearchAIContextRequestDTO searchAIContextRequestDTO);
    //
    //    SearchAIContextResponseDTO mapSearchPageResult(AIContextPageResult aiContextPageResult);
}
