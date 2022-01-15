package com.mock.apimocks.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * This enum represents a Http Content-Type.
 * <p/>
 * Currently, this project supports three kinds of content types:
 * <ul>
 *     <li>JSON ("application/json")</li>
 *     <li>XML ("text/xml" / "application/xml")</li>
 *     <li>URL_ENCODED ("application/x-www-form-urlencoded")</li>
 *     <li>PLAIN_TEXT ("text_plain")</li>
 * </ul>
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public enum ContentType {
    JSON("application/json"), XML("text/xml"), APP_XML("application/xml"), URL_ENCODED("application/x-www-form-urlencoded"), PLAIN_TEXT("text/plain");

    private String mime;
    ContentType(String mime) {
        this.mime = mime;
    }

    public String mime() {
        return this.mime;
    }

    public static Optional<ContentType> get(String header) {
        return Arrays.stream(ContentType.values())
                .filter(ct -> ct.mime.equalsIgnoreCase(header))
                .findFirst();
    }
}
