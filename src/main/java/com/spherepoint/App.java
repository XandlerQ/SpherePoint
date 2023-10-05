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
    private double epsilon = 0.000001;
    private boolean done = false;
    private String solver;
    private String type;

    Positioner opti;

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        this.processingRef = this;
        background(0);
        frameRate(1000);
        Gson gson = new Gson();
        PojoProblem problem = null;
        try {
            JsonReader reader = new JsonReader(new FileReader("problem.json"));
            problem = gson.fromJson(reader, PojoProblem.class);
        }
        catch (Exception exception) {
            System.out.println(exception);
        }

        SphereDotFunction sphereDotFunction = null;
        this.solver = problem.getSolver();

        this.type = problem.getType();

        switch (this.type) {
            case "Sphere1" -> sphereDotFunction = new Sum1OverR(problem.getDotCount());
            case "Sphere2" -> sphereDotFunction = new NegativeMinR(problem.getDotCount());
        }
        this.opti = new Positioner(sphereDotFunction);
        this.opti.setStepSize(Math.PI / 12);
    }

    public void draw() {
        if (!this.done) {
            background(0);
            switch (this.solver) {
                case "HJ" -> this.done = this.opti.hookeJeevesStep(this.epsilon);
                case "GD" -> this.done = this.opti.gradientDescentOptimumStep(this.epsilon, 0.00000001);
            }
            App.processingRef.pushMatrix();
            App.processingRef.sphereDetail(30);
            App.processingRef.stroke(Color.WHITE.getRGB());
            App.processingRef.noFill();
            App.processingRef.translate(500, 500, 500);
            App.processingRef.sphere(100);
            App.processingRef.popMatrix();

            ArrayList<SphereDot> sphereDots = this.opti.getFunction().getSphereDots();

            for (Iterator<SphereDot> iterator = sphereDots.iterator(); iterator.hasNext();) {
                SphereDot sphereDot = iterator.next();
                sphereDot.render();
            }
        }
        else {
            System.out.println(this.opti.getIterationCount());
        }
    }

    public static void main(String[] args) {
        PApplet.main("com.spherepoint.App");
    }
}
