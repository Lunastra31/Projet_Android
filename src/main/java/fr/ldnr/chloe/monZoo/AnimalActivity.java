package fr.ldnr.chloe.monZoo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimalActivity extends Activity implements View.OnClickListener{
    private ExecutorService executeur;
    private String contenuHTML;
    private Handler uiHandler;


    protected void onCreate(@Nullable Bundle savedInstanceState){ //sorte de constructeur
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animal);
        AutoCompleteTextView etAnimal = findViewById(R.id.et_animal);
        String[]animaux = getResources().getStringArray(R.array.animaux);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, animaux);
        etAnimal.setAdapter(aa);
        Button btChercher = findViewById(R.id.bt_search);
        btChercher.setOnClickListener(this);
        executeur = Executors.newFixedThreadPool(2); // mieux de le faire ici plutôt que onClick
        //car évite de créer un exécuteur à chaque click
        uiHandler = new Handler(Looper.getMainLooper()); //réalise les tâches qu'on lui envoit et les renvoit vers le thread indiqué ici le main
    }


    public void onClick(View view){
        //chercher(); interdit car trop long
        //ExecutorService executeur = Executors.newFixedThreadPool(2);
        contenuHTML = "{recherche en cours}";
        afficherResultat();
        executeur.execute(() -> chercher());//déclenche un thread lors du click
    }

    public void chercher(){
        try {
            AutoCompleteTextView animal = findViewById(R.id.et_animal);
            String nomAnimal = animal.getText().toString().trim(); //récupérer le contenu du champ
            String strurl = "https://fr.wikipedia.org/w/api.php?action=query&"
                    + "prop=extracts&exsentences=3&format=json&titles=" + URLEncoder.encode(nomAnimal, "UTF-8");
            Log.i("AnimalActivity.onClick", "Recherche de " + nomAnimal);
            URLConnection connection = new URL(strurl).openConnection();
            InputStream is = connection.getInputStream();
            String jsonText = "";
            BufferedReader br = new BufferedReader( //permet d'envoyer le contenu du fichier qui est lu via InputStreamReader
                    new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) { //readLine à mettre ici sinon le texte ne s'affiche pas
                // lecture de la prochaine ligne
                jsonText += line + "\n";
            }
            is.close();
            Log.i("AnimalActivity.onCLick()", "Résultat JSON " + jsonText);
            JSONObject racine = new JSONObject(jsonText); //permet d'obtenir uniquement les infos que l'on souhaite
            JSONObject query = racine.getJSONObject("query");
            JSONObject pages = query.getJSONObject("pages");
            String numeroPage = pages.keys().next();
            if (! numeroPage.equals("-1")) {
                JSONObject page = pages.getJSONObject(numeroPage);
                synchronized (contenuHTML){
                contenuHTML = page.getString("extract");}
                Log.i("AnimalActivity.onClick", "HTML : "+contenuHTML);
            } else {
                synchronized (contenuHTML) {
                    contenuHTML = "{non trouvé}";
                }
            }
            JSONObject page = pages.getJSONObject(numeroPage);
            contenuHTML= page.getString("extract");
            Log.i("AnimalActivity.onClick", "HTML : " + contenuHTML);

            /*TextView tvInfos = findViewById(R.id.tv_infos);
            tvInfos.setText(contenuHTML);*/
            uiHandler.post(() -> afficherResultat()); //envoie la requête au Handler qui la traitera dès que possible


        }catch (Exception ex){
            Log.e("AnimalActivity.onClick", "Erreur recherche", ex);
        }
    }

    private void afficherResultat(){
        TextView tvInfos = findViewById(R.id.tv_infos);
        tvInfos.setText(Html.fromHtml(contenuHTML));
    }
}
