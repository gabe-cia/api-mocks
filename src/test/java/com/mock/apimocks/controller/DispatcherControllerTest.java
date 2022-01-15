package com.mock.apimocks.controller;

import com.mock.apimocks.enums.ContentType;
import com.mock.apimocks.service.MockService;
import com.mock.apimocks.MvcControllerTestable;
import com.mock.apimocks.exception.InternalServerErrorException;
import com.mock.apimocks.exception.ResourceNotFoundException;
import com.mock.apimocks.models.CallContext;
import com.mock.apimocks.models.vo.MockScenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class DispatcherControllerTest extends MvcControllerTestable<DispatcherController> {
    private MvcResult response;

    private String id;
    private String body;
    private HttpHeaders headers;
    private MultiValueMap<String, String> queryParams;

    private static final String VALID_ID = "123";
    private static final MultiValueMap<String, String> VALID_QUERY_PARAMS = new LinkedMultiValueMap<String, String>() {
        {
            add("queryA", "1");
            add("queryB", "2");
            add("queryC", "3");
        }
    };
    private static final HttpHeaders VALID_HEADERS = new HttpHeaders() {
        {
            put("header1", Collections.singletonList("A"));
            put("header2", Collections.singletonList("B"));
            put("header3", Collections.singletonList("C"));
        }
    };
    private static final String VALID_BODY = "{\"test\": 123}";
    private static final MockScenario VALID_SCENARIO = MockScenario.builder()
            .httpCode(200)
            .headers(new HashMap<String, String>() {
                {
                    put("returnedHeader1", "retA");
                    put("returnedHeader2", "retB");
                    put("returnedHeader3", "retC");
                }
            })
            .contentType(ContentType.JSON)
            .body("{\"returned\": \"OK\"}")
            .build();
    private static final MockScenario NO_CONTENT_TYPE_SCENARIO = MockScenario.builder()
            .httpCode(200)
            .headers(new HashMap<String, String>() {
                {
                    put("returnedHeader1", "retA");
                    put("returnedHeader2", "retB");
                    put("returnedHeader3", "retC");
                }
            })
            .body("{\"returned\": \"OK\"}")
            .build();
    private static final MockScenario NULL_HEADERS_SCENARIO = MockScenario.builder()
            .httpCode(200)
            .contentType(ContentType.JSON)
            .body("{\"returned\": \"OK\"}")
            .build();
    private static final MockScenario EMPTY_BODY_SCENARIO = MockScenario.builder()
            .httpCode(200)
            .headers(new HashMap<String, String>() {
                {
                    put("returnedHeader1", "retA");
                    put("returnedHeader2", "retB");
                    put("returnedHeader3", "retC");
                }
            })
            .contentType(ContentType.JSON)
            .build();

    @InjectMocks
    private DispatcherController controller;

    @Mock
    private MockService service;

    @Before
    public void setup() {
        initializeMvc(controller);
    }

    // Testing wildcardApiCall
    @Test
    public void wildcardApiCallWithSuccessForGetMethod() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAValidScenario();
        whenWeCallWildcardApiCallWithGet();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectHeaders();
        thenWeExpectTheCorrectBody();
    }

    @Test
    public void wildcardApiCallWithSuccessForPostMethod() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAValidScenario();
        whenWeCallWildcardApiCallWithPost();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectHeaders();
        thenWeExpectTheCorrectBody();
    }

    @Test
    public void wildcardApiCallWithSuccessForPutMethod() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAValidScenario();
        whenWeCallWildcardApiCallWithPut();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectHeaders();
        thenWeExpectTheCorrectBody();
    }

    @Test
    public void wildcardApiCallWithSuccessForPatchMethod() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAValidScenario();
        whenWeCallWildcardApiCallWithPatch();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectHeaders();
        thenWeExpectTheCorrectBody();
    }

    @Test
    public void wildcardApiCallWithSuccessForDeleteMethod() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAValidScenario();
        whenWeCallWildcardApiCallWithDelete();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectHeaders();
        thenWeExpectTheCorrectBody();
    }

    @Test
    public void wildcardApiCallWithNotFoundError() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioThrowsAResourceNotFoundException();
        whenWeCallWildcardApiCallWithDelete();
        thenWeExpectANotFoundStatus();
    }

    @Test
    public void wildcardApiCallWithInternalServerError() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioThrowsAnInternalServerError();
        whenWeCallWildcardApiCallWithDelete();
        thenWeExpectAnInternalServerErrorStatus();
    }

    @Test
    public void wildcardApiCallWithNoResponseContentType() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAScenarioWithNoContentType();
        whenWeCallWildcardApiCallWithGet();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectHeaders();
        thenWeExpectTheCorrectBody();
    }

    @Test
    public void wildcardApiCallWithEmptyResponseHeaders() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAScenarioWithNoHeaders();
        whenWeCallWildcardApiCallWithGet();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectBody();
    }

    @Test
    public void wildcardApiCallWithEmptyResponseBody() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAScenarioWithNoBody();
        whenWeCallWildcardApiCallWithGet();
        thenWeExpectAnOkStatus();
        thenWeExpectTheCorrectHeaders();
        thenWeExpectAnEmptyBody();
    }

    @Test
    public void wildcardApiCallWithInvalidBody() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceGetScenarioReturnsAValidScenario();
        whenWeCallWildcardApiCallWithInvalidBody();
        thenWeExpectABadRequestStatus();
    }

    @Test
    public void wildcardApiCallWithGenericBusinessError() throws Exception {
        givenWeHaveAValidPathParameter();
        givenWeHaveSomeValidQueryParameter();
        givenWeHaveSomeValidHeaders();
        givenWeHaveAValidBody();
        givenServiceThrowsAnGenericException();
        whenWeCallWildcardApiCallWithGet();
        thenWeExpectAnInternalServerErrorStatus();
    }

    // Given methods
    private void givenWeHaveAValidPathParameter() {
        this.id = VALID_ID;
    }

    private void givenWeHaveSomeValidQueryParameter() {
        this.queryParams = VALID_QUERY_PARAMS;
    }

    private void givenWeHaveSomeValidHeaders() {
        this.headers = VALID_HEADERS;
    }

    private void givenWeHaveAValidBody() {
        this.body = VALID_BODY;
    }

    private void givenServiceGetScenarioReturnsAValidScenario() {
        doReturn(VALID_SCENARIO).when(service).getScenario(any(CallContext.class));
    }

    private void givenServiceGetScenarioReturnsAScenarioWithNoContentType() {
        doReturn(NO_CONTENT_TYPE_SCENARIO).when(service).getScenario(any(CallContext.class));
    }

    private void givenServiceGetScenarioReturnsAScenarioWithNoHeaders() {
        doReturn(NULL_HEADERS_SCENARIO).when(service).getScenario(any(CallContext.class));
    }

    private void givenServiceGetScenarioReturnsAScenarioWithNoBody() {
        doReturn(EMPTY_BODY_SCENARIO).when(service).getScenario(any(CallContext.class));
    }

    private void givenServiceThrowsAnGenericException() {
        doThrow(new RuntimeException()).when(service).getScenario(any(CallContext.class));
    }

    private void givenServiceGetScenarioThrowsAResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Operation not found")).when(service).getScenario(any(CallContext.class));
    }

    private void givenServiceGetScenarioThrowsAnInternalServerError() {
        doThrow(new InternalServerErrorException("Internal Server error")).when(service).getScenario(any(CallContext.class));
    }

    // When methods
    private void whenWeCallWildcardApiCallWithGet() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.get("/any-url-that-not-mocks/{id}", this.id)
                .headers(this.headers)
                .params(this.queryParams)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
    }

    private void whenWeCallWildcardApiCallWithPost() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.post("/any-url-that-not-mocks/{id}", this.id)
                .headers(this.headers)
                .params(this.queryParams)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.body)).andReturn();
    }

    private void whenWeCallWildcardApiCallWithPut() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.put("/any-url-that-not-mocks/{id}", this.id)
                .headers(this.headers)
                .params(this.queryParams)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.body)).andReturn();
    }

    private void whenWeCallWildcardApiCallWithPatch() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.patch("/any-url-that-not-mocks/{id}", this.id)
                .headers(this.headers)
                .params(this.queryParams)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.body)).andReturn();
    }

    private void whenWeCallWildcardApiCallWithDelete() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.delete("/any-url-that-not-mocks/{id}", this.id)
                .headers(this.headers)
                .params(this.queryParams)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
    }

    private void whenWeCallWildcardApiCallWithInvalidBody() throws Exception {
        this.response = mvc.perform(MockMvcRequestBuilders.post("/any-url-that-not-mocks/{id}", this.id)
                .headers(this.headers)
                .params(this.queryParams)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new byte[] {0, -121, -80, 116, -62})).andReturn();
    }

    // Then methods
    private void thenWeExpectAnOkStatus() {
        assertEquals(HttpStatus.OK.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectAnInternalServerErrorStatus() {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectANotFoundStatus() {
        assertEquals(HttpStatus.NOT_FOUND.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectABadRequestStatus() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), this.response.getResponse().getStatus());
    }

    private void thenWeExpectTheCorrectHeaders() {
        assertEquals("retA", this.response.getResponse().getHeader("returnedHeader1"));
        assertEquals("retB", this.response.getResponse().getHeader("returnedHeader2"));
        assertEquals("retC", this.response.getResponse().getHeader("returnedHeader3"));
    }

    private void thenWeExpectTheCorrectBody() throws Exception {
        assertEquals("{\"returned\": \"OK\"}", this.response.getResponse().getContentAsString());
    }

    private void thenWeExpectAnEmptyBody() throws Exception {
        assertEquals("", this.response.getResponse().getContentAsString());
    }
}
