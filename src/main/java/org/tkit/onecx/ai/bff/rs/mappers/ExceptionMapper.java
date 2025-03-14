package org.tkit.onecx.ai.bff.rs.mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.RestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.model.ProblemDetailInvalidParam;
import gen.org.tkit.onecx.permission.model.ProblemDetailParam;
import gen.org.tkit.onecx.permission.model.ProblemDetailResponse;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ExceptionMapper {

    default RestResponse<Object> constraint(ConstraintViolationException ex) {
        var dto = exception("CONSTRAINT_VIOLATIONS", ex.getMessage());
        dto.setInvalidParams(createErrorValidationResponse(ex.getConstraintViolations()));
        return RestResponse.status(Response.Status.BAD_REQUEST, dto);
    }

    default Response clientException(ClientWebApplicationException ex) {
        if (ex.getResponse().getStatus() == 500) {
            return Response.status(400)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"error\": \"Internal Server Error\"}")
                    .build();
        } else {
            if (ex.getResponse().getMediaType() != null
                    && ex.getResponse().getMediaType().toString().contains(MediaType.APPLICATION_JSON)) {
                return Response.status(ex.getResponse().getStatus())
                        .type(MediaType.APPLICATION_JSON)
                        .entity(map(ex.getResponse().readEntity(ProblemDetailResponse.class)))
                        .build();
            } else {
                return Response.status(ex.getResponse().getStatus())
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{\"error\": \"Unsupported Media Type\"}")
                        .build();
            }
        }
    }

    Object map(ProblemDetailResponse problemDetailResponse);

    @Mapping(target = "params", ignore = true)
    @Mapping(target = "invalidParams", ignore = true)
    ProblemDetailResponse exception(String errorCode, String detail);

    default List<ProblemDetailParam> map(Map<String, Object> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream().map(e -> {
            var item = new ProblemDetailParam();
            item.setKey(e.getKey());
            if (e.getValue() != null) {
                item.setValue(e.getValue().toString());
            }
            return item;
        }).toList();
    }

    @Mapping(target = "Collection", ignore = true)
    List<ProblemDetailInvalidParam> createErrorValidationResponse(
            Set<ConstraintViolation<?>> constraintViolation);

    default String mapPathToString(Path propertyPath) {
        return propertyPath != null ? propertyPath.toString() : null;
    }

    @Mapping(target = "name", expression = "java(mapPathToString(constraintViolation.getPropertyPath()))")
    ProblemDetailInvalidParam createError(ConstraintViolation<?> constraintViolation);

    default String mapPath(Path path) {
        return path.toString();
    }

}
