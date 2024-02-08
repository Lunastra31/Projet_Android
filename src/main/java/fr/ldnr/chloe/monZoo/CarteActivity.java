package fr.ldnr.chloe.monZoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CarteActivity extends Activity{

    //static public int xyz = 0; //ici cela revient à faire une variable globale
    //Autre solution de communication : service

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CarteView(this));
        Log.i("CarteActivity.onCreate()", "Activité créée"); //créer un message de log
        Toast.makeText(this, getString(R.string.carte_bienvenue), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "Bienvenue", Toast.LENGTH_LONG).show(); //permet d'afficher un petit message sur l'écran
    }

    //changer d'écran quand on le touche
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Intent i = new Intent(this, AquariumActivity.class);
            startActivity(i);
        }
        return true;
    }
     static class CarteView extends View{

        public CarteView(Context context) {
            super(context);
        }

        public void onDraw(@NonNull Canvas canvas){
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.planzoo);
            canvas.drawBitmap(bmp, 0, 0, null);
        }
    }

}
