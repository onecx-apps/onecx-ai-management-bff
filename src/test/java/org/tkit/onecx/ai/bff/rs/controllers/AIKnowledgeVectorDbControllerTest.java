package org.tkit.onecx.ai.bff.rs.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.ai.bff.rs.AbstractTest;
import org.tkit.quarkus.log.cdi.LogService;

// Client model classes (for MockServer responses)
import gen.org.tkit.onecx.ai.management.bff.client.model.*;
// BFF API DTOs (for RestAssured requests/responses)
import gen.org.tkit.onecx.ai.management.bff.rs.internal.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@LogService
@TestHTTPEndpoint(AIKnowledgeVectorDbController.class)
class AIKnowledgeVectorDbControllerTest extends AbstractTest {

    private static final String AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH = "/internal/ai/ai-knowledge-vdbs";
    @InjectMockServerClient
    MockServerClient mockServerClient;

    KeycloakTestClient keycloakTestClient = new KeycloakTestClient();

    static final String MOCK_ID = "MOCK";

    @AfterEach
    void resetMockserver() {
        try {
            mockServerClient.clear(MOCK_ID);
        } catch (Exception ex) {
            // mockId not existing
        }
    }

    @Test
    void getAIKnowledgeVectorDbById_200Test() {
        AIKnowledgeVectorDb fakeData = createAIKnowledgeVectorDbClientModel(
                "1",
                "Test Vector 1",
                "test-vdb-url",
                "test-collection",
                "desc",
                0);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(fakeData)));

        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", testId)
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(AIKnowledgeVectorDbDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Test Vector 1", response.getName());
        Assertions.assertEquals("test-collection", response.getVdbCollection());
        Assertions.assertEquals("desc", response.getDescription());
        Assertions.assertEquals("test-vdb-url", response.getVdb());
        Assertions.assertEquals(0, response.getModificationCount());
    }

    @Test
    void getAIKnowledgeVectorDbById_Fail_Test() {
        AIKnowledgeVectorDb fakeData = createAIKnowledgeVectorDbClientModel(
                "1",
                "Test Vector 1",
                "test-vdb-url",
                "test-collection",
                "desc",
                0);

        String testId = "false-id";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(fakeData)));

        var exception = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", testId)
                .get()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
    }

    @Test
    void getAIKnowledgeVectorDbById_404Test() {
        String testId = "non-existent-id";

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", testId)
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void deleteAIKnowledgeVectorDbTest() {
        String id = "1";

        mockServerClient
                .when(request().withPath(AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.DELETE))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode()));

        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", id)
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        Assertions.assertNotNull(response);
    }

    @Test
    void updateAIKnowledgeVectorDbTest() {
        UpdateAIKnowledgeVectorDbRequestDTO dto = new UpdateAIKnowledgeVectorDbRequestDTO();
        dto.setName("Test Vector 1");
        dto.setVdb("test-vdb-url");
        dto.setVdbCollection("test-collection");
        dto.setDescription("desc");

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NO_CONTENT.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("id", testId)
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void updateAIKnowledgeVectorDb_Fail_with_bad_requestTest() {
        UpdateAIKnowledgeVectorDbRequestDTO dto = new UpdateAIKnowledgeVectorDbRequestDTO();
        dto.setName("Test Vector 1");

        String testId = "1";

        gen.org.tkit.onecx.ai.management.bff.client.model.ProblemDetailResponse errorResponse = new gen.org.tkit.onecx.ai.management.bff.client.model.ProblemDetailResponse();
        errorResponse.setErrorCode("CONSTRAINT_VIOLATIONS");

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(errorResponse)));

        var updateResponseException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("id", testId)
                .put()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(updateResponseException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", updateResponseException.getErrorCode());
    }

    @Test
    void updateAIKnowledgeVectorDb_404Test() {
        UpdateAIKnowledgeVectorDbRequestDTO dto = new UpdateAIKnowledgeVectorDbRequestDTO();
        dto.setName("Updated Vector");
        dto.setVdb("updated-url");
        dto.setVdbCollection("updated-collection");

        String testId = "non-existent-id";

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_VECTOR_DB_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(dto)
                .pathParam("id", testId)
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void updateAIKnowledgeVectorDbWithoutBody_ConstraintViolation400Test() {
        // Test constraintException by sending PUT request without body (required field missing)
        String testId = "1";

        var updateResponseException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", testId)
                .put() // No body - violates @NotNull @Valid constraint
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(updateResponseException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", updateResponseException.getErrorCode());
    }

    private AIKnowledgeVectorDb createAIKnowledgeVectorDbClientModel(String id, String name, String vdbUrl,
            String vdbCollection,
            String description, int modificationCount) {
        AIKnowledgeVectorDb vectorDb = new AIKnowledgeVectorDb();
        vectorDb.setId(id);
        vectorDb.setName(name);
        vectorDb.setVdbUrl(vdbUrl);
        vectorDb.setVdbCollection(vdbCollection);
        vectorDb.setDescription(description);
        vectorDb.setModificationCount(modificationCount);
        return vectorDb;
    }
}
