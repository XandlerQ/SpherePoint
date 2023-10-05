package com.spherepoint;

public class Positioner {
    private SphereDotFunction function;
    private double currentStepSize;
    private double currentValue;
    private double lastValue;
    private int iterationCount;

    Positioner() {
        this.function = null;
        this.currentStepSize = 0;
        this.currentValue = Double.MAX_VALUE;
        this.lastValue = Double.MAX_VALUE;
        this.iterationCount = 0;
    }

    Positioner(SphereDotFunction function) {
        this.function = function;
        this.currentStepSize = 0;
        this.currentValue = function.getValue();
        this.lastValue = Double.MAX_VALUE;
        this.iterationCount = 0;
    }

    public SphereDotFunction getFunction() {
        return function;
    }

    public double[] getParameters() {
        return this.function.getParameters();
    }

    public double getCurrentStepSize() {
        return currentStepSize;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void setFunction(SphereDotFunction function) {
        this.function = function;
        reset();
    }

    public void setInitialParameters(double[] parameters) {
        this.function.setParameters(parameters);
        reset();
    }

    public void setStepSize(double currentStepSize) {
        this.currentStepSize = currentStepSize;
    }

    public void reset() {
        this.currentStepSize = 0;
        this.currentValue = this.function.getValue();
        this.lastValue = Double.MAX_VALUE;
        this.iterationCount = 0;
    }

    public int hookeJeeves(double stepSize, double epsilon) {
        int iterationCount = 0;
        double currentStepSize = stepSize;
        double currentValue = this.function.getValue();
        double lastValue = Double.MAX_VALUE;
        while (lastValue - currentValue > epsilon) {
            boolean improved = false;

            for (int i = 0; i < this.function.getParameterCount(); i++) {
                this.function.adjustParameter(i, currentStepSize);
                double newValue = this.function.getValue();
                if (newValue > currentValue) {
                    this.function.adjustParameter(i, -2 * currentStepSize);
                    newValue = this.function.getValue();
                    if (newValue > currentValue) {
                        this.function.adjustParameter(i, currentStepSize);
                    }
                    else {
                        lastValue = currentValue;
                        currentValue = newValue;
                        improved = true;
                    }
                }
                else {
                    lastValue = currentValue;
                    currentValue = newValue;
                    improved = true;
                }
            }
            if (this.function.getClass() == NegativeMinR.class) {
                boolean changedDots = ((NegativeMinR) this.function).setClosestDots();
                if (changedDots) currentStepSize = Math.PI / 12;
                else {
                    if (!improved) currentStepSize /= 2;
                }
            }
            else {
                if (!improved) currentStepSize /= 2;
            }
            iterationCount++;
        }
        return iterationCount;
    }

    public boolean hookeJeevesStep(double epsilon) {
        boolean improved = false;

        for (int i = 0; i < this.function.getParameterCount(); i++) {
            double initialParameterValue = this.function.getParameter(i);
            this.function.adjustParameter(i, this.currentStepSize);
            double newValue = this.function.getValue();
            if (newValue >= this.currentValue) {
                this.function.adjustParameter(i, -2 * this.currentStepSize);
                newValue = this.function.getValue();
                if (newValue >= this.currentValue) {
                    this.function.setParameter(i, initialParameterValue);
                }
                else {
                    this.lastValue = this.currentValue;
                    this.currentValue = newValue;
                    improved = true;
                }
            }
            else {
                this.lastValue = this.currentValue;
                this.currentValue = newValue;
                improved = true;
            }
        }
        if (this.function.getClass() == NegativeMinR.class) {
            boolean changedDots = ((NegativeMinR) this.function).setClosestDots();
            if (changedDots) this.currentStepSize = Math.PI / 12;
            else {
                if (!improved) this.currentStepSize /= 2;
            }
        }
        else {
            if (!improved) this.currentStepSize /= 2;
        }
        this.iterationCount++;
        return lastValue - currentValue <= epsilon;
    }

    public int gradientDescentOptimum(double epsilon, double innerEpsilon) {
        double[] antiGradient = null;

        while (this.lastValue - this.currentValue > epsilon) {
            double[] currentIterationInitialParameters = this.function.getParameters();
            antiGradient = this.function.getAntiGradient();
            double a = 0;
            double b = 1;
            double bestValue = this.currentValue;
            double[] bestValueParameters = null;

            double delta = (b - a) / 100;

            double valueLower = Double.MAX_VALUE;
            double valueHigher = Double.MAX_VALUE;
            while (b - a > innerEpsilon) {
                for (int i = 0; i < this.function.getParameterCount(); i++) {
                    this.function.adjustParameter(i, (-delta + (a + b) / 2) * antiGradient[i]);
                }
                valueLower = this.function.getValue();
                if (valueLower < bestValue) {
                    bestValue = valueLower;
                    bestValueParameters = this.function.getParameters();
                }
                for (int i = 0; i < this.function.getParameterCount(); i++) {
                    this.function.adjustParameter(i, 2 * delta * antiGradient[i]);
                }
                valueHigher = this.function.getValue();
                if (valueHigher < bestValue) {
                    bestValue = valueHigher;
                    bestValueParameters = this.function.getParameters();
                }
                this.function.setParameters(currentIterationInitialParameters);
                if (valueLower < valueHigher) {
                    b = delta + (a + b) / 2;
                } else {
                    a = -delta + (a + b) / 2;
                }
                delta = (b - a) / 100;
            }

            double lambda =  (a + b) / 2;
            for (int i = 0; i < this.function.getParameterCount(); i++) {
                this.function.adjustParameter(i, lambda * antiGradient[i]);
            }
            double newValue = this.function.getValue();
            if (newValue > bestValue) {
                this.function.setParameters(bestValueParameters);
                newValue = bestValue;
            }

            if (newValue > this.currentValue) {
                this.function.setParameters(currentIterationInitialParameters);
                return -1;
            }
            else {
                this.lastValue = this.currentValue;
                this.currentValue = newValue;
            }
            this.iterationCount++;
        }
        return this.iterationCount;
    }

    public boolean gradientDescentOptimumStep(double epsilon, double innerEpsilon) {
        double[] currentIterationInitialParameters = this.function.getParameters();
        double[] antiGradient = this.function.getAntiGradient();
        double a = 0;
        double b = 1;
        double bestValue = this.currentValue;
        double[] bestValueParameters = null;

        double delta = (b - a) / 100;

        double valueLower = Double.MAX_VALUE;
        double valueHigher = Double.MAX_VALUE;
        while (b - a > innerEpsilon) {
            for (int i = 0; i < this.function.getParameterCount(); i++) {
                this.function.adjustParameter(i, (-delta + (a + b) / 2) * antiGradient[i]);
            }
            valueLower = this.function.getValue();
            if (valueLower < bestValue) {
                bestValue = valueLower;
                bestValueParameters = this.function.getParameters();
            }
            for (int i = 0; i < this.function.getParameterCount(); i++) {
                this.function.adjustParameter(i, 2 * delta * antiGradient[i]);
            }
            valueHigher = this.function.getValue();
            if (valueHigher < bestValue) {
                bestValue = valueHigher;
                bestValueParameters = this.function.getParameters();
            }
            this.function.setParameters(currentIterationInitialParameters);
            if (valueLower < valueHigher) {
                b = delta + (a + b) / 2;
            } else {
                a = -delta + (a + b) / 2;
            }
            delta = (b - a) / 100;
        }

        double lambda =  (a + b) / 2;
        for (int i = 0; i < this.function.getParameterCount(); i++) {
            this.function.adjustParameter(i, lambda * antiGradient[i]);
        }
        double newValue = this.function.getValue();
        if (newValue > bestValue && bestValueParameters != null) {
            this.function.setParameters(bestValueParameters);
            newValue = bestValue;
        }


        if (newValue > this.currentValue) {
            this.function.setParameters(currentIterationInitialParameters);
            return true;
        }
        else {
            this.lastValue = this.currentValue;
            this.currentValue = newValue;
        }
        this.iterationCount++;

        return lastValue - currentValue <= epsilon;
    }
}
