package com.dataflowdeveloper.djlsentimentanalysis;

import ai.djl.modality.Classifications;
import jdk.nashorn.internal.objects.NativeArray;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class DeepLearningSAProcessorTest {

    private TestRunner testRunner;

    @Before
    public void init() {
        testRunner = TestRunners.newTestRunner(DeepLearningSAProcessor.class);
    }


    private String pathOfResource(String name) throws URISyntaxException {
        URL r = this.getClass().getClassLoader().getResource(name);
        URI uri = r.toURI();
        return Paths.get(uri).toAbsolutePath().getParent().toString();
    }

    private void runAndAssertHappy() {
        testRunner.setValidateExpressionUsage(false);
        testRunner.run();
        testRunner.assertValid();

        testRunner.assertAllFlowFilesTransferred(DeepLearningSAProcessor.REL_SUCCESS);
        List<MockFlowFile> successFiles = testRunner.getFlowFilesForRelationship(DeepLearningSAProcessor.REL_SUCCESS);

        for (MockFlowFile mockFile : successFiles) {

            System.out.println("Size:" +             mockFile.getSize() ) ;
            Map<String, String> attributes =  mockFile.getAttributes();

            for (String attribute : attributes.keySet()) {
                System.out.println("Attribute:" + attribute + " = " + mockFile.getAttribute(attribute));
            }
        }
    }

    @Test
    public void testProcessor() {
    	testRunner.setProperty(DeepLearningSAProcessor.MESSAGE_NAME, "This is the best ingest tool ever, so great.");
    	testRunner.enqueue();
        runAndAssertHappy();
    }

}
