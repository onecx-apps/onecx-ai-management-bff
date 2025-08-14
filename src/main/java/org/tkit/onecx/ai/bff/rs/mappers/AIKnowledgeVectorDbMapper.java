package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTime.class })

public interface AIKnowledgeVectorDbMapper {

    @Mapping(target = "vdb", source = "vdbUrl")
    @Mapping(target = "aiContext", ignore = true)
    AIKnowledgeVectorDbDTO map(AIKnowledgeVectorDb aiKnowledgeVectorDb);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "vdbUrl", source = "vdb")
    @Mapping(target = "pwd", ignore = true)
    AIKnowledgeVectorDb map(AIKnowledgeVectorDbDTO aiKnowledgeVectorDbDTO);

    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "vdbUrl", source = "vdb")
    CreateAIKnowledgeVectorDbRequest mapCreate(CreateAIKnowledgeVectorDbRequestDTO createAIknowledgeVectorDbRequestDTO);

    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "vdbUrl", source = "vdb")
    UpdateAIKnowledgeVectorDbRequest mapUpdate(UpdateAIKnowledgeVectorDbRequestDTO updateAIknowledgeVectorDbRequestDTO);
}
