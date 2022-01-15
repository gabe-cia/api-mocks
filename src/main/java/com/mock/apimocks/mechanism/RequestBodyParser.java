package com.mock.apimocks.mechanism;

import com.mock.apimocks.enums.ContentType;
import com.mock.apimocks.exception.BadRequestException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class wraps a universal HTTP body parser.
 * <p/>
 * It is able to parse http requests from the content types described on the the {@link ContentType} enum.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public class RequestBodyParser {
    private static final String URL_ENCODED_PARAM_DELIMITER = "&";
    private static final String URL_ENCODED_VALUE_DELIMITER = "=";
    private static final String INTEGER_CHECKER = "^-?[0-9]+";
    private static final String DOUBLE_CHECKER = "^-?\\d+(\\.\\d+)?$";

    /**
     * Default class constructor.
     * <p/>
     * It is set as private because this class only should have static methods
     */
    private RequestBodyParser() {
    }

    /**
     * Parse a given string into a Java object
     * <p/>
     * The objects will be parsed depending on the content type of the request.
     * <p/>
     * Currently, we are accepting these kinds of content:
     * <ul>
     * <li>application/json</li>
     * <li>text/xml</li>
     *     <li>application/x-www-form-urlencoded</li>
     * </ul>
     * In case the content type was not defined, this method returns the string as
     * it is without performing any conversions.
     *
     * @param rawBody     the raw request string to be parsed
     * @param contentType the content type of the request with its kind
     * @return a Java Object with the parsed body
     * @throws BadRequestException whenever the body could not be parsed
     */
    public static Object parseBody(String rawBody, ContentType contentType) throws BadRequestException {
        Object parsed = rawBody;
        try {
            if (contentType != null) {
                switch (contentType) {
                    case XML:
                    case APP_XML:
                        parsed = XML.toJSONObject(rawBody).toMap();
                        // since the {@link XML} class does not throw any exception in case the parsing process fails,
                        // we must throw an {@link JSONException} manually in case the  String to be parsed was not
                        // empty and the method result was an empty Map.
                        if (((HashMap<?, ?>) parsed).isEmpty())
                            throw new JSONException("Unable to parse XML String");
                        break;
                    case JSON:
                        parsed = new JSONObject(rawBody).toMap();
                        break;
                    case URL_ENCODED:
                        parsed = parseUrlEncoded(rawBody);
                        break;
                }
            }
        } catch (JSONException ex) {
            // whenever the body could not be parsed, we should throw a 400
            // Bad Request response because the body's structure was invalid
            throw new BadRequestException("Invalid Request Body. The given body could not be parsed.");
        }
        return parsed;
    }

    /**
     * Parsing an x-www-urlencoded body into a Map<String, Object>
     *
     * @param body the body to be parsed
     * @return a Map with the parsed result
     */
    private static Map<String, Object> parseUrlEncoded(String body) {
        if(!body.contains("="))
            throw new JSONException("Could not parse x-www-urlencoded");

        return Arrays.stream(body.split(URL_ENCODED_PARAM_DELIMITER))
                .map(p -> p.split(URL_ENCODED_VALUE_DELIMITER))
                .collect(Collectors.toMap(p -> p[0], p -> {
                    String val = p.length > 1 ? p[1] : "";
                    // checking for integer number
                    if (val.matches(INTEGER_CHECKER)) {
                        return Integer.valueOf(val);
                    }
                    // checking for double (float)
                    if (val.matches(DOUBLE_CHECKER)) {
                        return Double.valueOf(val);
                    }
                    // checking for boolean
                    if(val.equalsIgnoreCase("true") ||
                            val.equalsIgnoreCase("false")) {
                        return Boolean.valueOf(val);
                    }
                    return val;
                }));
    }
}
