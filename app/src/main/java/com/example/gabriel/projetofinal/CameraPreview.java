package com.example.gabriel.projetofinal;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {

    static SurfaceHolder mHolder;
    private Camera mCamera;

    public static String TAG = MainActivity.TAG;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        mCamera = camera;

        Parameters p = mCamera.getParameters();

        Log.d(TAG, "PARAMETERS: ");
        Log.d(TAG, p.flatten());

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Log.i(TAG, "CameraPreview OK");
    }

    /* CREATED */
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            Log.i(TAG, "PREVIEW CRIADA.");
        } catch (IOException e) {
            Log.e(TAG, "Erro ao criar PREVIEW: " + e.getMessage());
        }

    }

    /* DESTROYED */
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview(); // parar Preview
            MainActivity.releaseCamera(); // Liberar a camera
        }
    }

    /* CHANGED */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        Log.i(TAG, "Surface CHANGED");

        if (mHolder.getSurface() == null) {
            Log.w(TAG, "Preview surface nao existe.");
            return;
        }

        // Parar a preview antes de alterá-la
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignorar: tentamos pausar uma preview que nao existia
            Log.w(TAG, "Preview não existe, não temos como pausar.");
        }

        // configurar parametros
        try {
            Parameters params = mCamera.getParameters();
            mCamera.setParameters(params);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao configurar os parametros.");
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao recomecar a preview: " + e.getMessage());
        }
    }
}