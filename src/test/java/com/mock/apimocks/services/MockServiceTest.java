package com.mock.apimocks.services;

import com.mock.apimocks.enums.ContentType;
import com.mock.apimocks.service.MockService;
import com.mock.apimocks.exception.InternalServerErrorException;
import com.mock.apimocks.exception.ResourceNotFoundException;
import com.mock.apimocks.models.CallContext;
import com.mock.apimocks.models.vo.MockApi;
import com.mock.apimocks.models.vo.MockOperation;
import com.mock.apimocks.models.vo.MockScenario;
import com.mock.apimocks.models.vo.RegexOperation;
import com.mock.apimocks.repository.MockApiRepository;
import com.mock.apimocks.repository.MockOperationRepository;
import com.mock.apimocks.repository.RegexOperationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MockServiceTest {
    private List<MockApi> mocks;
    private MockApi mock;
    private String id;
    private MockScenario scenario;
    private CallContext context;

    private static final String VALID_IDENTIFIER = "valid_identifier";
    private static final String INVALID_IDENTIFIER = "invalid_identifier";
    private static final List<MockApi> VALID_MOCK_LIST = Arrays.asList(
            MockApi.builder().id("1").build(),
            MockApi.builder().id("2").build(),
            MockApi.builder().id("3").build()
    );
    private static final List<MockScenario> VALID_SCENARIOS = Arrays.asList(
            MockScenario.builder().isDefault(true).order(3).build(),
            MockScenario.builder().isDefault(false).order(2).conditions("$header.header1 == 'A'").build(),
            MockScenario.builder().isDefault(false).order(1).conditions("$header.header1 == 'B'").build()
    );
    private static final List<MockScenario> NO_DEFAULT_SCENARIOS = Collections.singletonList(
            MockScenario.builder().isDefault(false).build()
    );
    private static final MockApi VALID_MOCK = MockApi.builder()
            .id(VALID_IDENTIFIER)
            .basePath("/test/api/v1")
            .name("Test")
            .operations(Arrays.asList(
                    MockOperation.builder().id("1").path("/operationA").method(HttpMethod.GET).scenarios(VALID_SCENARIOS).build(),
                    MockOperation.builder().id("2").path("/operationA").method(HttpMethod.POST).scenarios(VALID_SCENARIOS).build(),
                    MockOperation.builder().id("3").path("/operationB").method(HttpMethod.GET).scenarios(VALID_SCENARIOS).build(),
                    MockOperation.builder().path("/operationC").method(HttpMethod.PUT).scenarios(VALID_SCENARIOS).build()
            ))
            .build();
    private static final MockApi MOCK_WITH_INVALID_ID = MockApi.builder().id(INVALID_IDENTIFIER).build();
    private static final CallContext VALID_CALL_CONTEXT = CallContext.builder()
            .url("/test/operation/123/b")
            .method("GET")
            .body("test=123")
            .contentType(ContentType.URL_ENCODED)
            .parsedBody(new HashMap<String, String>() {
                {
                    put("valA", "1");
                    put("valB", "2");
                }
            })
            .headers(new HashMap<String, String>() {
                {
                    put("header1", "A");
                    put("header2", "B");
                    put("header3", "C");
                }
            })
            .queryParams(new HashMap<String, String>() {
                {
                    put("queryA", "1");
                    put("queryB", "2");
                    put("queryC", "3");
                }
            })
            .build();
    private static final List<RegexOperation> VALID_REGEX_OP_LIST = Arrays.asList(
            new RegexOperation("GET/test/operation/([^\\\\/]+)/a", "1"),
            new RegexOperation("POST/test/operation/([^\\\\/]+)/a", "2"),
            new RegexOperation("GET/test/operation/([^\\\\/]+)/b", "3"),
            new RegexOperation("PUT/test/operation/([^\\\\/]+)/c", "4"));
    private static final MockOperation VALID_OPERATION_WITH_DEFAULT = MockOperation.builder()
            .id("3")
            .path("/operation/{id}/b")
            .fullPath("/test/operation/{id}/b")
            .regex("(/test/operation/([^\\\\/]+)/b)")
            .method(HttpMethod.GET)
            .scenarios(VALID_SCENARIOS)
            .build();
    private static final MockOperation VALID_OPERATION_WITH_TRUE_CONDITION = MockOperation.builder()
            .id("3")
            .path("/operation/{id}/b")
            .fullPath("/test/operation/{id}/b")
            .regex("(/test/operation/([^\\\\/]+)/b)")
            .method(HttpMethod.GET)
            .scenarios(VALID_SCENARIOS)
            .build();
    private static final MockOperation OPERATION_WITH_NO_DEFAULT_SCENARIO = MockOperation.builder()
            .id("3")
            .path("/operation/{id}/b")
            .fullPath("/test/operation/{id}/b")
            .regex("(/test/operation/([^\\\\/]+)/b)")
            .method(HttpMethod.GET)
            .scenarios(NO_DEFAULT_SCENARIOS)
            .build();

    @Mock
    private MockApiRepository mockApiRepo;

    @Mock
    private MockOperationRepository mockOpRepo;

    @Mock
    private RegexOperationRepository regexRepo;

    @InjectMocks
    private MockService service;

    // Testing getAllMocks
    @Test
    public void getAllMocksWithSuccess() {
        givenMockApiRepoFindAllReturnsAListOfMocks();
        whenWeCallGetAllMocks();
        thenWeExpectAListOfMocks();
    }

    // Testing getMockById
    @Test
    public void getMockByIdWithSuccess() {
        givenWeHaveAValidMockId();
        givenMockApiRepoFindByIdReturnsAMock();
        whenWeCallGetMockById();
        thenWeExpectAMock();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getMockByIdWithInvalidId() {
        givenWeHaveAnInvalidMockId();
        givenMockApiRepoFindByIdReturnsNoMock();
        whenWeCallGetMockById();
        thenWeExpectAResourceNotFoundException();
    }

    // Testing createMock
    @Test
    public void createMockWithSuccess() {
        givenWeHaveAValidMockApiToBeSaved();
        whenWeCallCreateMock();
        thenWeExpectTheCreatedIdToBeReturned();
        thenWeExpectTheMockApiRepoSaveToBeExecuted();
        thenWeExpectTheMockOpRepoSaveAllToBeExecuted();
        thenWeExpectTheRegexRepoSaveAllToBeExecuted();
    }

    // Testing updateMock
    @Test
    public void updateMockWithSuccess() {
        givenWeHaveAValidMockApiToBeSaved();
        givenMockApiRepoFindByIdReturnsAMock();
        whenWeCallUpdateMock();
        thenWeExpectTheMockOpRepoDeleteAllToBeExecuted();
        thenWeExpectTheRegexRepoDeleteAllToBeExecuted();
        thenWeExpectTheMockApiRepoSaveToBeExecuted();
        thenWeExpectTheMockOpRepoSaveAllToBeExecuted();
        thenWeExpectTheRegexRepoSaveAllToBeExecuted();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateMockWithInvalidId() {
        givenWeHaveAMockApiWithInvalidId();
        givenMockApiRepoFindByIdReturnsNoMock();
        whenWeCallUpdateMock();
        thenWeExpectTheMockOpRepoDeleteAllToNotBeExecuted();
        thenWeExpectTheRegexRepoDeleteAllToNotBeExecuted();
        thenWeExpectTheMockApiRepoSaveToNotBeExecuted();
        thenWeExpectTheMockOpRepoSaveAllToNotBeExecuted();
        thenWeExpectTheRegexRepoSaveAllToNotBeExecuted();
        thenWeExpectAResourceNotFoundException();
    }

    // Testing deleteMockById
    @Test
    public void deleteMockWithSuccess() {
        givenWeHaveAValidMockId();
        givenMockApiRepoFindByIdReturnsAMock();
        whenWeCallDeleteMockById();
        thenWeExpectTheMockApiRepoDeleteByIdToBeExecuted();
        thenWeExpectTheMockOpRepoDeleteAllToBeExecuted();
        thenWeExpectTheRegexRepoDeleteAllToBeExecuted();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteMockWithInvalidId() {
        givenWeHaveAnInvalidMockId();
        givenMockApiRepoFindByIdReturnsNoMock();
        whenWeCallDeleteMockById();
        thenWeExpectTheMockApiRepoDeleteByIdToNotBeExecuted();
        thenWeExpectTheMockOpRepoDeleteAllToNotBeExecuted();
        thenWeExpectTheRegexRepoDeleteAllToNotBeExecuted();
    }

    // Testing getScenario
    @Test
    public void getScenarioReturnsAValidConditionalScenario() {
        givenWeHaveAValidCallContext();
        givenTheRegexRepoFindAllReturnsAValidListOfRegexMocks();
        givenMockOpRepoFindByIdReturnsAValidOperationWithDefaultCondition();
        whenWeCallGetScenario();
        thenWeExpectAValidMockScenario();
    }

    @Test
    public void getScenarioReturnsAValidDefaultScenario() {
        givenWeHaveAValidCallContext();
        givenTheRegexRepoFindAllReturnsAValidListOfRegexMocks();
        givenMockOpRepoFindByIdReturnsAValidOperationWithTrueCondition();
        whenWeCallGetScenario();
        thenWeExpectAValidMockScenario();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getScenarioWithNoRegisteredOperation() {
        givenWeHaveAValidCallContext();
        givenTheRegexRepoFindAllReturnsAnEmptyListOfRegexMocks();
        whenWeCallGetScenario();
        thenWeExpectAResourceNotFoundException();
    }

    @Test(expected = InternalServerErrorException.class)
    public void getScenarioWithInvalidOperationOnRegex() {
        givenWeHaveAValidCallContext();
        givenTheRegexRepoFindAllReturnsAValidListOfRegexMocks();
        whenWeCallGetScenario();
        thenWeExpectAInternalServerErrorException();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getScenarioWithNoDefaultScenarioRegistered() {
        givenWeHaveAValidCallContext();
        givenTheRegexRepoFindAllReturnsAValidListOfRegexMocks();
        givenMockOpRepoFindByIdReturnsAnOperationWithNoDefaultScenario();
        whenWeCallGetScenario();
        thenWeExpectAResourceNotFoundException();
    }

    // Given methods
    private void givenMockApiRepoFindAllReturnsAListOfMocks() {
        doReturn(VALID_MOCK_LIST).when(mockApiRepo).findAll();
    }

    private void givenWeHaveAValidMockId() {
        this.id = VALID_IDENTIFIER;
    }

    private void givenWeHaveAnInvalidMockId() {
        this.id = INVALID_IDENTIFIER;
    }

    private void givenMockApiRepoFindByIdReturnsAMock() {
        doReturn(Optional.of(VALID_MOCK)).when(mockApiRepo).findById(VALID_IDENTIFIER);
    }

    private void givenMockApiRepoFindByIdReturnsNoMock() {
        doReturn(Optional.empty()).when(mockApiRepo).findById(INVALID_IDENTIFIER);
    }

    private void givenWeHaveAValidMockApiToBeSaved() {
        this.mock = VALID_MOCK;
        this.mock.setId(VALID_IDENTIFIER);
    }

    private void givenWeHaveAMockApiWithInvalidId() {
        this.mock = MOCK_WITH_INVALID_ID;
    }

    private void givenWeHaveAValidCallContext() {
        this.context = VALID_CALL_CONTEXT;
    }

    private void givenTheRegexRepoFindAllReturnsAValidListOfRegexMocks() {
        doReturn(VALID_REGEX_OP_LIST).when(regexRepo).findAll();
    }

    private void givenMockOpRepoFindByIdReturnsAValidOperationWithDefaultCondition() {
        doReturn(Optional.of(VALID_OPERATION_WITH_DEFAULT)).when(mockOpRepo).findById("3");
    }

    private void givenMockOpRepoFindByIdReturnsAValidOperationWithTrueCondition() {
        doReturn(Optional.of(VALID_OPERATION_WITH_TRUE_CONDITION)).when(mockOpRepo).findById("3");
    }

    private void givenTheRegexRepoFindAllReturnsAnEmptyListOfRegexMocks() {
        doReturn(Collections.EMPTY_LIST).when(regexRepo).findAll();
    }

    private void givenMockOpRepoFindByIdReturnsAnOperationWithNoDefaultScenario() {
        doReturn(Optional.of(OPERATION_WITH_NO_DEFAULT_SCENARIO)).when(mockOpRepo).findById("3");
    }

    // When methods
    private void whenWeCallGetAllMocks() {
        this.mocks = service.getAllMocks();
    }

    private void whenWeCallGetMockById() {
        this.mock = service.getMockById(this.id);
    }

    private void whenWeCallCreateMock() {
        this.id = service.createMock(this.mock);
    }

    private void whenWeCallUpdateMock() {
        service.updateMock(mock);
    }

    private void whenWeCallDeleteMockById() {
        service.deleteMockById(this.id);
    }

    private void whenWeCallGetScenario() {
        this.scenario = service.getScenario(this.context);
    }

    // Then methods
    private void thenWeExpectAListOfMocks() {
        assertEquals(VALID_MOCK_LIST, this.mocks);
    }

    private void thenWeExpectAMock() {
        assertEquals(VALID_MOCK, this.mock);
    }

    private void thenWeExpectAResourceNotFoundException() {
        // asserted at test scope
    }

    private void thenWeExpectTheCreatedIdToBeReturned() {
        assertNotNull(this.id);
    }

    private void thenWeExpectTheMockApiRepoSaveToBeExecuted() {
        verify(mockApiRepo, times(1)).save(any(MockApi.class));
    }

    private void thenWeExpectTheMockOpRepoSaveAllToBeExecuted() {
        verify(mockOpRepo, times(1)).saveAll(anyCollection());
    }

    private void thenWeExpectTheRegexRepoSaveAllToBeExecuted() {
        verify(regexRepo, times(1)).saveAll(anyCollection());
    }

    private void thenWeExpectTheMockApiRepoDeleteByIdToBeExecuted() {
        verify(mockApiRepo, times(1)).deleteById(anyString());
    }

    private void thenWeExpectTheMockOpRepoDeleteAllToBeExecuted() {
        verify(mockOpRepo, times(1)).deleteAll(anyCollection());
    }

    private void thenWeExpectTheRegexRepoDeleteAllToBeExecuted() {
        verify(regexRepo, times(1)).deleteAll(anyCollection());
    }

    private void thenWeExpectTheMockApiRepoSaveToNotBeExecuted() {
        verify(mockApiRepo, times(0)).save(any(MockApi.class));
    }

    private void thenWeExpectTheMockOpRepoSaveAllToNotBeExecuted() {
        verify(mockOpRepo, times(0)).saveAll(anyCollection());
    }

    private void thenWeExpectTheRegexRepoSaveAllToNotBeExecuted() {
        verify(regexRepo, times(0)).saveAll(anyCollection());
    }

    private void thenWeExpectTheMockApiRepoDeleteByIdToNotBeExecuted() {
        verify(mockApiRepo, times(0)).deleteById(anyString());
    }

    private void thenWeExpectTheMockOpRepoDeleteAllToNotBeExecuted() {
        verify(mockOpRepo, times(0)).deleteAll(anyCollection());
    }

    private void thenWeExpectTheRegexRepoDeleteAllToNotBeExecuted() {
        verify(regexRepo, times(0)).deleteAll(anyCollection());
    }

    private void thenWeExpectAInternalServerErrorException() {
        // asserted at test scope
    }

    private void thenWeExpectAValidMockScenario() {
        assertNotNull(this.scenario);
    }
}
