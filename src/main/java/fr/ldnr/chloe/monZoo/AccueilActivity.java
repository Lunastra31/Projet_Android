package fr.ldnr.chloe.monZoo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class AccueilActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Bundle ~ HashMap
        //le savedInstanceState permet de sauver l'état de l'activité à la fermeture
        setContentView(R.layout.accueil);
        remplirNews();
        preparerBoutons();
        enregistrerOuverture();
    }

    private void remplirNews() {
        TextView tvNews = findViewById(R.id.tv_news);
        String textComplet = "";
        try {
            InputStream is = getAssets().open("news.txt"); //récupère le contenu du fichier
            BufferedReader br = new BufferedReader( //permet d'envoyer le contenu du fichier qui est lu via InputStreamReader
                    //new InputStreamReader(is.Charset.forName("UTF-8")); Version Java et version Android en dessous
                    new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) { //readLine à mettre ici sinon le texte ne s'affiche pas
                // lecture de la prochaine ligne
                textComplet += line + "\n";
            }
            is.close();
            tvNews.setText(textComplet);
        } catch (Exception ex) {
            Log.e("AccueilAct.remplirNews", "Erreur lecture", ex);
        }
    }

    private void preparerBoutons(){
        Button btCarte = findViewById(R.id.bt_carte);
        //inclus l'appui et le relâchement et marche pour le tactile, correspond à interface
        btCarte.setOnClickListener(this);
        Button btAlerte = findViewById(R.id.bt_alerte);
        btAlerte.setOnClickListener(view -> {
            Intent i = new Intent(this, AlerteActivity.class);
            startActivity(i);
        });
        Button btAquarium = findViewById(R.id.bt_aquarium);
        btAquarium.setOnClickListener(view -> {
            Intent i = new Intent(this, AquariumActivity.class);
            startActivity(i);
        });
        Button btAnnuaire = findViewById(R.id.bt_annuaire);
        btAnnuaire.setOnClickListener(view -> {
            Intent i = new Intent(this, AnnuaireActivity.class);
            startActivity(i);
        });
        Button btAnimal = findViewById(R.id.bt_animal);
        btAnimal.setOnClickListener(view -> {
            Intent i = new Intent(this, AnimalActivity.class);
            startActivity(i);
        });
    }
    //On peut également faire des classes internes ou externes pour recevoir les événements

    private void enregistrerOuverture(){
        //demande des permissions à l'ouverture de l'appli
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED)
        enregistrerLog();
        else
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 0);
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grandResults) {
        if(permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION))
            if(grandResults[0]==PackageManager.PERMISSION_GRANTED)
                enregistrerLog();
        else
            Log.i("Accueil.onpr", "Permission refusée");
    }
    private void enregistrerLog(){
        try{
            OutputStream os = openFileOutput("zoo.Log.text", MODE_PRIVATE | MODE_APPEND ); //permet de ne pas écraser systématiquement le fichier
            Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
            Location position = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String posStr = "?,?";
            if (position != null){
                posStr = position.getLongitude()+","+position.getLatitude();
            }
            DateFormat df = new DateFormat(); //android.text.format en import
            Date now = new Date(); //java.util
            String ligne = df.format("yyyy-MM-dd hh:mm:ss", now) +" Ouverture\n";
            w.write(ligne);
            w.close();
            os.close();

            File repertoire = getFilesDir();
            File fichier = new File(repertoire, "zoo.Log.txt");
            if(!fichier.exists())
                Log.w("AccueilActivity.enregistrer", "Fichier pas créé");
        } catch (Exception ex){
            Log.e("AccueilActivity.enregistrer", "Erreur fichier", ex);
        }
    }

    public void onClick(View view){
        Intent i = new Intent(this, CarteActivity.class);
        startActivity(i);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.accueil, menu);
        SharedPreferences sp = getSharedPreferences("zoo", MODE_PRIVATE);
        boolean envoyer = sp.getBoolean("envoyer", true);
        menu.findItem(R.id.menu_envoyer).setChecked(envoyer);
        return true;
    }

    /*@Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item){
        if (item.getItemId() == R.id.menu_carte){
            startActivity(new Intent(this, CarteActivity.class));
        }
        if(item.getItemId() == R.id.menu_envoyer){
            item.setChecked(! item.isChecked());
        }
        return true;
    }*/
    @Override
    public boolean onMenuItemSelected (int featureId, @NonNull MenuItem item){
        if(item.getItemId() == R.id.menu_carte){
            startActivity(new Intent(this, CarteActivity.class));
        }
        if (item.getItemId() == R.id.menu_envoyer){
            item.setChecked(! item.isChecked());
            SharedPreferences sp = getSharedPreferences("zoo", MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("envoyer", item.isChecked());
            e.commit();
        }
        if(item.getItemId() == R.id.menu_animal){
            startActivity(new Intent(this, AnimalActivity.class));
        }
        return true;
    }
}
