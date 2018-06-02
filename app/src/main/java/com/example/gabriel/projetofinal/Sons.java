package com.example.gabriel.projetofinal;
import android.content.Context;
import android.media.MediaPlayer;

public class Sons {

    private static final int sToque = R.raw.toqueatela;
    private static final int sTenteNovamente = R.raw.tentenovamente;
    private static final int sFim = R.raw.fim;
    private static final int sBeep = R.raw.beep;
    private static final int sDois = R.raw.dois;
    private static final int sCinco = R.raw.cinco;
    private static final int sDez = R.raw.dez;
    private static final int sVinte = R.raw.vinte;
    private static final int sCinquenta = R.raw.cinquenta;
    private static final int sCem = R.raw.cem;

    public static void playSound(Context context, String f) {
        int soundID = sToque;
        if (f == "") {
            soundID = sTenteNovamente;
        } else if (f.equals("beep")) {
            soundID = sBeep;
        } else if ((f.equals("2.0")) || (f.equals("21.0"))) {
            soundID = sDois;
        } else if ((f.equals("5.0")) || (f.equals("51.0"))) {
            soundID = sCinco;
        } else if ((f.equals("10.0")) || (f.equals("101.0"))) {
            soundID = sDez;
        } else if ((f.equals("20.0")) || (f.equals("201.0"))) {
            soundID = sVinte;
        } else if ((f.equals("50.0")) || (f.equals("501.0"))) {
            soundID = sCinquenta;
        } else if ((f.equals("100.0")) || (f.equals("101.0"))) {
            soundID = sCem;
        } else if (f.equals("fim")) {
            soundID = sFim;
        }
        MediaPlayer mp = MediaPlayer.create(context, soundID);
        mp.setVolume(1f, 1f);
        mp.start();
    }
}
