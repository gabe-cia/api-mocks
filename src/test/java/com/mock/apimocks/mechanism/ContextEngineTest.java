package com.mock.apimocks.mechanism;

import com.mock.apimocks.models.CallContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ContextEngineTest {
    // test constants
    private static final String URL_WITHOUT_PATH_PARAMS = "/test/api/v1/users";
    private static final String URL_WITH_PATH_PARAMS = "/test/api/v1/users/{user_id}/addresses/{cep}/city";
    private static final String UNFORMATTED_URL_WITHOUT_PATH_PARAMS = "////test////////////////api/v1///users//";
    private static final String UNFORMATTED_URL_WITH_PATH_PARAMS = "/////////////test/api///v1/users///////{user_id}/addresses/{cep}//city///";
    private static final String URL_WITH_QUERY_PARAMS = "/test/api/v1/users?name=abc&age=3";
    private static final String URL_WITH_QUERY_AND_PATH_PARAMS = "/test/api/v1/users/{user_id}/addresses/{cep}/city?name=abc&age=3";

    private static final String REGEX_WITHOUT_PATH_PARAMS = "(\\/+test\\/+api\\/+v1\\/+users)";
    private static final String REGEX_WITH_PATH_PARAMS = "(\\/+test\\/+api\\/+v1\\/+users\\/+([^\\/]+)\\/+addresses\\/+([^\\/]+)\\/+city)";

    private static final String REQUESTED_URL_WITHOUT_PATH_PARAMETERS = "/test/api/v1/users";
    private static final String REQUESTED_URL_WITH_PATH_PARAMETERS = "/test/api/v1/users/1234/addresses/13000123/city";
    private static final String INCORRECT_URL_WITHOUT_PATH_PARAMS = "/api/v1/users";
    private static final String INCORRECT_URL_WITH_PATH_PARAMS = "/api/v1/users/1234/addresses/13000123/city";

    private static final String TRUE_HEADER_CONDITION = "$header.server == 'Tomcat'";
    private static final String TRUE_PATH_CONDITION = "$path.user_id == '1234'";
    private static final String TRUE_QUERY_CONDITION = "$query.name == 'gabriel'";
    private static final String TRUE_BODY_CONDITION = "$body.phone == '19998765432'";
    private static final String BRACKETS_HEADER_CONDITION = "$header['x-forwarded-from'] == '192.168.0.1'";
    private static final String BRACKETS_PATH_CONDITION = "$path['cep'] == '13000123'";
    private static final String BRACKETS_QUERY_CONDITION = "$query['cpf'] == '333.888.777-11'";
    private static final String BRACKETS_BODY_CONDITION = "$body['phone'] == '19998765432'";
    private static final String MISSING_HEADER_CONDITION = "$header.missingProperty == 'Tomcat'";
    private static final String MISSING_PATH_CONDITION = "$path.missingProperty == '1234'";
    private static final String MISSING_QUERY_CONDITION = "$query.missingProperty == 'gabriel'";
    private static final String MISSING_BODY_CONDITION = "$body.missingProperty == '19998765432'";
    private static final String WRONG_HEADER_CONDITION = "$header.server == 'wrong value'";
    private static final String WRONG_PATH_CONDITION = "$path.user_id == 'wrong value'";
    private static final String WRONG_QUERY_CONDITION = "$query.name == 'wrong value'";
    private static final String WRONG_BODY_CONDITION = "$body.phone == 'wrong value'";
    private static final String TRUE_OR_CONDITION = "$header.server == 'Tomcat' || $query.name == 'wrong value'";
    private static final String FALSE_OR_CONDITION = "$header.server == 'wrong value' || $query.name == 'wrong value'";
    private static final String TRUE_AND_CONDITION = "$header.server == 'Tomcat' && $query.name == 'gabriel'";
    private static final String FALSE_AND_CONDITION = "$header.server == 'Tomcat' && $query.name == 'wrong value'";
    private static final String COMPLEX_TRUE_CONDITION = "($header.server == 'Tomcat' || $query.name == 'wrong value') && $path.user_id == '1234'";
    private static final String COMPLEX_FALSE_CONDITION = "($header.server == 'Tomcat' || $query.name == 'wrong value') && $path.user_id == 'wrong value'";
    private static final String MULTILEVEL_BODY_CONDITION = "$body.addresses.work == '156 Grant Drive Avenue'";
    private final static String MISSING_PARENT_ON_MULTILEVEL_BODY_PROPERTY = "$body.phones.work == '1932547698'";
    private final static String ARRAY_INDEX_TRUE_CONDITION = "$body.accountNumbers[2] == '345'";
    private final static String ARRAY_INDEX_FALSE_CONDITION = "$body.accountNumbers[2] == '123'";
    private final static String ARRAY_OUT_OF_BOUNDS_CONDITION = "$body.accountNumbers[10] == '123'";
    private final static String ARRAY_CONTAINS_ITEM_CONDITION = "$body.accountNumbers.contains('234')";
    private final static String ARRAY_DOES_NOT_CONTAINS_ITEM_CONDITION = "$body.accountNumbers.contains('789')";
    private final static String ARRAY_LENGTH_TEST_CONDITION = "$body.accountNumbers.length == 4";
    private final static String ARRAY_CONTAINS_ALL_TRUE_CONDITION = "$body.accountNumbers.containsAll(['234', '345'])";
    private final static String ARRAY_CONTAINS_ALL_FALSE_CONDITION = "$body.accountNumbers.containsAll(['234', '789'])";
    private final static String ARRAY_INDEX_OF_TEST_CONDITION = "$body.accountNumbers.indexOf('234') == 1";
    private final static String STRING_CONTAINS_TRUE_CONDITION = "$body.cpf.contains('888')";
    private final static String STRING_CONTAINS_FALSE_CONDITION = "$body.cpf.contains('000')";
    private final static String STRING_INDEX_OF_CONDITION = "$body.cpf.indexOf('888') == 4";
    private final static String INTEGER_CONDITION = "$body.balance == 456.78";
    private final static String NEGATIVE_INTEGER_CONDITION = "$body.debits == -123.45";
    private final static String FLOAT_CONDITION = "$body.score == 75";
    private final static String NEGATIVE_FLOAT_CONDITION = "$body.lowestScore == -20";
    private final static String DOT_FLOAT_CONDITION = "$body.highestRate == 0.86";
    private final static String DOT_NEGATIVE_FLOAT_CONDITION = "$body.lowestRate == -0.32";
    private final static String BOOLEAN_TRUE_CONDITION = "$body.married";
    private final static String BOOLEAN_FALSE_CONDITION = "$body.hasChildren";

    private static final Map<String, String> PATH_PARAMS = new HashMap<String, String>() {
        {
            put("user_id", "1234");
            put("cep", "13000123");
        }
    };
    private static final Map<String, String> HEADERS = new HashMap<String, String>() {
        {
            put("Content-Type", "application/json");
            put("x-forwarded-from", "192.168.0.1");
            put("server", "Tomcat");
        }
    };
    private static final Map<String, String> QUERY_PARAMS = new HashMap<String, String>() {
        {
            put("name", "gabriel");
            put("cpf", "333.888.777-11");
            put("phone", "19998765432");
        }
    };
    private static final Map<String, Object> BODY = new HashMap<String, Object>() {
        {
            put("name", "gabriel");
            put("cpf", "333.888.777-11");
            put("phone", "19998765432");
            put("user_id", "1234");
            put("cep", "13000123");
            put("balance", 456.78);
            put("debits", -123.45);
            put("score", 75);
            put("lowestScore", -20);
            put("highestRate", .86);
            put("lowestRate", -.32);
            put("married", true);
            put("hasChildren", false);
            put("addresses", new HashMap<String, Object>() {
                {
                    put("home", "432 North Madison Street");
                    put("work", "156 Grant Drive Avenue");
                }
            });
            put("accountNumbers", Arrays.asList("123", "234", "345", "456"));
        }
    };

    // Test variables
    private String url;
    private String regex;
    private Boolean matchedUrl;
    private String requestedUrl;
    private Map<String, String> pathParams;
    private Boolean conditionMatched;
    private CallContext callContext;
    private String condition;
    private String sanitizedUrl;

    /*
     * Testing createUrlVerifier
     */
    @Test
    public void createUrlVerifierFormattedWithoutPathParameters() {
        givenWeHaveAFormattedUrlWithoutPathParameters();
        whenWeCallCreateUrlVerifier();
        thenWeExpectARegexWithoutPathParams();
    }

    @Test
    public void createUrlVerifierFormattedWithPathParameters() {
        givenWeHaveAFormattedUrlWithPathParameters();
        whenWeCallCreateUrlVerifier();
        thenWeExpectARegexWithPathParams();
    }

    @Test
    public void createUrlVerifierUnformattedWithoutPathParameters() {
        givenWeHaveAnUnformattedUrlWithoutPathParameters();
        whenWeCallCreateUrlVerifier();
        thenWeExpectARegexWithoutPathParams();
    }

    @Test
    public void createUrlVerifierUnformattedWithPathParameters() {
        givenWeHaveAnUnformattedUrlWithPathParameters();
        whenWeCallCreateUrlVerifier();
        thenWeExpectARegexWithPathParams();
    }

    @Test
    public void createUrlVerifierWithQueryParametersAndNoPathParameters() {
        givenWeHaveAnUrlWithQueryParametersAndNoPathParameters();
        whenWeCallCreateUrlVerifier();
        thenWeExpectARegexWithoutPathParams();
    }

    @Test
    public void createUrlVerifierWithQueryParametersAndPathParameters() {
        givenWeHaveAnUrlWithQueryParametersAndPathParameters();
        whenWeCallCreateUrlVerifier();
        thenWeExpectARegexWithPathParams();
    }

    /*
     * Testing verifyUrl
     */
    @Test
    public void verifyUrlWithoutPathParameterWithMatchedRegex() {
        givenWeHaveARegexWithoutPathParameters();
        givenWeHaveAMatchedUrlWithoutPathParameters();
        whenWeCallVerifyUrl();
        thenWeExpectTheVerifyResultToBeTrue();
    }

    @Test
    public void verifyUrlWithoutPathParameterWithUnmatchedRegex() {
        givenWeHaveARegexWithoutPathParameters();
        givenWeHaveAnUnmatchedUrlWithoutPathParameters();
        whenWeCallVerifyUrl();
        thenWeExpectTheVerifyResultToBeFalse();
    }

    @Test
    public void verifyUrlWithPathParameterWithMatchedRegex() {
        givenWeHaveARegexWithPathParameters();
        givenWeHaveAMatchedUrlWithPathParameters();
        whenWeCallVerifyUrl();
        thenWeExpectTheVerifyResultToBeTrue();
    }

    @Test
    public void verifyUrlWithPathParameterWithUnmatchedRegex() {
        givenWeHaveARegexWithPathParameters();
        givenWeHaveAnUnmatchedUrlWithPathParameters();
        whenWeCallVerifyUrl();
        thenWeExpectTheVerifyResultToBeFalse();
    }

    /*
     * Testing getPathParameters
     */
    @Test
    public void getPathParametersWithMatchedUrlAndRegexAndWithPathParams() {
        givenWeHaveAFormattedUrlWithPathParameters();
        givenWeHaveARegexWithPathParameters();
        givenWeHaveARequestedUrlWithPathParameters();
        whenWeCallGetPathParameters();
        thenWeExpectThePathParametersToContainsAllValues();
    }

    @Test
    public void getPathParametersWithMatchedUrlAndRegexAndWithoutPathParams() {
        givenWeHaveAFormattedUrlWithoutPathParameters();
        givenWeHaveARegexWithPathParameters();
        givenWeHaveARequestedUrlWithoutPathParameters();
        whenWeCallGetPathParameters();
        thenWeExpectThePathParametersToContainsNoValues();
    }

    /*
     * Testing evaluateCondition
     */
    @Test
    public void evaluateConditionWithHeaderValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsHeaders();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithPathParamValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsPathParams();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithQueryParamValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsQueryParams();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithBodyValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParams();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithHeaderValidationWithBracketsGetter() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsHeadersWithBracketsGetter();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithPathParamValidationWithBracketsGetter() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsPathParamsWithBracketsGetter();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithQueryParamValidationWithBracketsGetter() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsQueryParamsWithBracketsGetter();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithBodyValidationWithBracketsGetter() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParamsWithBracketsGetter();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithHeaderValidationWithMissingProperty() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsHeadersWithMissingProperty();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithPathParamValidationWithMissingProperty() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsPathParamsWithMissingProperty();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithQueryParamValidationWithMissingProperty() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsQueryParamsWithMissingProperty();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithBodyValidationWithMissingProperty() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParamsWithMissingProperty();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithHeaderValidationWithWrongValue() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsHeadersWithWrongValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithPathParamValidationWithWrongValue() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsPathParamsWithWrongValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithQueryParamValidationWithWrongValue() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsQueryParamsWithWrongValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithBodyValidationWithWrongValue() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParamsWithWrongValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithTrueOrClause() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParamsWithTrueOrClause();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseOrClause() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParamsWithFalseOrClause();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithTrueAndClause() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParamsWithTrueAndClause();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseAndClause() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsBodyParamsWithFalseAndClause();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithTrueComplexClause() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsATrueComplexClause();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseComplexClause() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAFalseComplexClause();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithBodyValidationWithMultilevelProperty() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsATrueMultilevelPropertyFromBody();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithBodyValidationWithMissingParentOnMultilevelProperty() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAMissingParentOnMultilevelPropertyFromBody();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithTrueArrayItemValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsATrueArrayItemValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseArrayItemValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAFalseArrayItemValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithArrayItemOutOfBounds() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAnArrayOutOfBoundsItem();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithTrueArrayContainsValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsATrueArrayContainsValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseArrayContainsValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAFalseArrayContainsValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithArrayLengthValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAnArrayLength();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithTrueArrayContainsAllValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsATrueArrayContainsAllValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseArrayContainsAllValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAFalseArrayContainsAllValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithIndexOfValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAIndexOfValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithTrueStringContainsValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsATrueStringContainsValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseStringContainsValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAFalseStringContainsValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    @Test
    public void evaluateConditionWithStringIndexOfValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAStringIndexOfValidation();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithIntegerValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAnIntegerValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithNegativeIntegerValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsANegativeIntegerValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFloatValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAFloatValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithNegativeFloatValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsANegativeFloatValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithDotFloatValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsADotFloatValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithDotNegativeFloatValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsANegativeDotFloatValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithTrueBooleanValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsATrueBooleanValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeTrue();
    }

    @Test
    public void evaluateConditionWithFalseBooleanValidation() {
        givenWeHaveAPopulatedCallContext();
        givenOurConditionTestsAFalseBooleanValue();
        whenWeCallEvaluateCondition();
        thenWeExpectTheConditionToBeFalse();
    }

    /*
     * Testing sanitizeUrl
     */
    @Test
    public void sanitizeUrlWithCleanUrlShouldReturnTheSameUrl() {
        givenWeHaveACleanUrl();
        whenWeCallSanitizeUrl();
        thenWeExpectTheSameSanitizedUrl();
    }

    @Test
    public void sanitizeUrlWithDuplicatedSlashesShouldReturnASingleSlashOnEachWord() {
        givenWeHaveAUrlWithMultipleSlashes();
        whenWeCallSanitizeUrl();
        thenWeExpectASanitizedUrl();
    }

    @Test
    public void sanitizeUrlWithTrailingSlashShouldReturnNoSlashesAtTheEndOfUrl() {
        givenWeHaveAUrlWithTrailingSlash();
        whenWeCallSanitizeUrl();
        thenWeExpectASanitizedUrl();
    }

    @Test
    public void sanitizeUrlWithQueryParamsShouldReturnAUrlWithNoQuery() {
        givenWeHaveAUrlWithQueryParameter();
        whenWeCallSanitizeUrl();
        thenWeExpectASanitizedUrl();
    }

    /*
     * Given Methods
     */
    private void givenWeHaveAFormattedUrlWithoutPathParameters() {
        this.url = URL_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveAFormattedUrlWithPathParameters() {
        this.url = URL_WITH_PATH_PARAMS;
    }

    private void givenWeHaveAnUnformattedUrlWithoutPathParameters() {
        this.url = UNFORMATTED_URL_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveAnUnformattedUrlWithPathParameters() {
        this.url = UNFORMATTED_URL_WITH_PATH_PARAMS;
    }

    private void givenWeHaveAnUrlWithQueryParametersAndNoPathParameters() {
        this.url = URL_WITH_QUERY_PARAMS;
    }

    private void givenWeHaveAnUrlWithQueryParametersAndPathParameters() {
        this.url = URL_WITH_QUERY_AND_PATH_PARAMS;
    }

    private void givenWeHaveARegexWithoutPathParameters() {
        this.regex = REGEX_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveARegexWithPathParameters() {
        this.regex = REGEX_WITH_PATH_PARAMS;
    }

    private void givenWeHaveAMatchedUrlWithoutPathParameters() {
        this.url = URL_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveAnUnmatchedUrlWithoutPathParameters() {
        this.url = INCORRECT_URL_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveAMatchedUrlWithPathParameters() {
        this.url = URL_WITH_PATH_PARAMS;
    }

    private void givenWeHaveAnUnmatchedUrlWithPathParameters() {
        this.url = INCORRECT_URL_WITH_PATH_PARAMS;
    }

    private void givenWeHaveARequestedUrlWithPathParameters() {
        this.requestedUrl = REQUESTED_URL_WITH_PATH_PARAMETERS;
    }

    private void givenWeHaveARequestedUrlWithoutPathParameters() {
        this.requestedUrl = REQUESTED_URL_WITHOUT_PATH_PARAMETERS;
    }

    private void givenWeHaveAPopulatedCallContext() {
        this.callContext = new CallContext();
        this.callContext.setPathParams(PATH_PARAMS);
        this.callContext.setQueryParams(QUERY_PARAMS);
        this.callContext.setHeaders(HEADERS);
        this.callContext.setParsedBody(BODY);
    }

    private void givenWeHaveACleanUrl() {
        this.url = URL_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveAUrlWithMultipleSlashes() {
        this.url = UNFORMATTED_URL_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveAUrlWithTrailingSlash() {
        this.url = UNFORMATTED_URL_WITHOUT_PATH_PARAMS;
    }

    private void givenWeHaveAUrlWithQueryParameter() {
        this.url = URL_WITHOUT_PATH_PARAMS;
    }

    private void givenOurConditionTestsHeaders() {
        this.condition = TRUE_HEADER_CONDITION;
    }

    private void givenOurConditionTestsPathParams() {
        this.condition = TRUE_PATH_CONDITION;
    }

    private void givenOurConditionTestsQueryParams() {
        this.condition = TRUE_QUERY_CONDITION;
    }

    private void givenOurConditionTestsBodyParams() {
        this.condition = TRUE_BODY_CONDITION;
    }

    private void givenOurConditionTestsHeadersWithBracketsGetter() {
        this.condition = BRACKETS_HEADER_CONDITION;
    }

    private void givenOurConditionTestsPathParamsWithBracketsGetter() {
        this.condition = BRACKETS_PATH_CONDITION;
    }

    private void givenOurConditionTestsQueryParamsWithBracketsGetter() {
        this.condition = BRACKETS_QUERY_CONDITION;
    }

    private void givenOurConditionTestsBodyParamsWithBracketsGetter() {
        this.condition = BRACKETS_BODY_CONDITION;
    }

    private void givenOurConditionTestsHeadersWithMissingProperty() {
        this.condition = MISSING_HEADER_CONDITION;
    }

    private void givenOurConditionTestsPathParamsWithMissingProperty() {
        this.condition = MISSING_PATH_CONDITION;
    }

    private void givenOurConditionTestsQueryParamsWithMissingProperty() {
        this.condition = MISSING_QUERY_CONDITION;
    }

    private void givenOurConditionTestsBodyParamsWithMissingProperty() {
        this.condition = MISSING_BODY_CONDITION;
    }

    private void givenOurConditionTestsHeadersWithWrongValue() {
        this.condition = WRONG_HEADER_CONDITION;
    }

    private void givenOurConditionTestsPathParamsWithWrongValue() {
        this.condition = WRONG_PATH_CONDITION;
    }

    private void givenOurConditionTestsQueryParamsWithWrongValue() {
        this.condition = WRONG_QUERY_CONDITION;
    }

    private void givenOurConditionTestsBodyParamsWithWrongValue() {
        this.condition = WRONG_BODY_CONDITION;
    }

    private void givenOurConditionTestsBodyParamsWithTrueOrClause() {
        this.condition = TRUE_OR_CONDITION;
    }

    private void givenOurConditionTestsBodyParamsWithFalseOrClause() {
        this.condition = FALSE_OR_CONDITION;
    }

    private void givenOurConditionTestsBodyParamsWithTrueAndClause() {
        this.condition = TRUE_AND_CONDITION;
    }

    private void givenOurConditionTestsBodyParamsWithFalseAndClause() {
        this.condition = FALSE_AND_CONDITION;
    }

    private void givenOurConditionTestsATrueComplexClause() {
        this.condition = COMPLEX_TRUE_CONDITION;
    }

    private void givenOurConditionTestsAFalseComplexClause() {
        this.condition = COMPLEX_FALSE_CONDITION;
    }

    private void givenOurConditionTestsATrueMultilevelPropertyFromBody() {
        this.condition = MULTILEVEL_BODY_CONDITION;
    }

    private void givenOurConditionTestsAMissingParentOnMultilevelPropertyFromBody() {
        this.condition = MISSING_PARENT_ON_MULTILEVEL_BODY_PROPERTY;
    }

    private void givenOurConditionTestsATrueArrayItemValidation() {
        this.condition = ARRAY_INDEX_TRUE_CONDITION;
    }

    private void givenOurConditionTestsAFalseArrayItemValidation() {
        this.condition = ARRAY_INDEX_FALSE_CONDITION;
    }

    private void givenOurConditionTestsAnArrayOutOfBoundsItem() {
        this.condition = ARRAY_OUT_OF_BOUNDS_CONDITION;
    }

    private void givenOurConditionTestsATrueArrayContainsValidation() {
        this.condition = ARRAY_CONTAINS_ITEM_CONDITION;
    }

    private void givenOurConditionTestsAFalseArrayContainsValidation() {
        this.condition = ARRAY_DOES_NOT_CONTAINS_ITEM_CONDITION;
    }

    private void givenOurConditionTestsAnArrayLength() {
        this.condition = ARRAY_LENGTH_TEST_CONDITION;
    }

    private void givenOurConditionTestsATrueArrayContainsAllValidation() {
        this.condition = ARRAY_CONTAINS_ALL_TRUE_CONDITION;
    }

    private void givenOurConditionTestsAFalseArrayContainsAllValidation() {
        this.condition = ARRAY_CONTAINS_ALL_FALSE_CONDITION;
    }


    private void givenOurConditionTestsAIndexOfValidation() {
        this.condition = ARRAY_INDEX_OF_TEST_CONDITION;
    }

    private void givenOurConditionTestsATrueStringContainsValidation() {
        this.condition = STRING_CONTAINS_TRUE_CONDITION;
    }

    private void givenOurConditionTestsAFalseStringContainsValidation() {
        this.condition = STRING_CONTAINS_FALSE_CONDITION;
    }

    private void givenOurConditionTestsAStringIndexOfValidation() {
        this.condition = STRING_INDEX_OF_CONDITION;
    }

    private void givenOurConditionTestsAnIntegerValue() {
        this.condition = INTEGER_CONDITION;
    }

    private void givenOurConditionTestsANegativeIntegerValue() {
        this.condition = NEGATIVE_INTEGER_CONDITION;
    }

    private void givenOurConditionTestsAFloatValue() {
        this.condition = FLOAT_CONDITION;
    }

    private void givenOurConditionTestsANegativeFloatValue() {
        this.condition = NEGATIVE_FLOAT_CONDITION;
    }

    private void givenOurConditionTestsADotFloatValue() {
        this.condition = DOT_FLOAT_CONDITION;
    }

    private void givenOurConditionTestsANegativeDotFloatValue() {
        this.condition = DOT_NEGATIVE_FLOAT_CONDITION;
    }

    private void givenOurConditionTestsATrueBooleanValue() {
        this.condition = BOOLEAN_TRUE_CONDITION;
    }

    private void givenOurConditionTestsAFalseBooleanValue() {
        this.condition = BOOLEAN_FALSE_CONDITION;
    }

    /*
     * When Methods
     */
    private void whenWeCallCreateUrlVerifier() {
        this.regex = ContextEngine.createUrlVerifier(this.url);
    }

    private void whenWeCallVerifyUrl() {
        this.matchedUrl = ContextEngine.verifyUrl(this.url, this.regex);
    }

    private void whenWeCallGetPathParameters() {
        this.pathParams = ContextEngine.getPathParameters(this.url, this.regex, this.requestedUrl);
    }

    private void whenWeCallEvaluateCondition() {
        this.conditionMatched = ContextEngine.evaluateCondition(this.callContext, this.condition);
    }

    private void whenWeCallSanitizeUrl() {
        this.sanitizedUrl = ContextEngine.sanitizeUrl(this.url);
    }

    /*
     * Then Methods
     */
    private void thenWeExpectARegexWithoutPathParams() {
        assertEquals(REGEX_WITHOUT_PATH_PARAMS, this.regex);
    }

    private void thenWeExpectARegexWithPathParams() {
        assertEquals(REGEX_WITH_PATH_PARAMS, this.regex);
    }

    private void thenWeExpectTheVerifyResultToBeTrue() {
        assertTrue(this.matchedUrl);
    }

    private void thenWeExpectTheVerifyResultToBeFalse() {
        assertFalse(this.matchedUrl);
    }

    private void thenWeExpectThePathParametersToContainsAllValues() {
        for (Map.Entry<String, String> path : this.pathParams.entrySet()) {
            assertTrue(PATH_PARAMS.containsKey(path.getKey()));
            assertEquals(PATH_PARAMS.get(path.getKey()), path.getValue());
        }
    }

    private void thenWeExpectThePathParametersToContainsNoValues() {
        assertTrue(this.pathParams.isEmpty());
    }

    private void thenWeExpectTheConditionToBeTrue() {
        assertTrue(this.conditionMatched);
    }

    private void thenWeExpectTheConditionToBeFalse() {
        assertFalse(this.conditionMatched);
    }

    private void thenWeExpectTheSameSanitizedUrl() {
        assertEquals(this.url, this.sanitizedUrl);
    }

    private void thenWeExpectASanitizedUrl() {
        assertEquals(URL_WITHOUT_PATH_PARAMS, this.sanitizedUrl);
    }
}
