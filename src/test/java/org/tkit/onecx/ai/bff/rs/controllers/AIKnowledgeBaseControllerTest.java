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
@TestHTTPEndpoint(AIKnowledgeBaseController.class)
class AIKnowledgeBaseControllerTest extends AbstractTest {

    private static final String AI_KNOWLEDGE_BASE_API_BASE_PATH = "/internal/ai/ai-knowledgebases";

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
    void getAIKnowledgeBaseById_200Test() {
        AIKnowledgeBase fakeData = createAIKnowledgeBase("1", "Test Base 1", "desc", "app", 0);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/" + testId)
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
                .get("/" + testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(AIKnowledgeBaseDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Test Base 1", response.getName());
    }

    @Test
    void getAIKnowledgeBaseById_404Test() {
        String testId = "non-existent-id";

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get("/" + testId)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void deleteAIKnowledgeBaseTest() {
        String id = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.DELETE))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .delete("/" + id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void updateAIKnowledgeBaseTest() {
        AIKnowledgeBaseDTO dataObject = new AIKnowledgeBaseDTO();
        dataObject.setName("Updated Base");
        dataObject.setDescription("desc");
        dataObject.setAppId("app");

        UpdateAIKnowledgeBaseRequestDTO updateDTO = new UpdateAIKnowledgeBaseRequestDTO();
        updateDTO.setDataObject(dataObject);

        AIKnowledgeBase responseDTO = createAIKnowledgeBase("1", "Updated Base", "desc", "app", 1);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(responseDTO)));

        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateDTO)
                .put("/" + testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(UpdateAIKnowledgeBaseRequestDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDataObject());
        Assertions.assertEquals("Updated Base", response.getDataObject().getName());
    }

    @Test
    void updateAIKnowledgeBase_400Test() {
        UpdateAIKnowledgeBaseRequestDTO updateDTO = new UpdateAIKnowledgeBaseRequestDTO();
        AIKnowledgeBaseDTO dataObject = new AIKnowledgeBaseDTO();
        dataObject.setName("");
        updateDTO.setDataObject(dataObject);

        String testId = "1";

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.BAD_REQUEST.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateDTO)
                .put("/" + testId)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void updateAIKnowledgeBase_404Test() {
        UpdateAIKnowledgeBaseRequestDTO updateDTO = new UpdateAIKnowledgeBaseRequestDTO();
        AIKnowledgeBaseDTO dataObject = new AIKnowledgeBaseDTO();
        dataObject.setName("Updated Base");
        updateDTO.setDataObject(dataObject);

        String testId = "non-existent-id";

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateDTO)
                .put("/" + testId)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void updateAIKnowledgeBaseWithoutBody_ConstraintViolation400Test() {
        // Test constraintException by sending PUT request without body (required field missing)
        String testId = "1";

        var updateResponseException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .put("/" + testId) // No body - violates @NotNull constraint
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(gen.org.tkit.onecx.ai.management.bff.rs.internal.model.ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(updateResponseException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", updateResponseException.getErrorCode());
    }

    @Test
    void createAIKnowledgeBaseTest() {
        AIKnowledgeBaseDTO dataObject = new AIKnowledgeBaseDTO();
        dataObject.setName("New Base");
        dataObject.setDescription("desc");
        dataObject.setAppId("app");

        CreateAIKnowledgeBaseRequestDTO createDTO = new CreateAIKnowledgeBaseRequestDTO();
        createDTO.setDataObject(dataObject);

        AIKnowledgeBase responseDTO = createAIKnowledgeBase("new-id", "New Base", "desc", "app", 0);

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH)
                        .withMethod(HttpMethod.POST))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(responseDTO)));

        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(createDTO)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .as(AIKnowledgeBaseDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("New Base", response.getName());
    }

    @Test
    void createAIKnowledgeBase_400Test() {
        CreateAIKnowledgeBaseRequestDTO createDTO = new CreateAIKnowledgeBaseRequestDTO();
        AIKnowledgeBaseDTO dataObject = new AIKnowledgeBaseDTO();
        dataObject.setName("");
        createDTO.setDataObject(dataObject);

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH)
                        .withMethod(HttpMethod.POST))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.BAD_REQUEST.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(createDTO)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void createAIKnowledgeBaseWithoutBody_ConstraintViolation400Test() {
        // Test constraintException by sending POST request without body
        var createResponseException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post() // No body - violates @NotNull constraint
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(gen.org.tkit.onecx.ai.management.bff.rs.internal.model.ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(createResponseException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", createResponseException.getErrorCode());
    }

    @Test
    void searchAIKnowledgeBasesTest() {
        SearchAIKnowledgeBaseRequestDTO criteria = new SearchAIKnowledgeBaseRequestDTO();
        criteria.setName("Test Base");

        AIKnowledgeBase base1 = createAIKnowledgeBase("1", "Test Base", "desc", "app", 0);
        AIKnowledgeBase base2 = createAIKnowledgeBase("2", "Test Base", "desc", "app", 0);

        AIKnowledgeBasePageResult pageResult = new AIKnowledgeBasePageResult();
        pageResult.setNumber(0);
        pageResult.setSize(10);
        pageResult.setTotalPages(1L);
        pageResult.setStream(java.util.List.of(base1, base2));
        pageResult.setTotalElements(2L);

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(pageResult)));

        var results = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(SearchAIKnowledgeBaseResponseDTO.class);

        Assertions.assertNotNull(results);
        Assertions.assertEquals(2, results.getTotalElements());
        Assertions.assertEquals("Test Base", results.getStream().get(0).getName());
    }

    @Test
    void searchAIKnowledgeBases_400Test() {
        SearchAIKnowledgeBaseRequestDTO criteria = new SearchAIKnowledgeBaseRequestDTO();

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_BASE_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.BAD_REQUEST.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    private AIKnowledgeBase createAIKnowledgeBase(String id, String name, String description, String appId,
            int modificationCount) {
        AIKnowledgeBase dto = new AIKnowledgeBase();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAppId(appId);
        dto.setModificationCount(modificationCount);
        return dto;
    }
}