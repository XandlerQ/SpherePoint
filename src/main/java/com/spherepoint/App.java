package com.spherepoint;

import processing.core.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.awt.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

public class App extends PApplet{
    public static PApplet processingRef;
    public static float renderRadius;
    public static int width;
    public static int height;
    private double epsilon = 0.000001;
    private boolean done = false;
    private boolean report = false;
    private String solver;
    private String type;

    Positioner positioner;

    public void settings() {
        width = 500;
        height = 500;
        size(width, height, P3D);
    }

    public void setup() {
        processingRef = this;
        renderRadius = 150;
        background(0);
        frameRate(15);
        Gson gson = new Gson();
        PojoProblem problem = null;
        try {
            JsonReader reader = new JsonReader(new FileReader("problem.json"));
            problem = gson.fromJson(reader, PojoProblem.class);
        }
        catch (Exception exception) {
            System.out.println(exception);
        }

        SpherePointFunction spherePointFunction = null;
        this.solver = problem.getSolver();

        this.type = problem.getType();

        switch (this.type) {
            case "1R" -> spherePointFunction = new Sum1OverR(problem.getPointCount());
            case "Min" -> spherePointFunction = new NegativeMinR(problem.getPointCount());
        }
        this.positioner = new Positioner(spherePointFunction);
        this.positioner.setStepSize(Math.PI / 12);
    }

    public void draw() {
        if (!this.done) {
            background(255);
            switch (this.solver) {
                case "HJ" -> this.done = this.positioner.hookeJeevesStep(this.epsilon);
                case "GD" -> this.done = this.positioner.gradientDescentOptimumStep(this.epsilon, 0.00000001);
            }
            App.processingRef.pushMatrix();
            App.processingRef.lights();
            App.processingRef.sphereDetail(20);
            App.processingRef.stroke(Color.BLACK.getRGB(), 100);
            App.processingRef.noFill();
            App.processingRef.translate(width / 2, height / 2, 0);
            App.processingRef.sphere(renderRadius);
            App.processingRef.popMatrix();

            ArrayList<SpherePoint> spherePoints = this.positioner.getFunction().getSpherePoints();

            for (Iterator<SpherePoint> iterator = spherePoints.iterator(); iterator.hasNext();) {
                SpherePoint spherePoint = iterator.next();
                spherePoint.render();
            }
        }
        else {
            if (!report) {
                System.out.println(this.positioner.getIterationCount());
                System.out.println(this.positioner.getFunction().getValue());
                report = true;
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("com.spherepoint.App");
    }
}
