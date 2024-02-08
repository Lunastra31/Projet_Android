package fr.ldnr.chloe.monZoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AlerteActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alerte);
        //Faire une autocomplete sur les lieux
        AutoCompleteTextView etLieu = findViewById(R.id.et_lieu);
        String[]lieux = getResources().getStringArray(R.array.lieux);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, lieux);
        etLieu.setAdapter(aa);
    }

    public void envoyer(View source) {
        //création de la modal pour demander la confirmation à l'utilisateur
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle(R.string.alerte_titre);
        builder.setMessage(R.string.alerte_confirmer);
        //builder.setIcon(R.mipmap.chienlouptcheque);
        builder.setPositiveButton(android.R.string.yes,
                (dialogInterface, i) -> {confirmer(); });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }

    private int enregistrer(){
        try{
            AlerteOpenHelper oh = new AlerteOpenHelper(this);
            EditText etLieu = findViewById(R.id.et_lieu);
            EditText etIntitule = findViewById(R.id.et_intitule);
            EditText etInformations = findViewById(R.id.et_infos);
            return oh.insererAlerte(etIntitule.getText().toString().trim(),
                    etLieu.getText().toString().trim(),
                    etInformations.getText().toString().trim());
        } catch (Exception ex){
            Log.e("AlerteActivity.enregistrer", "Erreur BDD", ex);
            return 0;
        }
    }

    public void confirmer(){
        EditText etIntitule = findViewById(R.id.et_intitule);
        String intitule = etIntitule.getText().toString();
        EditText etLieu = findViewById(R.id.et_lieu);
        String lieu = etLieu.getText().toString();
        CheckBox urgentCheck = findViewById(R.id.urgent_check);
        boolean urgent = urgentCheck.isChecked();
        if (intitule.trim().isEmpty() || lieu.trim().isEmpty() ) {
            Toast.makeText(this, "Alerte incomplète andouille", Toast.LENGTH_LONG).show();
        } else {
            int nbAlerte = enregistrer();
            String point = "";
            if(urgent)
                point = "!";
            Toast.makeText(this, getString(R.string.alerte_envoi, intitule, point, nbAlerte), Toast.LENGTH_LONG).show();
            finish();//permet de finir la fenêtre une fois le formulaire correctement rempli
        }
    }
}