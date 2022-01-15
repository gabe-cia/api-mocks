package com.mock.apimocks.mechanism;

import com.mock.apimocks.enums.ContentType;
import com.mock.apimocks.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RequestBodyParserTest {
    private String body;
    private ContentType contentType;
    private Object parsed;

    private static final Map<String, Object> SIMPLE_PROPERTIES_OBJ = new HashMap<String, Object>() {
        {
            put("userId", 123);
            put("name", "john");
            put("lastName", "doe");
            put("credit", 10.45);
            put("balance", -456.78);
            put("temperature", -10);
            put("observation", "");
            put("cpf", "111.222.333-44");
            put("married", true);
            put("hasChildren", false);
        }
    };
    private static final Map<String, Object> ARRAY_PROPERTY_OBJ = new HashMap<String, Object>() {
        {
            put("phones", Arrays.asList("(11) 2222-2222", "(22) 3333-3333", "(33) 4444-4444"));
        }
    };
    private static final Map<String, Object> COMPLEX_PROPERTY_OBJ = new HashMap<String, Object>() {
        {
            put("addresses", new HashMap<String, Object>() {
                {
                    put("home", "123 Evergreen St.");
                    put("work", "456 Elmer Blvd.");
                }
            });
        }
    };

    private static final String PLAIN_TEXT_BODY = "This is a plain text body and should not be parsed";
    private static final String URL_ENCODED_BODY =
            "userId=123&" +
            "name=john&" +
            "lastName=doe&" +
            "credit=10.45&" +
            "balance=-456.78&" +
            "temperature=-10&" +
            "observation=&" +
            "cpf=111.222.333-44&" +
            "married=true&" +
            "hasChildren=false";
    private static final String JSON_BODY =
            "{" +
            "    \"userId\": 123," +
            "    \"name\": \"john\"," +
            "    \"lastName\": \"doe\"," +
            "    \"cpf\": \"111.222.333-44\"," +
            "    \"credit\": 10.45," +
            "    \"balance\": -456.78," +
            "    \"temperature\": -10," +
            "    \"observation\": \"\"," +
            "    \"married\": true," +
            "    \"hasChildren\": false," +
            "    \"phones\": [" +
            "        \"(11) 2222-2222\"," +
            "        \"(22) 3333-3333\"," +
            "        \"(33) 4444-4444\"" +
            "    ]," +
            "    \"addresses\": {" +
            "        \"home\": \"123 Evergreen St.\"," +
            "        \"work\": \"456 Elmer Blvd.\"" +
            "    }" +
            "}";
    private static final String XML_BODY =
            "<userId>123</userId>" +
            "<name>john</name>" +
            "<lastName>doe</lastName>" +
            "<cpf>111.222.333-44</cpf>" +
            "<credit>10.45</credit>" +
            "<balance>-456.78</balance>" +
            "<temperature>-10</temperature>" +
            "<observation/>" +
            "<married>true</married>" +
            "<hasChildren>false</hasChildren>" +
            "<phones>(11) 2222-2222</phones>" +
            "<phones>(22) 3333-3333</phones>" +
            "<phones>(33) 4444-4444</phones>" +
            "<addresses>" +
            "   <home>123 Evergreen St.</home>" +
            "   <work>456 Elmer Blvd.</work>" +
            "</addresses>";

    /*
     * Testing parseBody
     */
    @Test
    public void parseBodyForNullContentType() {
        givenWeHaveANullContentType();
        givenWeHaveAValidTextBody();
        whenWeCallParseBody();
        thenWeExpectTheReturnToBeTheSameAsWeSend();
        thenWeExpectTheReturnedObjectToBeAString();
    }

    @Test
    public void parseBodyForPlainTextType() {
        givenWeHaveAPlainTextContentType();
        givenWeHaveAValidTextBody();
        whenWeCallParseBody();
        thenWeExpectTheReturnToBeTheSameAsWeSend();
        thenWeExpectTheReturnedObjectToBeAString();
    }

    @Test
    public void parseBodyForUrlEncodedType() {
        givenWeHaveAUrlEncodedContentType();
        givenWeHaveAValidUrlEncodedBody();
        whenWeCallParseBody();
        thenWeExpectTheReturnedObjectToBeAMapOfStringObject();
        thenWeExpectTheAMapShouldContainsTheSimpleProperties();
    }

    @Test
    public void parseBodyForJsonType() {
        givenWeHaveAJsonContentType();
        givenWeHaveAValidJsonBody();
        whenWeCallParseBody();
        thenWeExpectTheReturnedObjectToBeAMapOfStringObject();
        thenWeExpectTheAMapShouldContainsTheSimpleProperties();
        thenWeExpectTheAMapShouldContainsTheArrayProperties();
        thenWeExpectTheAMapShouldContainsTheComplexProperties();
    }

    @Test
    public void parseBodyForApplicationXmlType() {
        givenWeHaveAnApplicationXmlContentType();
        givenWeHaveAValidXmlBody();
        whenWeCallParseBody();
        thenWeExpectTheReturnedObjectToBeAMapOfStringObject();
        thenWeExpectTheAMapShouldContainsTheSimpleProperties();
        thenWeExpectTheAMapShouldContainsTheArrayProperties();
        thenWeExpectTheAMapShouldContainsTheComplexProperties();
    }

    @Test
    public void parseBodyForTextXmlType() {
        givenWeHaveATextXmlContentType();
        givenWeHaveAValidXmlBody();
        whenWeCallParseBody();
        thenWeExpectTheReturnedObjectToBeAMapOfStringObject();
        thenWeExpectTheAMapShouldContainsTheSimpleProperties();
        thenWeExpectTheAMapShouldContainsTheArrayProperties();
        thenWeExpectTheAMapShouldContainsTheComplexProperties();
    }

    @Test(expected = BadRequestException.class)
    public void parseBodyWithWrongBodyForJsonContentType() {
        givenWeHaveAJsonContentType();
        givenWeHaveAValidTextBody();
        whenWeCallParseBody();
        thenWeExpectABadRequestException();
    }

    @Test(expected = BadRequestException.class)
    public void parseBodyWithWrongBodyForApplicationXmlContentType() {
        givenWeHaveAnApplicationXmlContentType();
        givenWeHaveAValidTextBody();
        whenWeCallParseBody();
        thenWeExpectABadRequestException();
    }

    @Test(expected = BadRequestException.class)
    public void parseBodyWithWrongBodyForTextXmlContentType() {
        givenWeHaveATextXmlContentType();
        givenWeHaveAValidTextBody();
        whenWeCallParseBody();
        thenWeExpectABadRequestException();
    }

    @Test(expected = BadRequestException.class)
    public void parseBodyWithWrongBodyForTextUrlEncodedContentType() {
        givenWeHaveAUrlEncodedContentType();
        givenWeHaveAValidTextBody();
        whenWeCallParseBody();
        thenWeExpectABadRequestException();
    }

    /*
     * Given methods
     */
    private void givenWeHaveANullContentType() {
        this.contentType = null;
    }

    private void givenWeHaveAPlainTextContentType() {
        this.contentType = ContentType.PLAIN_TEXT;
    }

    private void givenWeHaveAValidTextBody() {
        this.body = PLAIN_TEXT_BODY;
    }

    private void givenWeHaveAUrlEncodedContentType() {
        this.contentType = ContentType.URL_ENCODED;
    }

    private void givenWeHaveAValidUrlEncodedBody() {
        this.body = URL_ENCODED_BODY;
    }

    private void givenWeHaveAJsonContentType() {
        this.contentType = ContentType.JSON;
    }

    private void givenWeHaveAValidJsonBody() {
        this.body = JSON_BODY;
    }

    private void givenWeHaveAnApplicationXmlContentType() {
        this.contentType = ContentType.APP_XML;
    }

    private void givenWeHaveATextXmlContentType() {
        this.contentType = ContentType.XML;
    }

    private void givenWeHaveAValidXmlBody() {
        this.body = XML_BODY;
    }

    /*
     * When methods
     */
    private void whenWeCallParseBody() {
        this.parsed = RequestBodyParser.parseBody(this.body, this.contentType);
    }

    /*
     * Then methods
     */
    private void thenWeExpectTheReturnToBeTheSameAsWeSend() {
        assertEquals(this.body, this.parsed);
    }

    @SuppressWarnings("unchecked")
    private void thenWeExpectTheAMapShouldContainsTheSimpleProperties() {
        Map<String, Object> parsed = (Map<String, Object>) this.parsed;
        for(Map.Entry<String, Object> prop : SIMPLE_PROPERTIES_OBJ.entrySet()) {
            assertTrue(parsed.containsKey(prop.getKey()));
            assertEquals(prop.getValue(), parsed.get(prop.getKey()));
        }
    }

    @SuppressWarnings("unchecked")
    private void thenWeExpectTheAMapShouldContainsTheArrayProperties() {
        Map<String, Object> parsed = (Map<String, Object>) this.parsed;
        for(Map.Entry<String, Object> prop : ARRAY_PROPERTY_OBJ.entrySet()) {
            assertTrue(parsed.containsKey(prop.getKey()));
            assertEquals(prop.getValue(), parsed.get(prop.getKey()));
        }
    }

    @SuppressWarnings("unchecked")
    private void thenWeExpectTheAMapShouldContainsTheComplexProperties() {
        Map<String, Object> parsed = (Map<String, Object>) this.parsed;
        for(Map.Entry<String, Object> prop : COMPLEX_PROPERTY_OBJ.entrySet()) {
            assertTrue(parsed.containsKey(prop.getKey()));
            assertEquals(prop.getValue(), parsed.get(prop.getKey()));
        }
    }

    private void thenWeExpectTheReturnedObjectToBeAString() {
        assertEquals("java.lang.String", this.parsed.getClass().getName());
    }

    private void thenWeExpectTheReturnedObjectToBeAMapOfStringObject() {
        assertEquals("java.util.HashMap", this.parsed.getClass().getName());
    }

    private void thenWeExpectABadRequestException() {
        // asserting at test scope
    }
}
