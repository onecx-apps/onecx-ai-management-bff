package org.tkit.onecx.ai.bff.rs.mappers;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;

import gen.org.tkit.onecx.ai.management.bff.client.model.*;
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTime.class })
public interface AIContextMapper {
    AIContextDTO map(AIContext aiContext);

    CreateAIContextRequest mapCreate(CreateAIContextRequestDTO createAIContextRequestDTO);

    UpdateAIContextRequest mapUpdate(UpdateAIContextRequestDTO updateAIContextRequestDTO);

    AIContextSearchCriteria mapSearch(SearchAIContextRequestDTO searchAIContextRequestDTO);

    SearchAIContextResponseDTO mapSearchPageResult(AIContextPageResult aiContextPageResult);
}
