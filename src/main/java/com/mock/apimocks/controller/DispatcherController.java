package com.mock.apimocks.controller;

import com.mock.apimocks.contants.HeaderName;
import com.mock.apimocks.enums.ContentType;
import com.mock.apimocks.models.vo.MockScenario;
import com.mock.apimocks.service.MockService;
import com.mock.apimocks.mechanism.RequestBodyParser;
import com.mock.apimocks.models.CallContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This class represents the Mock Dispatcher Controller.
 * <p/>
 * This class contains a single operations that listens to all requests (but the /mock operations) in order to respond
 * The correct mock responses based on the request context.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
@RestController
@RequestMapping
public class DispatcherController {
    // Service definitions
    private final MockService mockService;

    /**
     * Default class constructor.
     * </p>
     * Used for dependency injections
     *
     * @param mockService the mock service object
     */
    public DispatcherController(MockService mockService) {
        this.mockService = mockService;
    }

    /**
     * This is the main Mock Dispatcher.
     * </p>
     * This method is in charge to receive the requests from all resources but /mock operations
     * in order to get the mocked responses.
     *
     * @param request the {@link HttpServletRequest} object inject by Spring on each request with the request
     *                information such as headers, body, query parameters, etc.
     * @return a {@link ResponseEntity} object with the mocked response based on the request scenario sent.
     */
    @RequestMapping(method = {GET, POST, PUT, PATCH, DELETE}, path = "/**")
    public ResponseEntity<?> wildcardApiCall(HttpServletRequest request) throws IOException {
        // creating call context, so we can get the correct response scenario
        CallContext context = createContext(request);

        // getting the mock scenario based on the ongoing call context
        MockScenario scenario = mockService.getScenario(context);

        // building the response based on the scenario
        return ResponseEntity
                .status(scenario.getHttpCode())
                .headers(buildHttpHeaders(scenario))
                .body(scenario.getBody());
    }

    /**
     * Building the {@link HttpHeaders} object with the response headers
     *
     * @param scenario the mock scenario with the headers
     * @return a {@link HttpHeaders} object with the response headers
     */
    private HttpHeaders buildHttpHeaders(MockScenario scenario) {
        HttpHeaders headers = new HttpHeaders();
        if(scenario.getHeaders() != null) {
            headers.setAll(scenario.getHeaders());
        }
        if (scenario.getContentType() != null) {
            headers.add(HeaderName.CONTENT_TYPE, scenario.getContentType().mime());
        }
        return headers;
    }

    /**
     * Creating call context with the request information
     *
     * @param request the HTTP request object
     * @return a {@link CallContext} object with the context
     * @throws IOException whenever the request was invalid
     */
    private CallContext createContext(HttpServletRequest request) throws IOException {
        // getting Content-Type
        String contentTypeHeader = request.getHeader(HeaderName.CONTENT_TYPE);
        ContentType contentType = ContentType.get(contentTypeHeader).orElse(ContentType.PLAIN_TEXT);

        // getting request body
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        // populating context
        return CallContext.builder()
                .url(request.getServletPath())
                .method(request.getMethod())
                .contentType(contentType)
                .body(body)
                .parsedBody(RequestBodyParser.parseBody(body, contentType))
                .headers(Collections.list(request.getHeaderNames())
                        .stream().collect(Collectors.toMap(Function.identity(), request::getHeader)))
                .queryParams(Collections.list(request.getParameterNames())
                        .stream().collect(Collectors.toMap(Function.identity(), request::getParameter)))
                .build();
    }
}
