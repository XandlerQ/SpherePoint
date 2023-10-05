package com.spherepoint;

import java.awt.*;
import java.util.Random;

public class SphereDot {
    private double phi;
    private double theta;

    SphereDot() {
        this.phi = 0;
        this.theta = 0;
    }

    SphereDot(double phi, double omega) {
        this.phi = phi;
        this.theta = omega;
    }

    public static SphereDot randomDot() {
        Random random = new Random();
        return new SphereDot(random.nextDouble() * 2 * Math.PI, random.nextDouble() * Math.PI);
    }

    public double getPhi() {
        return phi;
    }

    public double getTheta() {
        return theta;
    }

    public double getX() {
        return Math.sin(this.theta) * Math.cos(this.phi);
    }

    public double getY() {
        return Math.sin(this.theta) * Math.sin(this.phi);
    }

    public double getZ() {
        return Math.cos(this.theta);
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public void adjustTheta(double value) {
        this.theta += value;
        normalizeTheta();
    }

    public void adjustPhi(double value) {
        this.phi += value;
        normalizePhi();
    }

    private void normalizePhi() {
        while(this.phi < 0){
            this.phi += 2 * Math.PI;
        }
        while(this.phi >= 2 * Math.PI){
            this.phi -= 2 * Math.PI;
        }
    }

    private void normalizeTheta() {
        while(this.theta < 0){
            this.theta += Math.PI;
        }
        while(this.theta >= Math.PI){
            this.theta -= Math.PI;
        }
    }

    public double distanseTo(SphereDot other) {
        double x = Math.sin(this.theta) * Math.cos(this.phi);
        double y = Math.sin(this.theta) * Math.sin(this.phi);
        double z = Math.cos(this.theta);

        double otherX = Math.sin(other.theta) * Math.cos(other.phi);
        double otherY = Math.sin(other.theta) * Math.sin(other.phi);
        double otherZ = Math.cos(other.theta);

        return Math.sqrt(
                (otherX - x) * (otherX - x)
                + (otherY - y) * (otherY - y)
                + (otherZ - z) * (otherZ - z)
        );
    }

    public void render() {
        App.processingRef.pushMatrix();
        App.processingRef.sphereDetail(4);
        App.processingRef.stroke(Color.RED.getRGB());
        App.processingRef.translate(500 + (float)(100 * getX()), 500 + (float)(100 * getY()), 500 + (float)(100 * getZ()));
        App.processingRef.sphere(2);
        App.processingRef.popMatrix();
    }
}
