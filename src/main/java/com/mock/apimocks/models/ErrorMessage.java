package com.mock.apimocks.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mock.apimocks.exception.HttpError;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
    @ApiModelProperty(value = "Error type", required = true, example = "Bad Request")
    private String type;

    @ApiModelProperty(value = "Error description", required = true, example = "There are some problems with the request body")
    private String description;

    @ApiModelProperty(value = "List of error causes", example = "[\"The property 'test' cannot be null.\"]")
    private Set<String> errors;

    public ErrorMessage(HttpError error) {
        this.type = error.getHttpError();
        this.description = error.getDescription();
    }
}
