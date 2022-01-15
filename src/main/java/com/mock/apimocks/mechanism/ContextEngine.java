package com.mock.apimocks.mechanism;

import com.mock.apimocks.contants.ConditionEngineScope;
import com.mock.apimocks.models.CallContext;

import javax.script.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class describes the Context Engine.
 * <p/>
 * This engine is meant to build and analyse mock contexts.
 * <p/>
 * In here we can find methods to create url validators, validate urls with those validators and analyse and evaluate
 * scenario conditions
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public class ContextEngine {
    private static final String JAVASCRIPT_ENGINE = "nashorn";

    /**
     * Default class constructor.
     * <p/>
     * It is set as private because this class only should have static methods
     */
    private ContextEngine() {
    }

    /**
     * Creates a verifier for a given mock URL. The verifier is basically
     * a Regular Expression that is sensitive enough to infer things as
     * path parameters' placeholder.
     * <p/>
     * This verifier could be used to validate a URL and checks if it
     * has the same context of the mocked call.
     *
     * @param url the mocked URL
     * @return a regular expression with the verifier string of the mocked URL
     */
    public static String createUrlVerifier(String url) {
        // sanitizing url
        String verifier = sanitizeUrl(url);

        // replacing slashes on path by "one or many" expression
        // adding grouping for path parameter placeholders
        verifier = verifier
                .replace("/", "\\/+")
                .replaceAll("\\{(.*?)}", "([^\\\\/]+)");

        // adding group delimiter
        return "(" + verifier + ")";
    }

    /**
     * Verifies if a given URL belongs to a context by matching it with a
     * pre-built verifier.
     *
     * @param url      the URL to be checked.
     * @param verifier the URL verifier.
     * @return a flag that indicates whether the URL is valid or not.
     */
    public static boolean verifyUrl(String url, String verifier) {
        Pattern pattern = Pattern.compile(verifier);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    /**
     * Get the path parameter names and values on a map.
     *
     * @param mockUrl    the mock URL that contains the path parameter placeholders and names
     * @param verifier   the verifier regular expression with the validators
     * @param requestUrl the requested URL with the path parameter values
     * @return a {@link Map} with the path parameters names and values
     */
    public static Map<String, String> getPathParameters(String mockUrl, String verifier, String requestUrl) {
        Map<String, String> pathParams = new HashMap<>();

        // getting a list of ordered path parameter names from the mocked URL
        // the parameter placeholders will be delimited by curly brackets {}
        List<String> paramNames = getPathParamName(mockUrl);

        // checking the parameter values on the requested url
        Pattern pattern = Pattern.compile(verifier);
        Matcher matcher = pattern.matcher(requestUrl);

        // we should start at the second group position because the first
        // group will always contain the full matched string, the following
        // ones will contain the path parameters
        while (matcher.find()) {
            for (int i = 2; i <= matcher.groupCount(); i++) {
                pathParams.put(paramNames.get(i - 2), matcher.group(i));
            }
        }
        return pathParams;
    }

    /**
     * Evaluate a given condition based on a call context
     *
     * @param context the call context with the request variables
     * @param condition the condition to be evaluated
     * @return a flag with the valuation result
     */
    public static boolean evaluateCondition(CallContext context, String condition) {
        boolean isValid = false;
        try {
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName(JAVASCRIPT_ENGINE);

            ScriptContext ctx = new SimpleScriptContext();
            ctx.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
            ctx.setAttribute(ConditionEngineScope.HEADER, context.getHeaders(), ScriptContext.ENGINE_SCOPE);
            ctx.setAttribute(ConditionEngineScope.QUERY_PARAM, context.getQueryParams(), ScriptContext.ENGINE_SCOPE);
            ctx.setAttribute(ConditionEngineScope.PATH_PARAM, context.getPathParams(), ScriptContext.ENGINE_SCOPE);
            ctx.setAttribute(ConditionEngineScope.BODY, context.getParsedBody(), ScriptContext.ENGINE_SCOPE);

            isValid = (boolean) engine.eval(condition, ctx);
        } catch(Exception ex) {
            // At this point is irrelevant if the condition fails, and so,
            // we must handle any evaluation errors as a miss
            // and so, we must simply bury this exception
        }
        return isValid;
    }

    /**
     * Sanitize the given URL.
     * <p/>
     * By sanitation we mean removing all the misplaced, double, and
     * unnecessary slashes of it.
     *
     * @param url the URL to be sanitized
     * @return a sanitized URL.
     */
    public static String sanitizeUrl(String url) {
        // removing all occurrences of misplaced doubled slashes
        url = url.replaceAll("(/+)", "/");

        // removing query parameters
        if(url.contains("?"))
            url = url.substring(0, url.indexOf("?"));

        // removing the slash at the end of the string (in case there's any)
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Getting the path parameter names on the mock URL
     *
     * @param url the mock URL with the path parameter placeholders
     * @return a List of path parameters ordered by occurrences
     */
    private static List<String> getPathParamName(String url) {
        List<String> params = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<=\\{)(.*?)(?=})");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            params.add(matcher.group(1));
        }
        return params;
    }
}
