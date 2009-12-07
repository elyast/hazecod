package org.robotframework.jdiameter.mapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robotframework.jdiameter.mapper.MappingReader;

public class MappingReaderTest {

    private static final String MAPPING_PROPERTIES_FILE_NAME = "mapping.properties";

    private static final String KEY_FOR_MULTIPLE_MAPPING = "D";
    private static final String KEY_FOR_SINGLE_MAPPING = "E";
    private static final String KEY_FOR_EMPTY_MAPPING = "F";
    private static final String KEY_FOR_DUPLICATED_MAPPING = "G";
    private static final String NOT_EXISTING_KEY = "H";

    private static final List<String> MULTIPLE_MAPPING = new ArrayList<String>();
    private static final List<String> SINGLE_MAPPING = new ArrayList<String>();
    private static final List<String> EMPTY_MAPPING = new ArrayList<String>();
    private static final List<String> DUPLICATED_MAPPING = new ArrayList<String>();

    static {
	MULTIPLE_MAPPING.add("A.B");
	MULTIPLE_MAPPING.add("A.B.C");

	SINGLE_MAPPING.add("A.B");

	DUPLICATED_MAPPING.add("A.B");
	DUPLICATED_MAPPING.add("A.B");
    }

    private MappingReader testObj;

    @Before
    public void setUp() throws Exception {
	testObj = new MappingReader();
	testObj.loadPropertiesFile(ClassLoader
		.getSystemResourceAsStream(MAPPING_PROPERTIES_FILE_NAME));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMapping_existingKey() {
	testGetMapping(KEY_FOR_SINGLE_MAPPING, SINGLE_MAPPING);
    }

    @Test
    public void testGetMapping_existingKeyMultipleMapping() {
	testGetMapping(KEY_FOR_MULTIPLE_MAPPING, MULTIPLE_MAPPING);
    }

    @Test
    public void testGetMapping_existingKeyDuplicatedMapping() {
	testGetMapping(KEY_FOR_DUPLICATED_MAPPING, DUPLICATED_MAPPING);
    }

    @Test
    public void testGetMapping_existingKeyEmptyMapping() {
	testGetMapping(KEY_FOR_EMPTY_MAPPING, EMPTY_MAPPING);
    }

    @Test
    public void testGetMapping_notExistingKey() {
	testGetMapping(NOT_EXISTING_KEY, EMPTY_MAPPING);
    }

    @Test
    public void testGetMapping_nullKey() {
	testGetMapping(null, EMPTY_MAPPING);
    }

    @Test
    public void testGetMapping_emptyStringKey() {
	testGetMapping("", EMPTY_MAPPING);
    }

    private void testGetMapping(String key, List<String> expectedMapping) {
	List<String> mapping = testObj.getMapping(key);
	assertEquals(expectedMapping, mapping);
    }
}
