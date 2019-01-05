package org.platformlambda.core.util;

import org.junit.Test;
import org.platformlambda.core.models.EventEnvelope;
import org.platformlambda.models.ObjectWithGenericType;
import org.platformlambda.core.util.models.PoJo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenericTypeTest {

    @Test
    @SuppressWarnings("unchecked")
    public void correctParametricType() throws IOException {

        int id = 100;
        String name = "hello world";
        ObjectWithGenericType<PoJo> genericObject = new ObjectWithGenericType<>();
        PoJo pojo = new PoJo();
        pojo.setName(name);
        genericObject.setContent(pojo);
        genericObject.setId(id);

        EventEnvelope event = new EventEnvelope();
        event.setBody(genericObject);
        event.setParametricType(PoJo.class);
        byte[] b = event.toBytes();

        EventEnvelope result = new EventEnvelope();
        result.load(b);

        Object o = result.getBody();
        assertTrue(o instanceof ObjectWithGenericType);
        ObjectWithGenericType<PoJo> gs = (ObjectWithGenericType<PoJo>) o;
        assertEquals(id, gs.getId());
        PoJo content = gs.getContent();
        assertNotNull(content);
        assertEquals(name, content.getName());
    }

    @Test(expected=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void missingTypingInfo() throws IOException {

        int id = 100;
        String name = "hello world";
        ObjectWithGenericType<PoJo> genericObject = new ObjectWithGenericType<>();
        PoJo pojo = new PoJo();
        pojo.setName(name);
        genericObject.setContent(pojo);
        genericObject.setId(100);

        EventEnvelope event = new EventEnvelope();
        event.setBody(genericObject);
        byte[] b = event.toBytes();

        EventEnvelope result = new EventEnvelope();
        result.load(b);

        Object o = result.getBody();
        assertTrue(o instanceof ObjectWithGenericType);
        ObjectWithGenericType<PoJo> gs = (ObjectWithGenericType<PoJo>) o;
        // all fields except the ones with generic types can be deserialized correctly
        assertEquals(id, gs.getId());
        /*
         * without parametricType defined, this will throw ClassCastException because the value is a HashMap.
         *
         * Note that Java class with generic types is not type-safe.
         * You therefore can retrieve a copy of the HashMap by this:
         * Object content = gs.getContent();
         */
        PoJo content = gs.getContent();
        assertNotNull(content);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invalidParametricType() throws IOException {
        int id = 100;
        String name = "hello world";
        ObjectWithGenericType<PoJo> genericObject = new ObjectWithGenericType<>();
        PoJo pojo = new PoJo();
        pojo.setName(name);
        genericObject.setContent(pojo);
        genericObject.setId(id);

        EventEnvelope event = new EventEnvelope();
        event.setBody(genericObject);
        event.setParametricType(String.class);  // setting an incorrect type
        byte[] b = event.toBytes();

        EventEnvelope result = new EventEnvelope();
        result.load(b);

        // when parametricType is incorrect, it will fall back to a Map
        Object o = result.getBody();

        System.out.println(o);

        assertTrue(o instanceof HashMap);
        // and we can retrieve the correct key-values
        MultiLevelMap m = new MultiLevelMap((Map<String, Object>) o);
        assertEquals(id, m.getElement("id"));
        assertEquals(name, m.getElement("content.name"));
    }

}