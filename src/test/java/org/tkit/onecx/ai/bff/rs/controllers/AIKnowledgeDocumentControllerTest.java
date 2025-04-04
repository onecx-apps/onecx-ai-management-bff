package org.tkit.onecx.ai.bff.rs.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;

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

import gen.org.tkit.onecx.ai.bff.rs.internal.model.*;
import gen.org.tkit.onecx.ai.mgmt.client.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@LogService
@TestHTTPEndpoint(AIKnowledgeDocumentController.class)
class AIKnowledgeDocumentControllerTest extends AbstractTest {

    private static final String AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH = "/internal/ai/ai-knowledge-documents";
    private static final String AI_CONTEXT_SVC_BASE_PATH = "/internal/ai/ai-contexts";
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
    void getAIKnowledgeDocumentById_200Test() {
        // Arrage
        AIKnowledgeDocument fakeData = createAIKnowledgeDocument(
                "1",
                "Test Document 1",
                "4e1c07bb-ef34-4017-b690-e5dfe3960590",
                DocumentStatusType.NEW,
                0);

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
        Assertions.assertEquals("4e1c07bb-ef34-4017-b690-e5dfe3960590", response.getDocumentRefId());
    }

    @Test
    void getAIKnowledgeDocumentById_Fail_Test() {
        // Arrage
        AIKnowledgeDocument fakeData = createAIKnowledgeDocument(
                "1",
                "Test Document 1",
                "4e1c07bb-ef34-4017-b690-e5dfe3960590",
                DocumentStatusType.NEW,
                0);

        String testId = "false-id";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(fakeData)));

        // Bff call DTO
        // Act
        var exception = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get(testId)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(ProblemDetailResponseDTO.class);

        // Assert
        Assertions.assertNotNull(exception);
    }

    // should delete a Knowledge Document by Id
    @Test
    void deleteAIKnowledgeDocumentTest() {
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

    // should update a AIKnowledge Document
    @Test
    void updateAIKnowledgeDocumentTest() {
        // Arrage
        // initialize it with Document DTO for Update request
        UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO;
        updateAIKnowledgeDocumentDTO = new UpdateAIKnowledgeDocumentDTO();
        updateAIKnowledgeDocumentDTO.setId("1");
        updateAIKnowledgeDocumentDTO.setDocumentRefId("4e1c07bb-ef34-4017-b690-e5dfe3960590");
        updateAIKnowledgeDocumentDTO.setName("Test AIKnowledge Document 1");
        updateAIKnowledgeDocumentDTO.setStatus(AIKnowledgeDocumentStatusTypeDTO.PROCESSING);
        updateAIKnowledgeDocumentDTO.setModificationCount(1);

        String testId = "1";
        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/" + testId)
                        .withMethod(HttpMethod.PUT))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));
        // Act
        var response = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateAIKnowledgeDocumentDTO)
                .put(testId)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(UpdateAIKnowledgeDocumentDTO.class);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Test AIKnowledge Document 1", response.getName());
    }

    @Test
    void createNewAIKnowledgeDocumentTest() {
        AIKnowledgeDocument newDocument = createAIKnowledgeDocument(
                "ai-knowledge-document-id",
                "New Test Document Create",
                "document-ref-id",
                DocumentStatusType.NEW,
                0);

        // create request
        CreateAIKnowledgeDocumentRequest createAIKnowledgeDocumentRequest = new CreateAIKnowledgeDocumentRequest();
        createAIKnowledgeDocumentRequest.setStatus(newDocument.getStatus());
        createAIKnowledgeDocumentRequest.setDocumentRefId(newDocument.getDocumentRefId());
        createAIKnowledgeDocumentRequest.setName(newDocument.getName());

        AIKnowledgeDocumentCreateRequestDTO aiKnowledgeDocumentData = new AIKnowledgeDocumentCreateRequestDTO();
        aiKnowledgeDocumentData.setDocumentRefId(newDocument.getDocumentRefId());
        aiKnowledgeDocumentData.setName(newDocument.getName());
        aiKnowledgeDocumentData.setStatus(AIKnowledgeDocumentStatusTypeDTO.NEW);

        CreateAIKnowledgeDocumentDTO dto = new CreateAIKnowledgeDocumentDTO();
        dto.setaIKnowledgeDocumentData(aiKnowledgeDocumentData);

        String contextId = "context-11-111";

        mockServerClient.when(
                request().withPath(AI_CONTEXT_SVC_BASE_PATH + "/" + contextId + "/ai-knowledge-documents")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(createAIKnowledgeDocumentRequest))
                        .withMethod(HttpMethod.POST))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(newDocument)));

        var createResult = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(dto)
                .queryParams("id", contextId)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(AIKnowledgeDocumentDTO.class);

        Assertions.assertNotNull(createResult);
        Assertions.assertEquals("New Test Document Create", createResult.getName());
        Assertions.assertEquals("document-ref-id", createResult.getDocumentRefId());
        Assertions.assertEquals("NEW", createResult.getStatus().name());
    }

    @Test
    void createAIKnowledgeDocument_shouldFail_wrongAIContextId_Test() {
        var aiContextId = "non-existing-ai-context-id";
        AIKnowledgeDocument aiKnowledgeDocument = createAIKnowledgeDocument(
                "new-test-document-id",
                "New Test Document 2",
                "new-test-documentRef-id",
                DocumentStatusType.NEW,
                0);
        // Create a request
        AIKnowledgeDocumentCreateRequestDTO createAIKnowledgeDocumentRequest = new AIKnowledgeDocumentCreateRequestDTO();
        createAIKnowledgeDocumentRequest.setName(aiKnowledgeDocument.getName());
        createAIKnowledgeDocumentRequest.setDocumentRefId(aiKnowledgeDocument.getDocumentRefId());
        createAIKnowledgeDocumentRequest.setStatus(AIKnowledgeDocumentStatusTypeDTO.NEW);

        CreateAIKnowledgeDocumentDTO aiKnowledgeDocumentDTO = new CreateAIKnowledgeDocumentDTO();
        aiKnowledgeDocumentDTO.setaIKnowledgeDocumentData(createAIKnowledgeDocumentRequest);

        var createResponseException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(JsonBody.json(aiKnowledgeDocumentDTO))
                .queryParams("id", aiContextId)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(createResponseException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", createResponseException.getErrorCode());
        Assertions.assertEquals("must not be null", createResponseException.getInvalidParams().get(0).getMessage());
    }

    // should fail with status code 400 because mandatory fields
    // are not provided
    @Test
    void updateAIKnowledgeDocument_Fail_with_bad_requestTest() {
        // Arrage
        UpdateAIKnowledgeDocumentDTO updateAIKnowledgeDocumentDTO;
        updateAIKnowledgeDocumentDTO = new UpdateAIKnowledgeDocumentDTO();
        updateAIKnowledgeDocumentDTO.setId("1");
        updateAIKnowledgeDocumentDTO.setName("Test AIKnowledge Document 1");
        updateAIKnowledgeDocumentDTO.setStatus(AIKnowledgeDocumentStatusTypeDTO.EMBEDDED);

        String testId = "1";
        // Act
        var updateResponseException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(updateAIKnowledgeDocumentDTO)
                .put(testId)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(updateResponseException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", updateResponseException.getErrorCode());
    }

    @Test
    void searchAIKnowledgeDocumentByCriteria_shouldReturn_AllResultsTest() {
        // Create a custom PageResult data for mocking server response
        AIKnowledgeDocumentPageResult documentPageResult = new AIKnowledgeDocumentPageResult();
        documentPageResult.setNumber(0);
        documentPageResult.setSize(10);
        documentPageResult.setTotalPages(1L);

        List<AIKnowledgeDocument> documents = generateMockAIKnowledgeDocumentData();
        documentPageResult.setStream(documents);
        documentPageResult.setTotalElements((long) documents.size());

        // Empty criteria
        AIKnowledgeDocumentSearchCriteriaDTO criteriaDTO = new AIKnowledgeDocumentSearchCriteriaDTO();

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(new AIKnowledgeDocumentSearchCriteria().pageSize(10).pageNumber(0))))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(documentPageResult)));

        var results = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .post("/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIKnowledgeDocumentSearchPageResultDTO.class);

        Assertions.assertNotNull(results);
        Assertions.assertEquals(5, results.getTotalNumberOfResults());
    }

    @Test
    void searchAIKnowledgeDocumentByRefIdTest() {
        AIKnowledgeDocumentPageResult documentPageResult = new AIKnowledgeDocumentPageResult();
        documentPageResult.setNumber(0);
        documentPageResult.setSize(10);
        documentPageResult.setTotalPages(1L);

        List<AIKnowledgeDocument> documents = generateMockAIKnowledgeDocumentData()
                .stream()
                .filter(
                        document -> document.getDocumentRefId()
                                .equals("210366c3-2ea6-432f-9443-7d9d2680d001"))
                .toList();
        documentPageResult.setStream(documents);
        documentPageResult.setTotalElements((long) documents.size());

        // only provide Ref_id
        AIKnowledgeDocumentSearchCriteria searchCriteria;
        searchCriteria = new AIKnowledgeDocumentSearchCriteria();
        searchCriteria.setDocumentRefId("210366c3-2ea6-432f-9443-7d9d2680d001");

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(searchCriteria)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(documentPageResult)));

        // for bff call
        AIKnowledgeDocumentSearchCriteriaDTO searchCriteriaDTO = new AIKnowledgeDocumentSearchCriteriaDTO();
        searchCriteriaDTO.setDocumentRefId("210366c3-2ea6-432f-9443-7d9d2680d001");

        var results = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(searchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIKnowledgeDocumentSearchPageResultDTO.class);

        Assertions.assertNotNull(results);
        Assertions.assertEquals(3, results.getTotalNumberOfResults());
        Assertions.assertEquals("210366c3-2ea6-432f-9443-7d9d2680d001", results.getResults().get(0).getDocumentRefId());
        Assertions.assertEquals("210366c3-2ea6-432f-9443-7d9d2680d001", results.getResults().get(1).getDocumentRefId());
        Assertions.assertEquals("210366c3-2ea6-432f-9443-7d9d2680d001", results.getResults().get(2).getDocumentRefId());
    }

    @Test
    void searchAIKnowledgeDocumentByStatusTest() {
        AIKnowledgeDocumentPageResult documentPageResult = new AIKnowledgeDocumentPageResult();
        documentPageResult.setNumber(0);
        documentPageResult.setSize(10);
        documentPageResult.setTotalPages(1L);

        List<AIKnowledgeDocument> documents = generateMockAIKnowledgeDocumentData()
                .stream()
                .filter(document -> document.getStatus().value().equals("NEW"))
                .toList();
        documentPageResult.setStream(documents);
        documentPageResult.setTotalElements((long) documents.size());

        // provide only status as search criteria
        AIKnowledgeDocumentSearchCriteria searchCriteria;
        searchCriteria = new AIKnowledgeDocumentSearchCriteria();
        searchCriteria.setStatus(DocumentStatusType.NEW);

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(searchCriteria)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(documentPageResult)));

        // bff call
        AIKnowledgeDocumentSearchCriteriaDTO searchCriteriaDTO = new AIKnowledgeDocumentSearchCriteriaDTO();
        searchCriteriaDTO.setStatus(AIKnowledgeDocumentStatusTypeDTO.NEW);

        var results = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(searchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIKnowledgeDocumentSearchPageResultDTO.class);

        Assertions.assertNotNull(results);
        Assertions.assertEquals(3, results.getTotalNumberOfResults());
        Assertions.assertEquals("NEW", results.getResults().get(0).getStatus().name());
        Assertions.assertEquals("NEW", results.getResults().get(1).getStatus().name());
        Assertions.assertEquals("NEW", results.getResults().get(2).getStatus().name());
    }

    @Test
    void searchAIKnowledgeDocumentByNameTest() {
        AIKnowledgeDocumentPageResult documentPageResult = new AIKnowledgeDocumentPageResult();
        documentPageResult.setNumber(0);
        documentPageResult.setSize(10);
        documentPageResult.setTotalPages(1L);

        List<AIKnowledgeDocument> documents = generateMockAIKnowledgeDocumentData()
                .stream()
                .filter(document -> document.getName().equals("Test AIKnowledge Document 1"))
                .toList();
        documentPageResult.setStream(documents);
        documentPageResult.setTotalElements((long) documents.size());

        // name as search criteria
        AIKnowledgeDocumentSearchCriteria searchCriteria;
        searchCriteria = new AIKnowledgeDocumentSearchCriteria();
        searchCriteria.setName("Test AIKnowledge Document 1");

        mockServerClient.when(
                request().withPath(AI_KNOWLEDGE_DOCUMENT_SVC_INTERNAL_API_BASE_PATH + "/search")
                        .withMethod(HttpMethod.POST)
                        .withBody(JsonBody.json(searchCriteria)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response()
                        .withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(documentPageResult)));

        AIKnowledgeDocumentSearchCriteriaDTO searchCriteriaDTO = new AIKnowledgeDocumentSearchCriteriaDTO();
        searchCriteriaDTO.setName("Test AIKnowledge Document 1");

        var results = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(searchCriteriaDTO)
                .post("/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIKnowledgeDocumentSearchPageResultDTO.class);

        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.getTotalNumberOfResults());
        Assertions.assertEquals("Test AIKnowledge Document 1", results.getResults().get(0).getName());
    }

    @Test
    void searchAIKnowledgeDocument_Fails_400_Test() {

        var searchConstraintException = given()
                .when()
                .auth().oauth2(keycloakTestClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .extract()
                .as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(searchConstraintException);
        Assertions.assertEquals("CONSTRAINT_VIOLATIONS", searchConstraintException.getErrorCode());
        Assertions.assertEquals("must not be null", searchConstraintException.getInvalidParams().get(0).getMessage());
    }

    private List<AIKnowledgeDocument> generateMockAIKnowledgeDocumentData() {
        List<AIKnowledgeDocument> documents = new ArrayList<>();
        documents.add(createAIKnowledgeDocument(
                "81dd5932-872f-4f57-98bc-854c5ddb706a",
                "Test AIKnowledge Document 1",
                "210366c3-2ea6-432f-9443-7d9d2680d001",
                DocumentStatusType.NEW,
                0));
        documents.add(createAIKnowledgeDocument(
                "7dac2ba3-5f79-4161-85dc-5b5a3cfd5720",
                "Test AIKnowledge Document 2",
                "210366c3-2ea6-432f-9443-7d9d2680d001",
                DocumentStatusType.EMBEDDED,
                1));
        documents.add(createAIKnowledgeDocument(
                "8c102fd3-4323-4b94-8499-261beb35d976",
                "Test AIKnowledge Document 3",
                "210366c3-2ea6-432f-9443-7d9d2680d001",
                DocumentStatusType.PROCESSING,
                1));
        documents.add(createAIKnowledgeDocument(
                "b879a8f6-8eec-4e8a-b59a-30e48fed3eee",
                "Test AIKnowledge Document 4",
                "210366c3-2ea6-432f-9443-7d9d2680c178",
                DocumentStatusType.NEW,
                0));
        documents.add(createAIKnowledgeDocument(
                "34cc4539-bc72-47a3-9f61-e4327f748276",
                "Test AIKnowledge Document 5",
                "210366c3-2ea6-432f-9443-7d9d2680c1d5",
                DocumentStatusType.NEW,
                0));

        return documents;
    }

    private AIKnowledgeDocument createAIKnowledgeDocument(String id, String name, String documentRefId,
            DocumentStatusType status, int modificationCount) {
        AIKnowledgeDocument document = new AIKnowledgeDocument();
        document.setId(id);
        document.setDocumentRefId(documentRefId);
        document.setName(name);
        document.setStatus(status);
        document.setModificationCount(modificationCount);
        return document;
    }
}
