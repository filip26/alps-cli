package com.apicatalog.alps.xml;
/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.apicatalog.alps.AlpsParserException;
import com.apicatalog.alps.dom.AlpsDocument;

class AlpsJsonTestSuite {

    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testCase(AlpsParserTestCase testCase) throws IOException {
        
        assertNotNull(testCase);
        assertNotNull(testCase.getInput());
        
        AlpsDocument document = null;
        
        try (final InputStream is = AlpsJsonTestSuite.class.getResourceAsStream(testCase.getInput())) {
            
            assertNotNull(is);
            
            document = (new AlpsXmlParser()).parse(URI.create("http://example.com"), "application/xml", is);
            
        } catch (AlpsParserException e) {
            fail(e.getMessage(), e);
        }
        
        assertNotNull(document);
        
        compare(testCase, document);
    }
    
    static final Stream<AlpsParserTestCase> testCaseMethodSource() throws IOException {
        
        try (final InputStream is = AlpsJsonTestSuite.class.getResourceAsStream("manifest.json")) {
            
            assertNotNull(is);
            
            final JsonParser jsonParser = Json.createParser(is);
            
            jsonParser.next();
            
            JsonArray tests = jsonParser.getObject().getJsonArray("sequence");
            
            return tests.stream().map(JsonObject.class::cast).map(AlpsParserTestCase::of);
        }
    }
    
    static final void compare(final AlpsParserTestCase testCase, final AlpsDocument document) {

        if (testCase.getExpected() == null) {
            return;
        }

        //TODO comparison actual vs expected
    }    
}
