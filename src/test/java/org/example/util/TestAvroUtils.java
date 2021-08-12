package org.example.util;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAvroUtils {

    private static final String schemaStr = "{\"namespace\": \"com.best.avro.test.bean\",\n" +
            " \"type\": \"record\",\n" +
            " \"name\": \"Teacher\",\n" +
            " \"fields\": [\n" +
            "     {\"name\": \"name\", \"type\": \"string\"},\n" +
            "     {\"name\": \"favorite_number\",  \"type\": [\"int\", \"null\"]},\n" +
            "     {\"name\": \"favorite_color\", \"type\": [\"string\", \"null\"]}\n" +
            " ]\n" +
            "}";

    @Test
    public void testGetAvroList() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Kami");
        data.put("favorite_number", 18);
        data.put("favorite_color", "Red");
        byte[] bytes = AvroUtils.serializeAvro(schemaStr, data);

        List<Map<String, Object>> avroList = AvroUtils.getAvroList(schemaStr, bytes);
        avroList.forEach(System.out::println);
    }

    @Test
    public void testParseSchema() throws IOException {

        InputStream in = new ByteArrayInputStream(schemaStr.getBytes());
        Schema schema = AvroUtils.parseSchema(in);
        System.out.println(schema);
    }
}
