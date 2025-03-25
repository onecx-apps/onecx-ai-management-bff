package org.tkit.onecx.ai.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.ai.bff.rs.internal.model.AIKnowledgeDocumentDTO;
import gen.org.tkit.onecx.ai.bff.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.ai.bff.rs.internal.model.UpdateAIKnowledgeDocumentDTO;
import gen.org.tkit.onecx.ai.mgmt.client.model.AIKnowledgeDocument;
//import gen.org.tkit.onecx.ai.mgmt.client.model.CreateAIKnowledgeDocumentRequest;
import gen.org.tkit.onecx.ai.mgmt.client.model.UpdateAIKnowledgeDocumentRequest;
import gen.org.tkit.onecx.permission.model.ProblemDetailResponse;

/**
 * @author tchanad
 * @project onecx-ai-management-bff
 */

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface AIKnowledgeDocumentMapper {

    //        @Mapping(target = "modificationCount", ignore = true)
    AIKnowledgeDocumentDTO mapById(AIKnowledgeDocument aiKnowledgeDocument);

    UpdateAIKnowledgeDocumentRequest mapUpdate(UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO);

    // CreateAIKnowledgeDocumentRequest mapCreate(CreateAIKnowledgeDocumentDTO createAIKnowledgeDocumentDTO);

    //
    @Mapping(target = "removeParamsItem", ignore = true)
    ProblemDetailResponseDTO mapErrorDetails(ProblemDetailResponse problemDetailResponse);
}
