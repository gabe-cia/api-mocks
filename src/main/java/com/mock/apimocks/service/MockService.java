package com.mock.apimocks.service;

import com.mock.apimocks.exception.InternalServerErrorException;
import com.mock.apimocks.exception.ResourceNotFoundException;
import com.mock.apimocks.mechanism.ContextEngine;
import com.mock.apimocks.models.CallContext;
import com.mock.apimocks.models.vo.MockApi;
import com.mock.apimocks.models.vo.MockOperation;
import com.mock.apimocks.models.vo.MockScenario;
import com.mock.apimocks.models.vo.RegexOperation;
import com.mock.apimocks.repository.MockApiRepository;
import com.mock.apimocks.repository.MockOperationRepository;
import com.mock.apimocks.repository.RegexOperationRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MockService {
    // repository references
    private final MockApiRepository mockApiRepo;
    private final MockOperationRepository mockOpRepo;
    private final RegexOperationRepository regexRepo;

    /**
     * Default class constructor
     * <p/>
     * Used to inject dependencies
     *
     * @param mockApiRepo the {@link MockApiRepository} instance
     * @param mockOpRepo  the {@link MockOperationRepository} instance
     * @param regexRepo   the {@link RegexOperationRepository} instance
     */
    public MockService(MockApiRepository mockApiRepo, MockOperationRepository mockOpRepo,
                       RegexOperationRepository regexRepo) {
        this.mockApiRepo = mockApiRepo;
        this.mockOpRepo = mockOpRepo;
        this.regexRepo = regexRepo;
    }

    /**
     * Getting all mocks from database
     *
     * @return a list with all {@link MockApi} references
     */
    public List<MockApi> getAllMocks() {
        return mockApiRepo.findAll();
    }

    /**
     * Getting a mock by its identifier
     *
     * @param id the mock identifier
     * @return a {@link MockApi} object with the mock information
     */
    public MockApi getMockById(String id) {
        return mockApiRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Mock not found"));
    }

    /**
     * Creating the mock and persisting it to our database
     *
     * @param mock the mock details to be persisted
     * @return a String with the mock identifier
     */
    public String createMock(MockApi mock) {
        // adding mock identifier
        mock.setId(UUID.randomUUID().toString());

        // adding identifiers to each operation and
        // generating regular expressions for each operation
        enhanceOperations(mock);

        // saving the mock on our 3 non-relational tables
        // in order to boost our searches
        mockApiRepo.save(mock);
        mockOpRepo.saveAll(mock.getOperations());
        regexRepo.saveAll(mock.getOperations().stream()
                .map(RegexOperation::new).collect(Collectors.toList()));

        // returning the created mock identifier
        return mock.getId();
    }

    /**
     * Updating the mock on our database
     *
     * @param mock the mock details to be updated
     */
    public void updateMock(MockApi mock) {
        MockApi oldMock = mockApiRepo.findById(mock.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mock not found"));

        // filling new ids and regular expressions
        enhanceOperations(mock);

        // removing old references on search tables
        mockOpRepo.deleteAll(oldMock.getOperations());
        regexRepo.deleteAll(oldMock.getOperations().stream()
                .map(RegexOperation::new).collect(Collectors.toList()));

        // updating references
        mockApiRepo.save(mock);
        mockOpRepo.saveAll(mock.getOperations());
        regexRepo.saveAll(mock.getOperations().stream()
                .map(RegexOperation::new).collect(Collectors.toList()));
    }

    /**
     * Hard-delete a mock by its identifier
     *
     * @param id the mock identifier to be deleted
     */
    public void deleteMockById(String id) {
        MockApi mock = mockApiRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock not found"));

        // deleting mock on our search tables
        mockApiRepo.deleteById(mock.getId());
        mockOpRepo.deleteAll(mock.getOperations());
        regexRepo.deleteAll(mock.getOperations().stream()
                .map(RegexOperation::new).collect(Collectors.toList()));
    }

    /**
     * Get a scenario based on a call context.
     * <p/>
     * The call context contains the headers, query parameters, path parameters and request body.
     *
     * @param context the request context that contains the call information
     * @return a {@link MockScenario} with the response scenario based on the context
     * @throws ResourceNotFoundException whenever a scenario could not be found for the given context. This error
     *      scenario is not likely to happen because we have means to ensure that at least one scenario should be
     *      added
     */
    public MockScenario getScenario(CallContext context) {
        // first, we must all the operations registered on our database and try to find the correct one
        // by using its regex against the incoming URL, throwing an ResourceNotFoundException in case it was not found
        RegexOperation regexOp = regexRepo.findAll().stream()
                .filter(op -> ContextEngine.verifyUrl(context.getMethod() + context.getUrl(), op.getRegex()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Operation Not Found"));

        // getting the operation details on the operation search table
        MockOperation operation = mockOpRepo.findById(regexOp.getOperationId())
                .orElseThrow(() -> new InternalServerErrorException("The requested operation was found, however it " +
                        "was not possible load it properly. If the problem persist, call an administrator."));

        // with the correct operation, we should be able to fill the path parameter values on the context object
        context.setPathParams(ContextEngine
                .getPathParameters(operation.getFullPath(), operation.getRegex(), context.getUrl()));

        // trying to find the correct scenario for our mock operation
        Optional<MockScenario> scenario = operation.getScenarios().stream()
                .sorted(Comparator.comparing(MockScenario::getOrder))
                .filter(sc -> ContextEngine.evaluateCondition(context, sc.getConditions()))
                .findFirst();

        // in case no scenarios were found, we must return the default one
        if (!scenario.isPresent()) {
            scenario = operation.getScenarios().stream().filter(MockScenario::isDefault).findFirst();
        }
        return scenario.orElseThrow(() -> new ResourceNotFoundException("There's no default scenario on this operation."));
    }

    /**
     * Enhance the Mock operations by updating path and regex strings and filling it with a new identifier in case
     * it was a new instance and doesn't have one yet
     *
     * @param mock the mock with the operations to be enhanced
     */
    private void enhanceOperations(MockApi mock) {
        mock.getOperations().forEach(op -> {
            // updating path, full path and regex information
            String fullPath = mock.getBasePath() + "/" + op.getPath();
            op.setFullPath(ContextEngine.sanitizeUrl(fullPath));
            op.setRegex(ContextEngine.createUrlVerifier(fullPath));

            // creating a new identifier in case it doesn't have one
            if (op.getId() == null) {
                op.setId(UUID.randomUUID().toString());
            }
        });
    }
}
