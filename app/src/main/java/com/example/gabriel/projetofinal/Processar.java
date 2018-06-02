package com.example.gabriel.projetofinal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.ml.SVM;
import org.opencv.xfeatures2d.SURF;

import android.util.Log;

public class Processar {

    // -- Atributos

    private final static String TAG = "RESULT>>>>>"; // DEBUG TAG
    //private static FeatureDetector detector = FeatureDetector
     //       .create(FeatureDetector.ORB);
    //private static DescriptorExtractor extractor = DescriptorExtractor
    //        .create(DescriptorExtractor.ORB);

    // -- Getters & Setters

    //public static FeatureDetector getDetector() {
    //    return detector;
    //}

   // public static DescriptorExtractor getExtractor() {
    //    return extractor;
    //}


    private static List<Pontos> processaFoto(Mat foto) {
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        //SURF detector = SURF.create();
        //detector.setHessianThreshold(450);
        //detector.setNOctaveLayers(6);
        //detector.setNOctaves(3);
        //detector.setUpright(false);

        // Encontrar keypoints
        MatOfKeyPoint keypointsFoto = new MatOfKeyPoint();
        detector.detect(foto, keypointsFoto);
        Log.i(TAG, "FOTO - Detect KeyPoints OK!");

        // Extrair descriptors
        Mat descriptorsFoto = new Mat();
        extractor.compute(foto, keypointsFoto, descriptorsFoto);
        Log.i(TAG, "FOTO - Extract descriptors OK!");

        // Criar lista de tuplas com keypoint e descriptor
        List<Pontos> list = new ArrayList<Pontos>();
        KeyPoint[] keypointsVector = keypointsFoto.toArray();
        Log.d(TAG, "Numero keypoints: " + keypointsVector.length);
        // numero de keypoints = numero descriptors (ROWs)
        for (int i = 0; i < keypointsVector.length; i++) {
            //Log.d(TAG, "Numero decriptor: " + keypointsVector.length);
            // descriptor (double[])
            double[] desc = new double[descriptorsFoto.cols()];

            for (int j = 0; j < descriptorsFoto.cols(); j++) { // 128
                desc[j] = descriptorsFoto.get(i, j)[0];
            }
            list.add(new Pontos(keypointsVector[i].response, desc));
        }
        return list;

    }

    private static List<Pontos> processaFotoSurf(Mat foto) {
        //FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        //DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        SURF detector = SURF.create();
        detector.setHessianThreshold(450);
        detector.setNOctaveLayers(6);
        detector.setNOctaves(3);
        detector.setUpright(false);

        // Encontrar keypoints
        MatOfKeyPoint keypointsFoto = new MatOfKeyPoint();
        detector.detect(foto, keypointsFoto);
        Log.i(TAG, "FOTO - Detect KeyPoints OK!");

        // Extrair descriptors
        Mat descriptorsFoto = new Mat();
        detector.compute(foto, keypointsFoto, descriptorsFoto);
        Log.i(TAG, "FOTO - Extract descriptors OK!");

        // Criar lista de tuplas com keypoint e descriptor
        List<Pontos> list = new ArrayList<Pontos>();
        KeyPoint[] keypointsVector = keypointsFoto.toArray();
        Log.d(TAG, "Numero keypoints: " + keypointsVector.length);
        // numero de keypoints = numero descriptors (ROWs)
        for (int i = 0; i < keypointsVector.length; i++) {
            //Log.d(TAG, "Numero decriptor: " + keypointsVector.length);
            // descriptor (double[])
            double[] desc = new double[descriptorsFoto.cols()];

            for (int j = 0; j < descriptorsFoto.cols(); j++) { // 128
                desc[j] = descriptorsFoto.get(i, j)[0];
            }
            list.add(new Pontos(keypointsVector[i].response, desc));
        }
        return list;

    }


    public static String identificarFoto(Mat foto, SVM svm) {

        // Processar
        List<Pontos> tuplas = processaFoto(foto);
        Log.i(TAG,
                "Lista de tuplas de keypoints e descritpors criadas com sucesso.");

        // Verificar se houve um numero minimo
        if (tuplas.size() >= 178) {

            List<Mat> matDesc = new ArrayList<Mat>();

            // Ordenar (SORT), ordem crescente
            Collections.sort(tuplas);

            // Escolher os 200 melhores
            int cont = 0;
            int ind = (tuplas.size() - 1); // comecar do ultimo (maior)

            while (cont < 178) {

                // Matriz auxiliar
                Mat m = new Mat(1, 32, CvType.CV_32F);

                for (int i = 0; i < 32; i++) {
                    m.put(0, i, tuplas.get(ind).getDescriptors()[i]);
                }
                // Adicionar a lista
                matDesc.add(m);

                ind--;
                cont++;
            }

            // Concatenar matrizes da lista horizontalmente (1D matriz)
            Mat data = new Mat();
            Log.w(TAG, "Size of trainData: rows = " + data.rows()
                    + ", cols = " + data.cols());

            Core.hconcat(matDesc, data);
            Log.w(TAG, "Size of trainData: rows = " + data.rows()
                    + ", cols = " + data.cols());
            // Classifica
            return svm.predict(data) +"";

        } else {
            // Nao identificou
            return "";

        }
    }

    public static String identificarFotoSurf(Mat foto, SVM svm) {

        // Processar
        List<Pontos> tuplas = processaFotoSurf(foto);
        Log.i(TAG,
                "Lista de tuplas de keypoints e descritpors criadas com sucesso.");

        // Verificar se houve um numero minimo
        if (tuplas.size() >= 178) {

            List<Mat> matDesc = new ArrayList<Mat>();

            // Ordenar (SORT), ordem crescente
            Collections.sort(tuplas);

            // Escolher os 200 melhores
            int cont = 0;
            int ind = (tuplas.size() - 1); // comecar do ultimo (maior)

            while (cont < 2000) {

                // Matriz auxiliar
                Mat m = new Mat(1, 64, CvType.CV_32F);

                for (int i = 0; i < 64; i++) {
                    m.put(0, i, tuplas.get(ind).getDescriptors()[i]);
                }
                // Adicionar a lista
                matDesc.add(m);

                ind--;
                cont++;
            }

            // Concatenar matrizes da lista horizontalmente (1D matriz)
            Mat data = new Mat();
            Log.w(TAG, "Size of trainData: rows = " + data.rows()
                    + ", cols = " + data.cols());

            Core.hconcat(matDesc, data);
            Log.w(TAG, "Size of trainData: rows = " + data.rows()
                    + ", cols = " + data.cols());
            // Classifica
            return svm.predict(data) +"";

        } else {
            // Nao identificou
            return "";

        }
    }
}