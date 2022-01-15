package com.mock.apimocks;

import com.mock.apimocks.controller.ControllerAdvice;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This abstract class represents a MVC Controller test
 * <p>
 * All the classes that extends this abstract class gain
 * the capability of mock REST responses using a
 * {@link MockMvc} object
 *
 * @author gabrielgn
 *
 * @param <T> the mocked controller type
 */
public abstract class MvcControllerTestable<T> {
    /*
     * Mock MVC declaration
     */
    protected MockMvc mvc;

    /**
     * Initialize the Mock MVN object used to mock our REST
     * responses for our tests on controller classes
     *
     * @param controller the controller to be mocked
     */
    protected void initializeMvc(T controller) {
        this.mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerAdvice())
                .build();
    }

    /**
     * Returns a serialized instance of the object in an array of bytes
     *
     * @return the object built on the builder process
     */
    protected byte[] toJson(Object object) throws JsonProcessingException {
        byte[] bytes = null;
        if (object != null) {
            ObjectMapper mapper = new ObjectMapper();
            bytes = mapper.writeValueAsBytes(object);
        }
        return bytes;
    }
}

