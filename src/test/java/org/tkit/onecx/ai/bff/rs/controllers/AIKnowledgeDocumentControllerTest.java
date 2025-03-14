package org.tkit.onecx.ai.bff.rs.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

// static imports
import java.time.OffsetDateTime;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.ai.bff.rs.AbstractTest;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.ai.bff.rs.internal.model.AIKnowledgeDocumentDTO;
import gen.org.tkit.onecx.ai.bff.rs.internal.model.UpdateAIKnowledgeDocumentDTO;
import gen.org.tkit.onecx.ai.mgmt.client.model.AIKnowledgeDocument;
import gen.org.tkit.onecx.ai.mgmt.client.model.DocumentStatusType;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

/**
 * @author tchanad
 * @project onecx-ai-management-bff
 */
@QuarkusTest
@LogService
@TestHTTPEndpoint(AIKnowledgeDocumentController.class)
public class AIKnowledgeDocumentControllerTest extends AbstractTest {

    private static final String AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH = "/internal/ai/ai-knowledge-documents";
    // Instanciate keycloack test client
    KeycloakTestClient keycloakTestClient = new KeycloakTestClient();

    // inject MockServer client
    @InjectMockServerClient
    MockServerClient mockServerClient;

    static final String MOCK_ID = "MOCK";

    @BeforeEach
    void resetExpectation() {
        try {
            mockServerClient.clear(MOCK_ID);
        } catch (Exception ex) {
            // Mock_ID not exists
        }
    }

    // should return successfully an give knowledge document by Id
    @Test
    void getAIKnowledgeDocumentById_200() {
        // Arrage
        var offsetDateTime = OffsetDateTime.parse("2025-03-13T14:45:52.423454002+01:00");
        AIKnowledgeDocument fakeData = new AIKnowledgeDocument();
        fakeData.setVersion(1);
        fakeData.setCreationDate(offsetDateTime);
        fakeData.creationUser("FakeUser");
        fakeData.setModificationDate(offsetDateTime);
        fakeData.setModificationUser("FakeModUser");
        fakeData.setId("1");
        fakeData.setStatus(DocumentStatusType.NEW);
        fakeData.setName("Test Document 1");
        fakeData.setDocumentRefId("4e1c07bb-ef34-4017-b690-e5dfe3960590");

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(fakeData)));

        // Bff call DTO
        // Act
        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get(testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .as(AIKnowledgeDocumentDTO.class);

        // Assert
        Assertions.assertNotNull(response);
    }

    // should delete a Knowledge Document by Id
    @Test
    void deleteAIKnowledgeDocument() {
        //Arrange
        String id = "1";

        mockServerClient
                .when(request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/" + id)
                        .withMethod(HttpMethod.DELETE))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode()));
        // Act
        // Bff Service
        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .delete(id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        // Assert
        Assertions.assertNotNull(response);
    }

    // should update a knowledge document
    @Test
    void updateAIKNowledgeDocument() {
        // Arrage
        // create Document DTO
        AIKnowledgeDocumentDTO documentDTO = new AIKnowledgeDocumentDTO();
        documentDTO.setId("1");
        documentDTO.setDocumentRefId("4e1c07bb-ef34-4017-b690-e5dfe3960590");
        documentDTO.setName("Test AIKnowledge Document 1");
        documentDTO.setStatus(AIKnowledgeDocumentDTO.StatusEnum.PROCESSING);
        documentDTO.setModificationCount(1);

        // initialize it with Document DTO for Update request
        UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO;
        updateAIKnowledgeDocumentDTO = new UpdateAIKnowledgeDocumentDTO();
        updateAIKnowledgeDocumentDTO.setaIKnowledgeDocumentData(documentDTO);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        // Bff call DTO
        // Act
        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateAIKnowledgeDocumentDTO)
                .put(testId)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        // Assert
        Assertions.assertNotNull(response);
    }
}
