package org.example.util;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @descriptions: 解析avro工具类
 * @author: zhangfaquan
 * @date: 2021/7/29 9:30
 * @version: 1.0
 */
public class AvroUtils {

    private static final Logger logger = LoggerFactory.getLogger(AvroUtils.class);

    private AvroUtils() {
    }

    public static List<Map<String, Object>> getAvroList(String schemaStr, byte[] dataBuf) throws Exception {
        Schema schema = parseSchema(schemaStr);
        return getAvroList(schema, dataBuf);
    }

    /**
     * @descriptions 反序列化
     * @param schema schema
     * @param dataBuf avro格式数据的字节数组
     * @return
     */
    public static List<Map<String, Object>> getAvroList(Schema schema, byte[] dataBuf) throws Exception {

        List<Map<String, Object>> avroList = new ArrayList<>();
        deserializeAvro(schema, dataBuf, (dataFileStream, schemaObject) -> {
            GenericRecord record = null;
            Map<String, Object> sourceMsgMap = null;
            while (dataFileStream.hasNext()) {
                record = dataFileStream.next();
                if (record == null)
                    continue;
                sourceMsgMap = new HashMap<>();
                for (Schema.Field field : schemaObject.getFields()) {
                    String name = field.name();
                    sourceMsgMap.put(name, record.get(name));
                }
                avroList.add(sourceMsgMap);
            }
        });
        return avroList.isEmpty() ? null : avroList;
    }

    public static InputStream getSchemaFromFile(String filePath) throws FileNotFoundException {
        return FileUtils.getFileStream(filePath);
    }

    public static Schema parseSchema(String schemaStr) {
        Schema.Parser parser = new Schema.Parser();
        return parser.parse(schemaStr);
    }

    public static Schema parseSchema(InputStream schemaStream) throws IOException {
        Schema.Parser parser = new Schema.Parser();
        return parser.parse(schemaStream);
    }

    /**
     * @descriptions 反序列化
     * @param schema schema
     * @param dataBuf avro格式数据的字节数组
     * @param biConsumer 处理数据的函数
     * @return
     */
    public static void deserializeAvro(Schema schema, byte[] dataBuf, BiConsumer<DataFileStream<GenericRecord>, Schema> biConsumer) throws Exception {
        InputStream in = new ByteArrayInputStream(dataBuf);
        deserializeAvro(schema, in, biConsumer);
    }

    /**
     * @descriptions 功能描述
     * @param schema schema
     * @param in avro格式数据的输入流
     * @param biConsumer 处理数据的函数
     * @return
     */
    public static void deserializeAvro(Schema schema, InputStream in, BiConsumer<DataFileStream<GenericRecord>, Schema> biConsumer) throws Exception {

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);

        DataFileStream<GenericRecord> dataFileStream = null;
        try {
            dataFileStream = new DataFileStream<>(in, datumReader);
            // 处理数据
            biConsumer.accept(dataFileStream, schema);
        } finally {
            try {
                if (dataFileStream != null)
                    dataFileStream.close();
            } catch (IOException e) {
                logger.error("资源释放失败");
            }
        }
    }

    public static byte[] serializeAvro(String schemaStr, Map<String, Object> map) throws IOException {
        Schema schema = parseSchema(schemaStr);
        GenericRecord genericRecord = new GenericData.Record(schema);
        map.forEach(genericRecord::put);
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter).create(schema, outputStream)) {
            dataFileWriter.append(genericRecord);
        }
        return outputStream.toByteArray();
    }
}
