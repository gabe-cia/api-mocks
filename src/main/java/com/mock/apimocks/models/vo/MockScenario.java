package com.mock.apimocks.models.vo;

import com.mock.apimocks.enums.ContentType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Map;

@Data
@Builder
public class MockScenario {
    @ApiModelProperty(value = "Operation Scenario Name", required = true, example = "Success Scenario With 200")
    @NotEmpty(message = "The property 'name' cannot be null or empty")
    private String name;

    @ApiModelProperty(value = "Operation Scenario Condition to be fulfilled. Must not be empty in case the 'isDefault' property was false.", example = "$path.id == 3")
    private String conditions;

    @ApiModelProperty(value = "HTTP Status code", required = true, example = "200")
    @Min(value = 100, message = "The property 'httpCode' should be greater than 100 and lesser than 600")
    @Max(value = 599, message = "The property 'httpCode' should be greater than 100 and lesser than 600")
    @NotNull(message = "The property 'httpCode' cannot be null or empty")
    private Integer httpCode;

    @ApiModelProperty(value = "The scenario's evaluation order", required = true, example = "1")
    @Positive(message = "The property 'order' should be a positive number")
    @NotNull(message = "The property 'order' cannot be null or empty")
    private Integer order;

    @ApiModelProperty(value = "A flag which indicates if the scenario is default. There should be one and only one default scenario on each environment", example = "false")
    private boolean isDefault;

    @ApiModelProperty(value = "Response Content-Type. This property is allowed to be null because there could be scenarios with no body", example = "JSON", allowableValues = "JSON, XML, APP_XML, URL_ENCODED, PLAIN_TEXT")
    private ContentType contentType;

    @ApiModelProperty(value = "Response body", example = "{\"response\": \"OK\"}")
    private String body;

    @ApiModelProperty(value = "Response headers. This property should be a map where the key / value corresponds with the header name and value.", example = "{\"Content-Type\": \"application/json\", \"server\": \"API Manager\"}")
    private Map<String, String> headers;
}
