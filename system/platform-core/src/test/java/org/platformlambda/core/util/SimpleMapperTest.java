package org.platformlambda.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.platformlambda.core.serializers.SimpleMapper;
import org.platformlambda.core.util.models.PoJo;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SimpleMapperTest {

    @Test
    public void mapperSerializationTest() throws IOException {

        ObjectMapper mapper = SimpleMapper.getInstance().getMapper();

        Date now = new Date();
        String iso8601 = Utility.getInstance().date2str(now);

        Map<String, Object> map = new HashMap<>();
        map.put("date", now);
        map.put("sql_time", new java.sql.Date(now.getTime()));
        map.put("sql_timestamp", new java.sql.Timestamp(now.getTime()));

        Map converted = mapper.readValue(mapper.writeValueAsString(map), Map.class);
        // verify that java.util.Date, java.sql.Date and java.sql.Timestamp can be serialized to ISO-8601 string format
        assertEquals(iso8601, converted.get("date"));
        assertEquals(iso8601, converted.get("sql_time"));
        assertEquals(iso8601, converted.get("sql_timestamp"));

        String name = "hello world";
        Map<String, Object> input = new HashMap<>();
        input.put("full_name", name);
        input.put("date", iso8601);
        PoJo pojo = mapper.convertValue(input, PoJo.class);
        // verify that the time is restored correctly
        assertEquals(now.getTime(), pojo.getDate().getTime());
        // verify that snake case is deserialized correctly
        assertEquals(name, pojo.getFullName());

        // verify input timestamp can be in milliseconds too
        input.put("date", now.getTime());
        pojo = mapper.convertValue(input, PoJo.class);
        assertEquals(now.getTime(), pojo.getDate().getTime());
    }

}