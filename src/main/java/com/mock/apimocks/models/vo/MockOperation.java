package com.mock.apimocks.models.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpMethod;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@RedisHash("MockOperation")
public class MockOperation {
    @ApiModelProperty(value = "Operation Identifier", example = "5678XXXX-XXXX-XXXX-XXXX-XXXXXXXX9012")
    @Id
    private String id;

    @ApiModelProperty(value = "Operation Method", required = true, example = "POST", allowableValues = "GET, POST, PUT, PATCH, DELETE")
    @NotNull(message = "The property 'method' cannot be null or empty")
    private HttpMethod method;

    @ApiModelProperty(value = "Operation Path", required = true, example = "/example/{id}")
    @Size(min = 2, message = "The property 'path' should contains at least one letter after the slash")
    @NotEmpty(message = "The property 'path' cannot be null or empty and should start with a slash")
    private String path;

    @JsonIgnore
    private String fullPath;

    @JsonIgnore
    private String regex;

    @ApiModelProperty(value = "Mock Response Scenarios", required = true)
    @Valid
    private List<MockScenario> scenarios;
}
