package com.mock.apimocks.models.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("MockApis")
public class MockApi {
    @Id
    @ApiModelProperty(value = "API Mock Identifier", example = "1234XXXX-XXXX-XXXX-XXXX-XXXXXXXX5678")
    private String id;

    @ApiModelProperty(value = "Mocked API Name", required = true, example = "Test API")
    @NotEmpty(message = "The property 'name' cannot be null or empty")
    private String name;

    @ApiModelProperty(value = "Mocked API Base Path", required = true, example = "/test-api")
    @Size(min = 2, message = "The property 'basePath' should contains at least one letter after the slash")
    @NotEmpty(message = "The property 'basePath' cannot be null or empty and should start with a slash character")
    private String basePath;

    @ApiModelProperty(value = "Mock Operations", required = true)
    @Valid
    private List<MockOperation> operations;
}
