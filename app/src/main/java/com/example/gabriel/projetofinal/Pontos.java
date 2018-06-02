package com.example.gabriel.projetofinal;

/**
 * Created by GABRIEL on 03/05/2018.
 */

public class Pontos implements Comparable<Pontos>{
    private float keypointHessian;
    private double[] descriptors;

    public Pontos(float k, double[] d) {
        this.keypointHessian = k;
        this.descriptors = d;
    }

    public int compareTo(Pontos another) {
        double valor = (this.keypointHessian - another.keypointHessian);
        int r;
        if (valor > 0) {
            r = 1;
        } else if (valor < 0) {
            r = -1;
        } else {
            r = 0;
        }
        return r;
    }

    public double[] getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(double[] descriptors) {
        this.descriptors = descriptors;
    }
}
