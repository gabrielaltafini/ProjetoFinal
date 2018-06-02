package com.example.gabriel.projetofinal;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import static org.opencv.ml.Ml.ROW_SAMPLE;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap inputImage; // make bitmap from image resource

    protected FrameLayout preview; // Preview da camera, exibida na tela
    private static Camera mCamera;
    private static CameraPreview mPreview;

    // Path das imagens temporarias
    public static String filePath = new String();
    // Mat com a imagem da foto


    public static String TAG = "RESULT>>>>>";
    private  SVM svmpoly;
    //private  SVM svmLinear;
    //private  SVM svmBRF;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Permissoes requestUserPermission = new Permissoes(this);
        requestUserPermission.verifyStoragePermissions();
        try {
            // -- Esperar thread e abrir camera
            safeCameraOpen();
            // -- Preparar preview
            mPreview = new CameraPreview(this, mCamera);
            preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            preview.setOnClickListener(new FrameLayoutClickHandler());
            Log.i(TAG, "--- FrameLayout pronto.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Erro ao esperar pela Thread da SVM.");
        }

        // Executar som inicial
        Sons.playSound(this, "beep");

    }

    public void sift() {
        Log.d(TAG, "AQUI");
        CarregaSVM();
    }

    private void CarregaSVM()
    {
        /*
        svmBRF = SVM.create();
        File f = new File(getExternalFilesDir(null).getAbsolutePath() + "/svm_trainedb.xml");
        svmBRF  = SVM.load(f.getAbsolutePath());
        if(svmBRF.isTrained())
        {
            Log.i(TAG, "deu bom" );
        }
        else{
            Log.i(TAG, "nao carregou" );
        }



        svmLinear = SVM.create();
        f = new File(getExternalFilesDir(null).getAbsolutePath() + "/svm_trainedl.xml");
        svmLinear  = SVM.load(f.getAbsolutePath());
        if(svmLinear.isTrained())
        {
            Log.i(TAG, "deu bom" );
        }
        else{
            Log.i(TAG, "nao carregou" );
        }
*/

        svmpoly = SVM.create();
        File f = new File(getExternalFilesDir(null).getAbsolutePath() + "/svm_trained.xml");
        svmpoly  = SVM.load(f.getAbsolutePath());
        if(svmpoly.isTrained())
        {
            Log.i(TAG, "deu bom" );
        }
        else{
            Log.i(TAG, "nao carregou" );
        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {
                        sift();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onBackPressed() {
        // Encerrar o programa
        Sons.playSound(this, "fim");
        SystemClock.sleep(2500);
        onDestroy();
    }

    /**
     * Disponibilizar a camera para o aplicativo.
     */
    private void safeCameraOpen() {
        try {
            releaseCamera();
            //mCamera = Camera.open();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                    Camera.getCameraInfo(i, info);
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        try {
                            // Gets to here OK
                            mCamera = Camera.open(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //  throws runtime exception :"Failed to connect to camera service"
                        }
                    }
                }
            }
            Camera.Parameters params = mCamera.getParameters();

            // Ativar autofocus se houver
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                Log.w(TAG, "Possui AutoFocus - AUTO");
            }
            // Ajustar parametros configurados
            mCamera.setParameters(params);

            Log.i(TAG, "-- CAMERA OPENED");
        } catch (Exception e) {
            Log.e(TAG, "Falhou ao abrir a Camera");
        }
    }

    /**
     * Liberar a camera ate que seja necessaria novamente.
     */
    public static void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            Log.i(TAG, "-- RELEASE CAMERA");
        }
    }


    /**
     * Acao de clique da frameView que mostra o que a camera esta capturando.
     */
    public class FrameLayoutClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.i(TAG, "EXECUTADO: TELAClickHandler.onClick()");
            try {
                // Ação de capturar a imagem
                mCamera.takePicture(null, null, mPicture);
            } catch (Exception e) {
                Log.e(TAG, "Erro takePicture: " + e);
            }
        }
    }



    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile;
            // Criar arquivo com a foto
            try {
                pictureFile = makeFileInPath("ictempphoto");
                Log.i(TAG,
                        "Calling back picture: "
                                + pictureFile.getAbsolutePath());
                filePath = pictureFile.getAbsolutePath();
            } catch (Exception e) {
                Log.e(TAG, "Erro ao criar midia. Checar permissoes.");
                return;
            }

            // Gravar
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Error accessing file: " + e.getMessage());
            }

            // Foto capturada, processar.
            onPhotoTaken();
        }
    };



    private File makeFileInPath(String FileName) {

        File mediaFile = null;

        try {
            // Pasta do aplicativo, acessivel ao usuário
            String path = getExternalFilesDir(null).getAbsolutePath() + "/"
                    + FileName + ".jpg";

            // Checar o SDCard antes de fazer o resto
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // Podemos ler e escrever
                mediaFile = new File(path);
                Log.i(TAG, "Tentando salvar a imagem em: " + path);
            } else {
                // Erro a vista
                Log.e(TAG,
                        "Sem SD card, nao foi possivel salvar foto temporaria.");
            }

        } catch (Exception e) {
            AlertDialog.Builder d = new AlertDialog.Builder(this);
            d.setTitle("Erro");
            d.setMessage("SD Card em uso, nao foi possivel completar a operacao.");
            d.setNeutralButton("OK", null);
            d.show();
            onDestroy();
        }
        return mediaFile;
    }

    /**
     * Chamado quando a foto e tirada.
     */
    protected void onPhotoTaken() {
        Log.i(TAG, "--- onPhotoTaken, executando");

        // Soltar a camera
        releaseCamera();

        // Ajustar opcoes do bitmap
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //options.inSampleSize = 10;
        //File new_file = new File(filePath);
        //saveBitmapToFile(new_file);

        Mat mFoto = new Mat();
        // Criar bitmap usando o path de onde a imagem foi salva

        Bitmap foto_tirada = BitmapFactory.decodeFile(filePath);
       // foto_tirada = Bitmap.createScaledBitmap(foto_tirada,(int)(foto_tirada.getWidth()*0.4), (int)(foto_tirada.getHeight()*0.3), true);
        SalvaImage(filePath,foto_tirada);
        Utils.bitmapToMat(foto_tirada, mFoto); // Foto

        // Foto para escalas de cinza
        Imgproc.cvtColor(mFoto, mFoto, Imgproc.COLOR_BGRA2GRAY, 8);
        Log.w(TAG, "Foto carregada!");

        // Identificar foto
        String f = Processar.identificarFotoSurf(mFoto, svmpoly);

        // Tocar som
        Sons.playSound(this, f); // resultado

        // Começar a preview novamente
        safeCameraOpen();
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (IOException e) {
            Log.e(TAG, "ERRO setPreviewDisplay: " + e);
        }
        mCamera.startPreview();

        // Beep de conclusao
        Sons.playSound(this, "beep"); // beep
    }

    public void SalvaImage(String filename,Bitmap bmp)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=50;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
}