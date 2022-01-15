package com.mock.apimocks.controller;

import com.mock.apimocks.service.MockService;
import com.mock.apimocks.MvcControllerTestable;
import com.mock.apimocks.exception.ResourceNotFoundException;
import com.mock.apimocks.models.vo.MockApi;
import com.mock.apimocks.models.vo.MockOperation;
import com.mock.apimocks.models.vo.MockScenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MockControllerTest extends MvcControllerTestable<MockController> {
    private MvcResult response;

    private String id;
    private MockApi mock;

    private static final String VALID_MOCK_ID = "1";
    private static final String INVALID_MOCK_ID = "invalid_id";
    private static final String CREATED_MOCK_ID = "1";
    private static final List<MockApi> VALID_MOCK_LIST = Arrays.asList(
            MockApi.builder().id("1").build(),
            MockApi.builder().id("2").build(),
            MockApi.builder().id("3").build());
    private static final List<MockScenario> VALID_SCENARIOS = Arrays.asList(
            MockScenario.builder().name("S1").order(4).httpCode(200).isDefault(true).conditions(null).build(),
            MockScenario.builder().name("S2").order(3).httpCode(400).isDefault(false).conditions("$header.val == 1").build(),
            MockScenario.builder().name("S3").order(2).httpCode(500).isDefault(false).conditions("$header.val == 2").build(),
            MockScenario.builder().name("S4").order(1).httpCode(201).isDefault(false).conditions("$header.val == 3").build());
    private static final List<MockScenario> EMPTY_SCENARIO_NAME = Collections.singletonList(
            MockScenario.builder().order(4).httpCode(200).isDefault(true).conditions(null).build());
    private static final List<MockScenario> EMPTY_SCENARIO_HTTP_CODE = Collections.singletonList(
            MockScenario.builder().name("S1").order(4).isDefault(true).conditions(null).build());
    private static final List<MockScenario> EMPTY_SCENARIO_ORDER = Collections.singletonList(
            MockScenario.builder().name("S1").httpCode(200).isDefault(true).conditions(null).build());
    private static final List<MockScenario> DUPLICATED_DEFAULT_SCENARIO = Arrays.asList(
            MockScenario.builder().name("S1").order(4).httpCode(200).isDefault(true).conditions(null).build(),
            MockScenario.builder().name("S4").order(1).httpCode(201).isDefault(true).conditions(null).build());
    private static final List<MockOperation> VALID_OPERATIONS = Arrays.asList(
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").scenarios(VALID_SCENARIOS).build(),
            MockOperation.builder().method(HttpMethod.POST).path("/operationA").scenarios(VALID_SCENARIOS).build(),
            MockOperation.builder().method(HttpMethod.PUT).path("/operationC").scenarios(VALID_SCENARIOS).build());
    private static final List<MockOperation> EMPTY_OPERATION_METHOD = Collections.singletonList(
            MockOperation.builder().path("/operationA").scenarios(VALID_SCENARIOS).build());
    private static final List<MockOperation> EMPTY_OPERATION_PATH = Collections.singletonList(
            MockOperation.builder().method(HttpMethod.GET).scenarios(VALID_SCENARIOS).build());
    private static final List<MockOperation> EMPTY_OPERATION_SCENARIO_NAME = Collections.singletonList(
            MockOperation.builder().path("/operationA").method(HttpMethod.GET).scenarios(EMPTY_SCENARIO_NAME).build());
    private static final List<MockOperation> EMPTY_OPERATION_SCENARIO_HTTP_CODE = Collections.singletonList(
            MockOperation.builder().path("/operationA").method(HttpMethod.GET).scenarios(EMPTY_SCENARIO_HTTP_CODE).build());
    private static final List<MockOperation> EMPTY_OPERATION_SCENARIO_ORDER = Collections.singletonList(
            MockOperation.builder().path("/operationA").method(HttpMethod.GET).scenarios(EMPTY_SCENARIO_ORDER).build());
    private static final List<MockOperation> DUPLICATED_OPERATION_DEFAULT_SCENARIO = Collections.singletonList(
            MockOperation.builder().path("/operationA").method(HttpMethod.GET).scenarios(DUPLICATED_DEFAULT_SCENARIO).build());
    private static final MockApi VALID_MOCK = MockApi.builder()
            .id("1")
            .name("Test")
            .basePath("/test/api/v1/")
            .operations(VALID_OPERATIONS)
            .build();
    private static final MockApi EMPTY_NAME_MOCK = MockApi.builder()
            .id("1")
            .basePath("/test/api/v1/")
            .operations(VALID_OPERATIONS)
            .build();
    private static final MockApi EMPTY_BASEPATH_MOCK = MockApi.builder()
            .id("1")
            .name("Test")
            .operations(VALID_OPERATIONS)
            .build();
    private static final MockApi EMPTY_OPERATION_METHOD_MOCK = MockApi.builder()
            .id("1").name("Test").basePath("/test/api/v1/")
            .operations(EMPTY_OPERATION_METHOD)
            .build();
    private static final MockApi EMPTY_OPERATION_PATH_MOCK = MockApi.builder()
            .id("1").name("Test").basePath("/test/api/v1/")
            .operations(EMPTY_OPERATION_PATH)
            .build();
    private static final MockApi EMPTY_SCENARIO_NAME_MOCK = MockApi.builder()
            .id("1").name("Test").basePath("/test/api/v1/")
            .operations(EMPTY_OPERATION_SCENARIO_NAME)
            .build();
    private static final MockApi EMPTY_SCENARIO_HTTP_CODE_MOCK = MockApi.builder()
            .id("1").name("Test").basePath("/test/api/v1/")
            .operations(EMPTY_OPERATION_SCENARIO_HTTP_CODE)
            .build();
    private static final MockApi EMPTY_SCENARIO_ORDER_MOCK = MockApi.builder()
            .id("1").name("Test").basePath("/test/api/v1/")
            .operations(EMPTY_OPERATION_SCENARIO_ORDER)
            .build();
    private static final MockApi DUPLICATED_DEFAULT_SCENARIO_MOCK = MockApi.builder()
            .id("1").name("Test").basePath("/test/api/v1/")
            .operations(DUPLICATED_OPERATION_DEFAULT_SCENARIO)
            .build();

    @InjectMocks
    private MockController controller;

    @Mock
    private MockService service;

    @Before
    public void setup() {
        initializeMvc(controller);
    }

    // Testing getAllMocks
    @Test
    public void getAllMocksWithSuccess() throws Exception {
        givenServiceGetAllMocksReturnsAListOfMocks();
        whenWeCallGetAllMocks();
        thenWeExpectAnOkStatus();
    }

    // Testing getMockById
    @Test
    public void getMockByIdWithSuccess() throws Exception {
        givenWeHaveAValidMockId();
        givenServiceGetMockByIdReturnsAListOfMocks();
        whenWeCallGetMockById();
        thenWeExpectAnOkStatus();
    }

    @Test
    public void getMockByIdWithInvalidId() throws Exception {
        givenWeHaveAnInvalidMockId();
        givenServiceGetMockByIdThrowsAResourceNotFoundException();
        whenWeCallGetMockById();
        thenWeExpectANotFoundStatus();
    }

    // Testing createMock
    @Test
    public void createMockWithSuccess() throws Exception {
        givenWeHaveAValidMock();
        givenServiceCreateMockReturnsTheCreatedId();
        whenWeCallCreateMock();
        thenWeExpectTheHATEOASLink();
        thenWeExpectAnCreatedStatus();
    }

    @Test
    public void createMockWithEmptyApiName() throws Exception {
        givenWeHaveAMockWithEmptyApiName();
        whenWeCallCreateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void createMockWithEmptyApiBasePath() throws Exception {
        givenWeHaveAMockWithEmptyApiBasePath();
        whenWeCallCreateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void createMockWithEmptyApiOperationMethod() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationMethod();
        whenWeCallCreateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void createMockWithEmptyApiOperationPath() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationPath();
        whenWeCallCreateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void createMockWithEmptyApiOperationScenarioName() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationScenarioName();
        whenWeCallCreateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void createMockWithEmptyApiOperationScenarioHttpCode() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationScenarioHttpCode();
        whenWeCallCreateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void createMockWithEmptyApiOperationScenarioOrder() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationScenarioOrder();
        whenWeCallCreateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void createMockWithMoreThanOneDefaultScenario() throws Exception {
        givenWeHaveAMockWithDuplicatedApiOperationDefaultScenario();
        whenWeCallCreateMock();
        thenWeExpectAnUnprocessableEntity();
    }

    // Testing updateMock
    @Test
    public void updateMockWithSuccess() throws Exception {
        givenWeHaveAValidMock();
        givenWeHaveAValidMockId();
        givenServiceUpdateMockExecutesWithSuccess();
        whenWeCallUpdateMock();
        thenWeExpectANoContentStatus();
    }

    @Test
    public void updateMockWithInvalidId() throws Exception {
        givenWeHaveAValidMock();
        givenWeHaveAnInvalidMockId();
        givenServiceUpdateMockThrowsResourceNotFoundException();
        whenWeCallUpdateMock();
        thenWeExpectANotFoundStatus();
    }

    @Test
    public void updateMockWithEmptyApiName() throws Exception {
        givenWeHaveAMockWithEmptyApiName();
        givenWeHaveAnInvalidMockId();
        whenWeCallUpdateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void updateMockWithEmptyApiBasePath() throws Exception {
        givenWeHaveAMockWithEmptyApiBasePath();
        givenWeHaveAnInvalidMockId();
        whenWeCallUpdateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void updateMockWithEmptyApiOperationMethod() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationMethod();
        givenWeHaveAnInvalidMockId();
        whenWeCallUpdateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void updateMockWithEmptyApiOperationPath() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationPath();
        givenWeHaveAnInvalidMockId();
        whenWeCallUpdateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void updateMockWithEmptyApiOperationScenarioName() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationScenarioName();
        givenWeHaveAnInvalidMockId();
        whenWeCallUpdateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void updateMockWithEmptyApiOperationScenarioHttpCode() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationScenarioHttpCode();
        givenWeHaveAnInvalidMockId();
        whenWeCallUpdateMock();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void updateMockWithEmptyApiOperationScenarioOrder() throws Exception {
        givenWeHaveAMockWithEmptyApiOperationScenarioOrder();
        givenWeHaveAnInvalidMockId();
        whenWeCallUpdateMock();
        thenWeExpectABadRequestStatus();
    }

    // Testing deleteMockById
    @Test
    public void deleteMockByIdWithSuccess() throws Exception {
        givenWeHaveAValidMockId();
        givenServiceDeleteMockByIdExecutesWithSuccess();
        whenWeCallDeleteMockById();
        thenWeExpectANoContentStatus();
    }

    @Test
    public void deleteMockByIdWithInvalidId() throws Exception {
        givenWeHaveAnInvalidMockId();
        givenServiceDeleteMockByIdThrowsResourceNotFoundException();
        whenWeCallDeleteMockById();
        thenWeExpectANotFoundStatus();
    }

    // Given methods
    private void givenServiceGetAllMocksReturnsAListOfMocks() {
        doReturn(VALID_MOCK_LIST).when(service).getAllMocks();
    }

    private void givenWeHaveAValidMockId() {
        this.id = VALID_MOCK_ID;
    }

    private void givenWeHaveAnInvalidMockId() {
        this.id = INVALID_MOCK_ID;
    }

    private void givenServiceGetMockByIdReturnsAListOfMocks() {
        doReturn(VALID_MOCK).when(service).getMockById(VALID_MOCK_ID);
    }

    private void givenServiceGetMockByIdThrowsAResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Invalid Id")).when(service).getMockById(INVALID_MOCK_ID);
    }

    private void givenWeHaveAValidMock() {
        this.mock = VALID_MOCK;
    }

    private void givenServiceCreateMockReturnsTheCreatedId() {
        doReturn(CREATED_MOCK_ID).when(service).createMock(any(MockApi.class));
    }

    private void givenWeHaveAMockWithEmptyApiName() {
        this.mock = EMPTY_NAME_MOCK;
    }

    private void givenWeHaveAMockWithEmptyApiBasePath() {
        this.mock = EMPTY_BASEPATH_MOCK;
    }

    private void givenWeHaveAMockWithEmptyApiOperationMethod() {
        this.mock = EMPTY_OPERATION_METHOD_MOCK;
    }

    private void givenWeHaveAMockWithEmptyApiOperationPath() {
        this.mock = EMPTY_OPERATION_PATH_MOCK;
    }

    private void givenWeHaveAMockWithEmptyApiOperationScenarioName() {
        this.mock = EMPTY_SCENARIO_NAME_MOCK;
    }

    private void givenWeHaveAMockWithEmptyApiOperationScenarioHttpCode() {
        this.mock = EMPTY_SCENARIO_HTTP_CODE_MOCK;
    }

    private void givenWeHaveAMockWithEmptyApiOperationScenarioOrder() {
        this.mock = EMPTY_SCENARIO_ORDER_MOCK;
    }

    private void givenWeHaveAMockWithDuplicatedApiOperationDefaultScenario() {
        this.mock = DUPLICATED_DEFAULT_SCENARIO_MOCK;
    }

    private void givenServiceUpdateMockExecutesWithSuccess() {
        doNothing().when(service).updateMock(any(MockApi.class));
    }

    private void givenServiceUpdateMockThrowsResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Invalid id")).when(service).updateMock(any(MockApi.class));
    }

    private void givenServiceDeleteMockByIdExecutesWithSuccess() {
        doNothing().when(service).deleteMockById(VALID_MOCK_ID);
    }

    private void givenServiceDeleteMockByIdThrowsResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Invalid id")).when(service).deleteMockById(INVALID_MOCK_ID);
    }

    // When methods
    private void whenWeCallGetAllMocks() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.get("/mocks")
                .accept(MediaType.APPLICATION_JSON)).andReturn();
    }

    private void whenWeCallGetMockById() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.get("/mocks/{id}", this.id)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
    }

    private void whenWeCallCreateMock() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.post("/mocks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(toJson(this.mock))).andReturn();
    }

    private void whenWeCallUpdateMock() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.put("/mocks/{id}", this.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(this.mock))).andReturn();
    }

    private void whenWeCallDeleteMockById() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.delete("/mocks/{id}", this.id)).andReturn();
    }

    // Then methods
    private void thenWeExpectTheHATEOASLink() {
        assertEquals("/mocks/" + VALID_MOCK_ID, this.response.getResponse().getHeader("Location"));
    }

    private void thenWeExpectAnCreatedStatus() {
        assertEquals(HttpStatus.CREATED.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectAnOkStatus() {
        assertEquals(HttpStatus.OK.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectANoContentStatus() {
        assertEquals(HttpStatus.NO_CONTENT.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectANotFoundStatus() {
        assertEquals(HttpStatus.NOT_FOUND.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectABadRequestStatus() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectAnUnprocessableEntity() {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), this.response.getResponse().getStatus());
    }
}
