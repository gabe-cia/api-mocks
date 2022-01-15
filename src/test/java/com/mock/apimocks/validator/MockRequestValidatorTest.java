package com.mock.apimocks.validator;

import com.mock.apimocks.exception.UnprocessableEntityException;
import com.mock.apimocks.models.vo.MockApi;
import com.mock.apimocks.models.vo.MockOperation;
import com.mock.apimocks.models.vo.MockScenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MockRequestValidatorTest {
    private MockApi mock;

    private static final String VALID_API_BASE_PATH = "/test/api/v1/";
    private static final String INVALID_API_BASE_PATH = "test/api/v1/";
    private static final List<MockScenario> VALID_SCENARIOS = Arrays.asList(
            MockScenario.builder().isDefault(true).conditions(null).build(),
            MockScenario.builder().isDefault(false).conditions("$header.val == 1").build(),
            MockScenario.builder().isDefault(false).conditions("$header.val == 2").build(),
            MockScenario.builder().isDefault(false).conditions("$header.val == 3").build()
    );
    private static final List<MockScenario> DUPLICATED_DEFAULT_SCENARIOS = Arrays.asList(
            MockScenario.builder().isDefault(true).conditions(null).build(),
            MockScenario.builder().isDefault(true).conditions(null).build()
    );
    private static final List<MockScenario> NO_DEFAULT_SCENARIOS = Arrays.asList(
            MockScenario.builder().isDefault(false).conditions("$header.val == 1").build(),
            MockScenario.builder().isDefault(false).conditions("$header.val == 2").build()
    );
    private static final List<MockScenario> NO_DEFAULT_WITH_EMPTY_CONDITION_SCENARIOS = Arrays.asList(
            MockScenario.builder().isDefault(true).conditions(null).build(),
            MockScenario.builder().isDefault(false).conditions("").build()
    );
    private static final List<MockScenario> NO_DEFAULT_WITH_NULL_CONDITION_SCENARIOS = Arrays.asList(
            MockScenario.builder().isDefault(true).conditions(null).build(),
            MockScenario.builder().isDefault(false).conditions(null).build()
    );
    private static final List<MockOperation> VALID_OPERATIONS = Arrays.asList(
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").scenarios(VALID_SCENARIOS).build(),
            MockOperation.builder().method(HttpMethod.POST).path("/operationA").scenarios(VALID_SCENARIOS).build(),
            MockOperation.builder().method(HttpMethod.GET).path("/operationB").scenarios(VALID_SCENARIOS).build(),
            MockOperation.builder().method(HttpMethod.GET).path("/operationC").scenarios(VALID_SCENARIOS).build(),
            MockOperation.builder().method(HttpMethod.PUT).path("/operationD").scenarios(VALID_SCENARIOS).build());
    private static final List<MockOperation> DUPLICATED_OPERATIONS = Arrays.asList(
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").build(),
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").build());
    private static final List<MockOperation> INVALID_OPERATION_PATH = Collections.singletonList(
            MockOperation.builder().method(HttpMethod.PUT).path("operationA").build());
    private static final List<MockOperation> OPERATIONS_WITH_DUPLICATED_DEFAULT_SCENARIO = Collections.singletonList(
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").scenarios(DUPLICATED_DEFAULT_SCENARIOS).build());
    private static final List<MockOperation> OPERATIONS_WITH_NO_DEFAULT_SCENARIO = Collections.singletonList(
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").scenarios(NO_DEFAULT_SCENARIOS).build());
    private static final List<MockOperation> OPERATIONS_WITH_NO_DEFAULT_AND_EMPTY_CONDITION_SCENARIO = Collections.singletonList(
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").scenarios(NO_DEFAULT_WITH_EMPTY_CONDITION_SCENARIOS).build());
    private static final List<MockOperation> OPERATIONS_WITH_NO_DEFAULT_AND_NULL_CONDITION_SCENARIO = Collections.singletonList(
            MockOperation.builder().method(HttpMethod.GET).path("/operationA").scenarios(NO_DEFAULT_WITH_NULL_CONDITION_SCENARIOS).build());

    @Before
    public void setup() {
        this.mock = new MockApi();
    }

    // Testing validate
    @Test
    public void validateMethodWithSuccess() {
        givenWeHaveAValidBasePath();
        givenWeHaveAValidSetOfOperations();
        whenWeCallValidateMethod();
        thenWeHaveNoExceptions();
    }

    @Test(expected = UnprocessableEntityException.class)
    public void validateMethodWithInvalidBasePath() {
        givenWeHaveAnInvalidBasePath();
        givenWeHaveAValidSetOfOperations();
        whenWeCallValidateMethod();
        thenWeHaveAnUnprocessableEntityException();
    }

    @Test(expected = UnprocessableEntityException.class)
    public void validateMethodWithDuplicatedOperation() {
        givenWeHaveAValidBasePath();
        givenWeHaveASetWithDuplicatedOperations();
        whenWeCallValidateMethod();
        thenWeHaveAnUnprocessableEntityException();
    }

    @Test(expected = UnprocessableEntityException.class)
    public void validateMethodWithInvalidOperationPath() {
        givenWeHaveAValidBasePath();
        givenWeHaveAnOperationWithInvalidPath();
        whenWeCallValidateMethod();
        thenWeHaveAnUnprocessableEntityException();
    }

    @Test(expected = UnprocessableEntityException.class)
    public void validateMethodWithDuplicatedDefaultScenario() {
        givenWeHaveAValidBasePath();
        givenWeHaveMoreThanOneDefaultScenarioForAnOperation();
        whenWeCallValidateMethod();
        thenWeHaveAnUnprocessableEntityException();
    }

    @Test(expected = UnprocessableEntityException.class)
    public void validateMethodWithNoDefaultScenario() {
        givenWeHaveAValidBasePath();
        givenWeHaveAnOperationWithNoDefaultScenario();
        whenWeCallValidateMethod();
        thenWeHaveAnUnprocessableEntityException();
    }

    @Test(expected = UnprocessableEntityException.class)
    public void validateMethodWithNonDefaultScenarioAndEmptyCondition() {
        givenWeHaveAValidBasePath();
        givenWeHaveANonDefaultScenarioWithEmptyCondition();
        whenWeCallValidateMethod();
        thenWeHaveAnUnprocessableEntityException();
    }

    @Test(expected = UnprocessableEntityException.class)
    public void validateMethodWithNonDefaultScenarioAndNullCondition() {
        givenWeHaveAValidBasePath();
        givenWeHaveANonDefaultScenarioWithNullCondition();
        whenWeCallValidateMethod();
        thenWeHaveAnUnprocessableEntityException();
    }

    // Given methods
    private void givenWeHaveAValidBasePath() {
        mock.setBasePath(VALID_API_BASE_PATH);
    }

    private void givenWeHaveAnInvalidBasePath() {
        mock.setBasePath(INVALID_API_BASE_PATH);
    }

    private void givenWeHaveAValidSetOfOperations() {
        mock.setOperations(VALID_OPERATIONS);
    }

    private void givenWeHaveASetWithDuplicatedOperations() {
        mock.setOperations(DUPLICATED_OPERATIONS);
    }

    private void givenWeHaveAnOperationWithInvalidPath() {
        mock.setOperations(INVALID_OPERATION_PATH);
    }

    private void givenWeHaveMoreThanOneDefaultScenarioForAnOperation() {
        mock.setOperations(OPERATIONS_WITH_DUPLICATED_DEFAULT_SCENARIO);
    }

    private void givenWeHaveAnOperationWithNoDefaultScenario() {
        mock.setOperations(OPERATIONS_WITH_NO_DEFAULT_SCENARIO);
    }

    private void givenWeHaveANonDefaultScenarioWithEmptyCondition() {
        mock.setOperations(OPERATIONS_WITH_NO_DEFAULT_AND_EMPTY_CONDITION_SCENARIO);
    }

    private void givenWeHaveANonDefaultScenarioWithNullCondition() {
        mock.setOperations(OPERATIONS_WITH_NO_DEFAULT_AND_NULL_CONDITION_SCENARIO);
    }

    // When methods
    private void whenWeCallValidateMethod() {
        MockRequestValidator.validate(this.mock);
    }

    // Then methods
    private void thenWeHaveNoExceptions() {
        // asserted at test scope
    }

    private void thenWeHaveAnUnprocessableEntityException() {
        // asserted at test scope
    }
}
