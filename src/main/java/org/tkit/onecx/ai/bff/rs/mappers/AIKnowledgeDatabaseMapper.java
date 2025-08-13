package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.ai.management.bff.client.model.AIKnowledgeDatabase;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.AIKnowledgeDatabaseDTO;

@Mapper(uses = { OffsetDateTime.class })
public interface AIKnowledgeDatabaseMapper {

    @Mapping(target = "removeTablesItem", ignore = true)
    AIKnowledgeDatabaseDTO map(AIKnowledgeDatabase aiKnowledgeDatabase);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    AIKnowledgeDatabase map(AIKnowledgeDatabaseDTO aiKnowledgeDatabaseDTO);
}
