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
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@LogService
class AIProviderControllerTest extends AbstractTest {

    private static final String AI_PROVIDER_API_BASE_PATH = "/internal/ai/ai-providers";
    private static final String BFF_BASE_PATH = "/aIProvider";

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
    void getAIProviderById_200Test() {
        AIProvider fakeData = createAIProvider("1", "Test Provider", "desc", "url", "app", "model", "v1", "key", 0);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_PROVIDER_API_BASE_PATH + "/" + testId)
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
                .get(BFF_BASE_PATH + "/" + testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(AIProvider.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Test Provider", response.getName());
    }

    @Test
    void searchAIProviderTest() {
        AIProviderSearchRequestDTO requestDTO = new AIProviderSearchRequestDTO();
        requestDTO.setName("Provider");

        AIProviderSearchCriteria criteria = new AIProviderSearchCriteria();
        criteria.setName("Provider");

        AIProvider provider1 = createAIProvider("1", "Provider", "desc", "url", "app", "model", "v1", "key", 0);

        AIProviderPageResult pageResult = new AIProviderPageResult();
        pageResult.setNumber(0);
        pageResult.setSize(10);
        pageResult.setTotalPages(1L);
        pageResult.setStream(java.util.List.of(provider1));
        pageResult.setTotalElements(1L);

        mockServerClient.when(
                request().withPath(AI_PROVIDER_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(criteria)))
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
                .body(requestDTO)
                .post(BFF_BASE_PATH + "/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(AIProviderSearchResponseDTO.class);

        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.getTotalNumberOfResults().intValue());
        Assertions.assertEquals("Provider", results.getResults().get(0).getName());
    }

    @Test
    void createAIProviderTest() {
        AIProviderDTO dataObject = new AIProviderDTO();
        dataObject.setId("new-id");
        dataObject.setName("New Provider");
        dataObject.setDescription("desc");
        dataObject.setLlmUrl("url");
        dataObject.setAppId("app");
        dataObject.setModelName("model");
        dataObject.setModelVersion("v1");
        dataObject.setApiKey("key");

        CreateAIProviderDTO createDTO = new CreateAIProviderDTO();
        createDTO.setDataObject(dataObject);

        CreateAIProviderRequest createRequest = new CreateAIProviderRequest();
        createRequest.setName("New Provider");
        createRequest.setDescription("desc");
        createRequest.setLlmUrl("url");
        createRequest.setAppId("app");
        createRequest.setModelName("model");
        createRequest.setModelVersion("v1");
        createRequest.setApiKey("key");

        AIProvider responseDTO = new AIProvider();
        responseDTO.setId("new-id");
        responseDTO.setName("New Provider");

        mockServerClient.when(
                request().withPath(AI_PROVIDER_API_BASE_PATH)
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(createRequest)))
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
                .post(BFF_BASE_PATH)
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .extract()
                .as(AIProviderDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("New Provider", response.getName());
    }

    @Test
    void updateAIProviderTest() {
        AIProviderDTO dataObject = new AIProviderDTO();
        dataObject.setId("1");
        dataObject.setName("Updated Provider");
        dataObject.setDescription("desc");
        dataObject.setLlmUrl("url");
        dataObject.setAppId("app");
        dataObject.setModelName("model");
        dataObject.setModelVersion("v2");
        dataObject.setApiKey("key");

        UpdateAIProviderDTO updateDTO = new UpdateAIProviderDTO();
        updateDTO.setDataObject(dataObject);

        UpdateAIProviderRequest updateRequest = new UpdateAIProviderRequest();
        updateRequest.setName("Updated Provider");
        updateRequest.setDescription("desc");
        updateRequest.setLlmUrl("url");
        updateRequest.setAppId("app");
        updateRequest.setModelName("model");
        updateRequest.setModelVersion("v2");
        updateRequest.setApiKey("key");

        AIProvider responseDTO = new AIProvider();
        responseDTO.setId("1");
        responseDTO.setName("Updated Provider");

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_PROVIDER_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT)
                        .withBody(JsonBody.json(updateRequest)))
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
                .put(BFF_BASE_PATH + "/" + testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(UpdateAIProviderDTO.class);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDataObject());
        Assertions.assertEquals("Updated Provider", response.getDataObject().getName());
    }

    @Test
    void deleteAIProviderTest() {
        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_PROVIDER_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.DELETE))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NO_CONTENT.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .delete(BFF_BASE_PATH + "/" + testId)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    private AIProvider createAIProvider(String id, String name, String description, String llmUrl, String appId,
            String modelName, String modelVersion, String apiKey, int modificationCount) {
        AIProvider dto = new AIProvider();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setLlmUrl(llmUrl);
        dto.setAppId(appId);
        dto.setModelName(modelName);
        dto.setModelVersion(modelVersion);
        dto.setApiKey(apiKey);
        dto.setModificationCount(modificationCount);
        return dto;
    }
}
