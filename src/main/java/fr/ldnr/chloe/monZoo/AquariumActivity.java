package fr.ldnr.chloe.monZoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AquariumActivity extends Activity {
    private long debut, fin;

    /**
     *
     *ZOne de stockage, par durée :
     * - variable locale
     * - variable d'instance (attribut) juste au dessus
     * - état d'instance sauvée (savedInstanceState), permet de prolonger les durées de vie de l'instance voir plus bas
     * - variable de classe (attribut static final) pouvant être lus n'importe où dans l'activité
     * - fichier de cache (ex les logs)
     * Préférences :
     * - SharedPreference : il n'existe plus que les private si elles sont communes à toutes les activités (les Shared) ou juste des preferences
     * (voir sur l'AccueilActivity pour voir l'exemple)
     * - fichier interne
     * - base interne
     * - fichier externe/publique
     * - stockage externe/serveur
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        //Bundle permet d'envoyer différents types de données primitives(contrairement à une liste) par serialization
        //Pour envoyer un objet on utilisera plutôt les parcelables
        super.onCreate(savedInstanceState);
        setContentView(new AquariumView(this));
        //permet de démarrer un chronomètre à l'ouverture de la page et de conserver cette durée si on réouvre l'activité
        //plus tard
        if(savedInstanceState!=null &&
                savedInstanceState.containsKey("debut")){
            debut = savedInstanceState.getLong("debut");
        } else {
            debut = System.currentTimeMillis();
        }
        Log.i("AquariumActivity.onCreate()", "Activité créée");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Intent i = new Intent(this, PopcornActivity.class);
            //Envoyer des infos à une autre activité
            fin = System.currentTimeMillis();
            long duree = fin - debut;
            Log.i("AquariumActivity.onTouch()", "Duree : "+duree);
            i.putExtra("duree_aqua", duree);
            //startActivityForResult permet de récupérer une donnée venant d'ailleurs
            startActivityForResult(i, 0);
        }
        return true;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putLong("debut", debut);
        //Cette fonction permet de sauvegarder tout ce que les usagers font quand l'activité redémarre
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Intent data va correspondre ici au Result de l'activité Popcorn
        if(data != null && data.getBooleanExtra("toast_affiche", false)==true)
            Log.i("Aquarium.onActivityR", "Popcorn affiche le message");
            else
                Log.i("Aquarium.onActivityR", "Popcorn n'affiche PAS le message");
    }

    static class AquariumView extends View{

        public AquariumView(AquariumActivity context){
            super(context);//constructeur
        } //La classe view permet de gérer des éléments en
        //plus autres que le texte etc ici elle permet l'insertion d'une image sur l'activité

        @Override
        public void onDraw(@NonNull Canvas canvas){
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.planaquarium);
            canvas.drawBitmap(bmp, 0, 0, null);
        }
    }

}
