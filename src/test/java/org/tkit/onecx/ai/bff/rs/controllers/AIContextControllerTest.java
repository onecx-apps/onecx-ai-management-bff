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
@TestHTTPEndpoint(AIContextController.class)
class AIContextControllerTest extends AbstractTest {

    private static final String AI_CONTEXT_API_BASE_PATH = "/internal/ai/ai-contexts";
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
    void getAIContextById_200Test() {
        AIContext fakeData = createAIContext("1", "Test Context", "desc", "app", 0);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_CONTEXT_API_BASE_PATH + "/" + testId)
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
                .get(testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(AIContext.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Test Context", response.getName());
    }

    @Test
    void getAIContextById_404Test() {
        String testId = "non-existent-id";

        mockServerClient.when(
                request().withPath(AI_CONTEXT_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get(testId)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void deleteAIContextTest() {
        String id = "1";
        mockServerClient.when(
                request().withPath(AI_CONTEXT_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.DELETE))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .delete(id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void updateAIContextTest() {
        UpdateAIContextRequestDTO updateDTO = new UpdateAIContextRequestDTO();
        AIContextDTO dataObject = new AIContextDTO();
        dataObject.setName("Updated Context");
        updateDTO.setDataObject(dataObject);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_CONTEXT_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(updateDTO)));

        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateDTO)
                .put(testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(UpdateAIContextRequestDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Updated Context", response.getDataObject().getName());
    }

    @Test
    void updateAIContext_400Test() {
        UpdateAIContextRequestDTO updateDTO = new UpdateAIContextRequestDTO();
        AIContextDTO dataObject = new AIContextDTO();
        dataObject.setName("");
        updateDTO.setDataObject(dataObject);

        String testId = "1";

        mockServerClient.when(
                request().withPath(AI_CONTEXT_API_BASE_PATH + "/" + testId)
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
                .put(testId)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void updateAIContext_404Test() {
        UpdateAIContextRequestDTO updateDTO = new UpdateAIContextRequestDTO();
        AIContextDTO dataObject = new AIContextDTO();
        dataObject.setName("Updated Context");
        updateDTO.setDataObject(dataObject);

        String testId = "non-existent-id";

        mockServerClient.when(
                request().withPath(AI_CONTEXT_API_BASE_PATH + "/" + testId)
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
                .put(testId)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void updateAIContextWithoutBody_ConstraintViolation400Test() {
        // Test constraintException by sending PUT request without body (required field missing)
        String testId = "1";

        var updateResponseException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .put(testId) // No body - violates @NotNull/@Valid constraint
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(updateResponseException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", updateResponseException.getErrorCode());
    }

    @Test
    void searchAIContextsTest() {
        AIContextSearchCriteria criteria = new AIContextSearchCriteria();
        criteria.setName("Test Context");

        AIContext context1 = createAIContext("1", "Test Context", "desc", "app", 0);
        AIContext context2 = createAIContext("2", "Test Context", "desc", "app", 0);

        AIContextPageResult responseDTO = new AIContextPageResult();
        responseDTO.setNumber(0);
        responseDTO.setSize(10);
        responseDTO.setTotalPages(1L);
        responseDTO.setStream(java.util.List.of(context1, context2));
        responseDTO.setTotalElements(2L);

        mockServerClient.when(
                request().withPath(AI_CONTEXT_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(criteria)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(responseDTO)));

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
                .as(AIContextPageResult.class);

        Assertions.assertNotNull(results);
        Assertions.assertEquals(2L, results.getTotalElements());
        Assertions.assertEquals("Test Context", results.getStream().get(0).getName());
    }

    private AIContext createAIContext(String id, String name, String description, String appId, int modificationCount) {
        AIContext context = new AIContext();
        context.setId(id);
        context.setName(name);
        //        context.setDescription(description);
        //        context.setAppId(appId);
        context.setModificationCount(modificationCount);
        return context;
    }
}