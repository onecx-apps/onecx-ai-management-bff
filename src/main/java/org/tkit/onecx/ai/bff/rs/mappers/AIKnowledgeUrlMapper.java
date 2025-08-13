package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import gen.org.tkit.onecx.ai.management.bff.client.model.AIKnowledgeUrl;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.AIKnowledgeUrlDTO;

@Mapper(uses = { OffsetDateTime.class })
public interface AIKnowledgeUrlMapper {

    AIKnowledgeUrlDTO map(AIKnowledgeUrl aiKnowledgeUrl);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    AIKnowledgeUrl map(AIKnowledgeUrlDTO aiKnowledgeUrlDTO);

}
