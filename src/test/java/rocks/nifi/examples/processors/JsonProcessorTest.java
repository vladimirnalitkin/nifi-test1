package rocks.nifi.examples.processors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;

import static org.junit.Assert.*;

/**
 * @author phillip
 */
public class JsonProcessorTest {

    static final String JSON_RESULT = "hi Apache NIFI";

    /**
     * Test of onTrigger method, of class JsonProcessor.
     */
    @org.junit.Test
    public void testOnTrigger() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("files/test.json").getFile());

        // Content to be mock a json file
        InputStream content = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));

        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new JsonProcessor());

        // Add properties
        runner.setProperty(JsonProcessor.JSON_PATH, "$.hello");

        // Add the content to the runner
        runner.enqueue(content);

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do additional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(JsonProcessor.SUCCESS);
        assertTrue("1 match", results.size() == 1);
        MockFlowFile result = results.get(0);
        String resultValue = new String(runner.getContentAsByteArray(result));
        System.out.println("Match: " + IOUtils.toString(runner.getContentAsByteArray(result)));

        // Test attributes and content
        result.assertAttributeEquals(JsonProcessor.MATCH_ATTR, JSON_RESULT);
        result.assertContentEquals(JSON_RESULT);

    }

}
