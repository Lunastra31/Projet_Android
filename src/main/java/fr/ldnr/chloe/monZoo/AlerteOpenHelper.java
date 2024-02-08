package fr.ldnr.chloe.monZoo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AlerteOpenHelper extends SQLiteOpenHelper {


    public AlerteOpenHelper(@Nullable Context context) {
        super(context, "zoo.sqlite", null, 1); //le n° de version permet de maintenir la BDD
        //Même en cas de modif de celle là
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE lieu(id INTEGER PRIMARY KEY,"+"nom VARCHAR(100) UNIQUE)");
    db.execSQL("CREATE TABLE alerte(id INTEGER PRIMARY KEY, "+
            "intitule VARCHAR(100), lieu INTEGER NOT NULL, "+
            "informations TEXT, FOREIGN KEY(lieu) REFERENCES lieu(id) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int ancienne, int nouvelle) {

    }

    public int insererAlerte(String intitule, String lieu, String informations){
        SQLiteDatabase db = getWritableDatabase();
        //Connection à la BDD et début transaction
        db.beginTransaction();
        Cursor c = db.rawQuery("SELECT id FROM lieu WHERE nom LIKE ?", new String[]{ lieu });
        // Pour rappel LIKE permet d'ignorer la casse
        Integer idLieu = null;
        if(c.moveToFirst()){
            idLieu = c.getInt(0);
        }
        c.close(); // un curseur permet de se déplacer dans une BDD pour une recherche
        int nbAlerte;
        if(idLieu==null) {
            db.execSQL("INSERT INTO lieu(nom) VALUES (?)", new Object[]{lieu});
            db.execSQL("INSERT INTO alerte(intitule, lieu, informations) VALUES " +
                            "(?,(SELECT MAX(id) FROM lieu),?)", //les deux ? représentent ici des infos qui viennent hors de la BDD
                    new Object[]{intitule, informations});
            nbAlerte = 1;
        }else {
            db.execSQL("INSERT INTO alerte(intitule, lieu, informations) VALUES (?,?,?)",
                    new Object[]{intitule, idLieu, informations});
            Cursor cur = db.rawQuery("SELECT COUNT(*) FROM alerte WHERE lieu=?", new String[]{String.valueOf(idLieu)});
            cur.moveToFirst();
            nbAlerte = cur.getInt(0);
            cur.close();
        }
        //permet de considérer la transaction comme réussi si pas d'exception et de pouvoir y mettre fin après
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return nbAlerte;
    }
}
