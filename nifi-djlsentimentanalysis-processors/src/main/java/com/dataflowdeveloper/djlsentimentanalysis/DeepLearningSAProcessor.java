package com.dataflowdeveloper.djlsentimentanalysis;

import ai.djl.modality.cv.Image;
import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.*;

@EventDriven
@SupportsBatching
@SideEffectFree
@Tags({ "djl", "nlp", "models", "sentiment analysis", "Java Deep Learning", "deep learning" })
@CapabilityDescription("Run Sentiment Analysis Models with DJL")
@SeeAlso({})
@WritesAttributes({ @WritesAttribute(attribute = "className", description = "Sentiment") })
/**
 *
 * @author tspann  Timothy Spann
 *
 */
public class DeepLearningSAProcessor extends AbstractProcessor {

    // Input variables
    public static final String MESSAGE_NAME = "message";
    public static final String OUTPUT_PREDICTION = "prediction";
    public static final String OUTPUT_ERROR = "error";

    // properties
//    public static final PropertyDescriptor QUESTION = new PropertyDescriptor.Builder().name( QUESTION_NAME )
//            .description("Question").required(true).defaultValue("What")
//            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public static final PropertyDescriptor PARAGRAPH = new PropertyDescriptor.Builder().name(MESSAGE_NAME)
            .description("Paragraph").required(true).defaultValue("...")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    // Relationships
    public static final Relationship REL_SUCCESS = new Relationship.Builder().name("success")
            .description("Successfully determined image.").build();
    public static final Relationship REL_FAILURE = new Relationship.Builder().name("failure")
            .description("Failed to determine image.").build();
    public static final String FILENAME = "filename";

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    // DL4J Deep Learning Service
    private SentimentAnalysisService service;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        descriptors.add(PARAGRAPH);

        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(REL_SUCCESS);
        relationships.add(REL_FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {
        service = new SentimentAnalysisService();
        return;
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            flowFile = session.create();
        }
        try {
            String message = flowFile.getAttribute(MESSAGE_NAME);
            if (message == null) {
                message = context.getProperty(MESSAGE_NAME).evaluateAttributeExpressions(flowFile).getValue();
            }

            Map<String, String> attributes = flowFile.getAttributes();
            Map<String, String> attributesClean = new HashMap<>();
            
            Result result = service.predict( message );

            if (result != null) {
                try {
                    attributesClean.put(String.format("probpositive"), String.format("%.2f", result.getProbability()));
                    attributesClean.put(String.format("probpositiveperc"), String.format("%.2f", result.getProbabilityPercentage()));
                    attributesClean.put(String.format("probnegative"), String.format("%.2f", result.getProbabilityNegative()));
                    attributesClean.put(String.format("probnegativeperc"), String.format("%.2f", result.getProbabilityNegativePercentage()));
                    attributesClean.put(String.format("rawclassification"), String.format("%s", result.getRawClassification()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (attributes.size() == 0) {
                session.transfer(flowFile, REL_FAILURE);
            } else {
                flowFile = session.putAllAttributes(flowFile, attributesClean);
                session.transfer(flowFile, REL_SUCCESS);
            }
            session.commit();
        } catch (final Throwable t) {
            getLogger().error("Unable to process Deep Learning Sentiment Analysis DL " + t.getLocalizedMessage());
            getLogger().error("{} failed to process due to {}; rolling back session", new Object[] { this, t });
        }
    }
}
