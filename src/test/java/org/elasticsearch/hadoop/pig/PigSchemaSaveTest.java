/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.elasticsearch.hadoop.pig;

import org.apache.pig.ResourceSchema;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.util.Utils;
import org.elasticsearch.hadoop.serialization.ContentBuilder;
import org.elasticsearch.hadoop.util.FastByteArrayOutputStream;
import org.elasticsearch.hadoop.util.IOUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class PigSchemaSaveTest {

    // fails in Pig 0.10+ (and probably 0.9 as well) due to some changes in 0.8/0.9
    // the cause seems to be the lack of a name for the tuple inside the bag
    // https://issues.apache.org/jira/browse/PIG-2509
    @Test(expected = Exception.class)
    public void testLoadingOfBagSchema() throws Exception {
        assertNotNull(Utils.getSchemaFromString(Utils.getSchemaFromString("name:bytearray,links:{(missing:chararray)}").toString()));
    }

    @Test
    public void testSchemaSerializationPlusBase64() throws Exception {
        Schema schemaFromString = Utils.getSchemaFromString("name:bytearray,links:{(missing:chararray)}");
        Schema schemaSaved = IOUtils.deserializeFromBase64(IOUtils.serializeToBase64(schemaFromString));
        assertEquals(schemaFromString.toString(), schemaSaved.toString());
    }

    @Test
    public void testSchemaToJsonTest() throws Exception {
        ResourceSchema schema = new ResourceSchema(Utils.getSchemaFromString("name:bytearray,links:{(missing:chararray)}"));
        System.out.println(schema.toString());

        ContentBuilder builder = ContentBuilder.generate(new PigSchemaWriter("test")).value(schema).flush();
        FastByteArrayOutputStream out = ((FastByteArrayOutputStream) builder.content());
        builder.close();
        System.out.println(new String(out.bytes().toString()));
    }
}
