package com.mock.apimocks.models.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RedisHash("RegexOperation")
public class RegexOperation implements Serializable {
    public RegexOperation(MockOperation operation) {
        this.regex = operation.getMethod() + operation.getRegex();
        this.operationId = operation.getId();
    }

    @Id
    private String regex;
    private String operationId;
}
