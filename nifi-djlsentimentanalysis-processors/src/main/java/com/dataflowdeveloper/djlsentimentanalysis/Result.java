package com.dataflowdeveloper.djlsentimentanalysis;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.StringJoiner;

/**
 *
 * Class: dog
 * Probabilties: 0.82268184
 * Coord:83.82353, 179.13997, 206.63783, 476.78754
 * @author tspann
 */
public class Result implements Serializable {

    private double probability;
    private double probabilityPercentage;

    private double probabilityNegative;
    private double probabilityNegativePercentage;

    private String rawClassification;

    public Result(double probability, double probabilityPercentage, double probabilityNegative, double probabilityNegativePercentage, String rawClassification) {
        super();
        this.probability = probability;
        this.probabilityPercentage = probabilityPercentage;
        this.probabilityNegative = probabilityNegative;
        this.probabilityNegativePercentage = probabilityNegativePercentage;
        this.rawClassification = rawClassification;
    }

    public String getRawClassification() {
        return rawClassification;
    }

    public void setRawClassification(String rawClassification) {
        this.rawClassification = rawClassification;
    }

    @Override
    public String toString() {
        return new StringJoiner( ", ", Result.class.getSimpleName() + "[", "]" )
                .add( "probability=" + probability )
                .add( "probabilityPercentage=" + probabilityPercentage )
                .add( "probabilityNegative=" + probabilityNegative )
                .add( "probabilityNegativePercentage=" + probabilityNegativePercentage )
                .add( "rawClassification='" + rawClassification + "'" )
                .toString();
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getProbabilityPercentage() {
        return probabilityPercentage;
    }

    public void setProbabilityPercentage(double probabilityPercentage) {
        this.probabilityPercentage = probabilityPercentage;
    }

    public double getProbabilityNegative() {
        return probabilityNegative;
    }

    public void setProbabilityNegative(double probabilityNegative) {
        this.probabilityNegative = probabilityNegative;
    }

    public double getProbabilityNegativePercentage() {
        return probabilityNegativePercentage;
    }

    public void setProbabilityNegativePercentage(double probabilityNegativePercentage) {
        this.probabilityNegativePercentage = probabilityNegativePercentage;
    }

    public Result() {
        super();
    }

    public Result(double probability, double probabilityPercentage, double probabilityNegative, double probabilityNegativePercentage) {
        super();
        this.probability = probability;
        this.probabilityPercentage = probabilityPercentage;
        this.probabilityNegative = probabilityNegative;
        this.probabilityNegativePercentage = probabilityNegativePercentage;
    }

}