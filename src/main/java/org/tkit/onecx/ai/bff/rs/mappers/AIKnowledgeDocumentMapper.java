package org.tkit.onecx.ai.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.ai.bff.rs.internal.model.AIKnowledgeDocumentDTO;
import gen.org.tkit.onecx.ai.bff.rs.internal.model.CreateAIKnowledgeDocumentDTO;
import gen.org.tkit.onecx.ai.bff.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.ai.bff.rs.internal.model.UpdateAIKnowledgeDocumentDTO;
import gen.org.tkit.onecx.ai.mgmt.client.model.AIKnowledgeDocument;
import gen.org.tkit.onecx.ai.mgmt.client.model.CreateAIKnowledgeDocumentRequest;
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

    //    @Mapping(target = "modificationCount", source = "aIKnowledgeDocumentData.modificationCount")
    //    @Mapping(target = "status", source = "aIKnowledgeDocumentData.status")
    //    @Mapping(target = "name", source = "aIKnowledgeDocumentData.name")
    //    @Mapping(target = "documentRefId", source = "aIKnowledgeDocumentData.documentRefId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "documentRefId", source = "aIKnowledgeDocumentData.documentRefId")
    UpdateAIKnowledgeDocumentRequest mapUpdate(UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "documentRefId", ignore = true)
    CreateAIKnowledgeDocumentRequest mapCreate(CreateAIKnowledgeDocumentDTO createAIKnowledgeDocumentDTO);

    //
    @Mapping(target = "removeParamsItem", ignore = true)
    ProblemDetailResponseDTO mapErrorDetails(ProblemDetailResponse problemDetailResponse);
}
