package com.mock.apimocks.validator;

import com.mock.apimocks.models.vo.MockApi;
import com.mock.apimocks.models.vo.MockScenario;
import com.mock.apimocks.exception.UnprocessableEntityException;
import com.mock.apimocks.mechanism.ContextEngine;

/**
 * Validator class for MockApi Crud Controller
 *
 * @author gabriel.nascimento@sensedia
 * @version 1.0
 */
public class MockRequestValidator {
    /**
     * Default class constructor.
     * <p/>
     * It is set as private because this class only should have static methods
     */
    private MockRequestValidator() {
    }

    /**
     * Validate a given {@link MockApi} object
     *
     * @param mock the mock object to be validated
     */
    public static void validate(MockApi mock) {
        validateBasePath(mock);
        validateDuplicateOperations(mock);
        validateOperations(mock);
    }

    /**
     * Validate the basePath property value
     *
     * @param mock the mock object to be validated
     */
    private static void validateBasePath(MockApi mock) {
        if (!mock.getBasePath().startsWith("/")) {
            throw new UnprocessableEntityException("The 'basePath' should start with a slash");
        }
    }

    /**
     * Validate if there are no duplicated operations within the mock api
     *
     * @param mock the mock object to be validated
     */
    private static void validateDuplicateOperations(MockApi mock) {
        long totalOperations = mock.getOperations().size();
        long countMethodOperations = mock.getOperations().stream().map(op -> op.getMethod() +
                ContextEngine.sanitizeUrl(op.getPath())).distinct().count();
        if (totalOperations != countMethodOperations) {
            throw new UnprocessableEntityException("There could be no duplicate operations");
        }
    }

    /**
     * Validate each oe of the operation path value, default scenario and conditions properties.
     *
     * @param mock the mock object to be validated
     */
    private static void validateOperations(MockApi mock) {
        mock.getOperations().forEach(op -> {
            if (!op.getPath().startsWith("/")) {
                throw new UnprocessableEntityException("The 'path' of operation " + op.getPath() +
                        " should start with a slash");
            }

            if (op.getScenarios().stream().filter(MockScenario::isDefault).count() != 1) {
                throw new UnprocessableEntityException("The should be at least one, and only one, default " +
                        "scenario for operation: " + op.getPath());
            }

            if (op.getScenarios().stream().anyMatch(sc -> (sc.getConditions() == null || sc.getConditions().isEmpty())
                    && !sc.isDefault())) {
                throw new UnprocessableEntityException("Only scenarios flagged with 'default' property can have empty " +
                        "'conditions' properties on operation: " + op.getPath());
            }
        });
    }
}
