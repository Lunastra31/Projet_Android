package fr.ldnr.chloe.monZoo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Random;

public class PopcornActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PopcornView(this));
        Log.i("PopcornActivity.onCreate()", "Activité créée");
        //Faire apparaitre un toast quand la durée passée sur la page excède 2s
        int seconde = (int) getIntent().getLongExtra("duree_aqua", 0) / 1000;
        //if (seconde > 2)
        if (seconde > 0) {
            String seconde_str = getResources().getQuantityString(R.plurals.popcorn_seconde, seconde);
            String[] especes = getResources().getStringArray(R.array.popcorn_especes);
            //choix random des espèces parmi celles présents dans le string.xml
            int nombre = new Random().nextInt(especes.length); //0 1 ou 2
            String message = getString(R.string.popcorn_avertissement, seconde, seconde_str, especes[nombre]);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Pas de popcorn aux poissons (" + seconde + "s)", Toast.LENGTH_LONG).show();
            Intent resultat = new Intent();
            resultat.putExtra("toast_affiche", true);
            setResult(0, resultat);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            int largeur = findViewById(android.R.id.content).getWidth();
            if (event.getX() < largeur/2) { //à gauche, scinde l'écran en deux partie :
                //la partie gauche renverra sur l'activité du début
                //la partie droite permettra d'ouvrir une page wikipedia dédiée au popcorn
                Intent i = new Intent(this, CarteActivity.class);
                //Les flags permettent de gérer les animations, il en existe donc plusieurs, ici celui permet
                //de nettoyer tous les activités ouvertes et de ne garder que la première
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                //gestion de la partie droite de l'écran avec ouverture de la page
                String page = "https://fr.wikipedia.org/wiki/Pop-corn";
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(page));
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException ex){
                    Log.e("PopcornActivity", "Pas de navigateur", ex);
                }
            }
        }
        return true;
    }
        static class PopcornView extends View {

            public PopcornView(Context context) {
                super(context);
            }

            @Override
            public void onDraw(@NonNull Canvas canvas) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.popcorn);
                canvas.drawBitmap(bmp, 0, 0, null);
            }
        }
    }
