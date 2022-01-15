package com.mock.apimocks.controller;

import com.mock.apimocks.models.ErrorMessage;
import com.mock.apimocks.models.NoResponse;
import com.mock.apimocks.models.vo.MockApi;
import com.mock.apimocks.service.MockService;
import com.mock.apimocks.validator.MockRequestValidator;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This class contains the CRUD of Mock operations.
 * <p/>
 * Its purpose it's to create, update, get and delete mock scenarios based on API.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/mocks")
@Api(value = "/mocks", tags = "Mocks")
public class MockController {
    // Service definitions
    private final MockService mockService;

    /**
     * Default class constructor.
     * </p>
     * Used for dependency injections
     *
     * @param mockService the mock service object
     */
    public MockController(MockService mockService) {
        this.mockService = mockService;
    }

    /**
     * Getting all API Mocks on our database
     *
     * @return a {@link ResponseEntity} with a List of {@link MockApi}s with all API mocks
     */
    @ApiOperation(value = "Get all Mocks", notes = "Getting a list of all API Mocks", response = MockApi.class, responseContainer = "List", produces = "application/json")
    @RequestMapping(method = GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Mock not found", response = ErrorMessage.class)})
    public ResponseEntity<?> getAllMocks() {
        return ResponseEntity.ok(mockService.getAllMocks());
    }

    /**
     * Getting a given API Mock by its identifier
     *
     * @param id the API Mock identifier
     * @return a {@link ResponseEntity} with a {@link MockApi} object with the mock operation
     */
    @ApiOperation(value = "Get a Mock", notes = "Getting an API Mocks by its identifier", response = MockApi.class, produces = "application/json")
    @RequestMapping(method = GET, produces = "application/json", path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Mock not found", response = ErrorMessage.class)})
    public ResponseEntity<?> getMockById(@ApiParam(value = "Mock's Identifier", required = true) @PathVariable("id") String id) {
        return ResponseEntity.ok(mockService.getMockById(id));
    }

    /**
     * Create a new API Mock
     * <p/>
     * Since this method returns a 201 Created response, we are following the RFC and returning the Mock identifier on
     * a header called "location"
     *
     * @param mock the API Mock to be created
     * @return a {@link ResponseEntity} with the created identifier
     */
    @ApiOperation(value = "Create a Mock", notes = "Create a new API Mock", consumes = "application/json")
    @RequestMapping(method = POST, produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = NoResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Mock not found", response = ErrorMessage.class),
            @ApiResponse(code = 422, message = "Unprocessable Entity", response = ErrorMessage.class)})
    public ResponseEntity<?> createMock(@RequestBody @Valid MockApi mock) {
        MockRequestValidator.validate(mock);
        String id = mockService.createMock(mock);
        return ResponseEntity.created(URI.create("/mocks/" + id)).build();
    }

    /**
     * Updates a given API Mock
     *
     * @param id the identifier of the API to be updated
     * @param mock the API to be updated
     * @return a {@link ResponseEntity} with an OK status
     */
    @ApiOperation(value = "Update a Mock", notes = "Update a given API Mock", consumes = "application/json")
    @RequestMapping(method = PUT, consumes = "application/json", path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = NoResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Mock not found", response = ErrorMessage.class),
            @ApiResponse(code = 422, message = "Unprocessable Entity", response = ErrorMessage.class)})
    public ResponseEntity<?> updateMock(@ApiParam(value = "Mock's Identifier", required = true) @PathVariable("id") String id, @RequestBody @Valid MockApi mock) {
        MockRequestValidator.validate(mock);
        mock.setId(id);
        mockService.updateMock(mock);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a given Mock by its identifier.
     * <p/>
     * <b>This is a hard-delete and should be used wisely</b>
     *
     * @param id the API Mock identifier
     * @return a {@link ResponseEntity} with an OK status
     */
    @ApiOperation(value = "Delete a Mock", notes = "Delete a given API Mock")
    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = NoResponse.class),
            @ApiResponse(code = 404, message = "Mock not found", response = ErrorMessage.class)})
    public ResponseEntity<?> deleteMockById(@ApiParam(value = "Mock's Identifier", required = true) @PathVariable("id") String id) {
        mockService.deleteMockById(id);
        return ResponseEntity.noContent().build();
    }
}
