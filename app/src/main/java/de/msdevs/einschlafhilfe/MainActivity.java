package de.msdevs.einschlafhilfe;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    String antwort, url_debug_tost, string_antwort_favo;
    String beschreibung_folge;
    int app_version = 11;
    boolean neue_folge_laden = true;
    String url_extra, name, nummer, folgen_name, folgen_nummer;
    ViewFlipper vf_bottom_bar;
    String device_id;
    Boolean favoresiert;
    String[] folgen_Daten;
    String extra_parameter;
    String folgen_typ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //App initalisieren
        if(isNetworkAvailable()){
            ini();
        }else{
            RelativeLayout rl_main_snackbar = findViewById(R.id.rl_main_layout);
            Snackbar.make(rl_main_snackbar, "Internet nicht verfügbar, diese App braucht Internet um zu funktionieren!", Snackbar.LENGTH_SHORT).show();
        }

        final FloatingActionButton fab_favo = findViewById(R.id.fab_favo);
        fab_favo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new folge_favorisieren().execute();

            }
        });
        //Neue Folge laden lassen
        vf_bottom_bar = findViewById(R.id.bottom_bar_view_flipper);
        FloatingActionButton fab_refresh = findViewById(R.id.fab_refresh);
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewflipper();

                try{
                    if(neue_folge_laden){
                        neue_folge_laden = false;
                        AppCompatButton btn_beschreibung =  findViewById(R.id.btn_beschreibung);
                        AppCompatButton btn_spotify =  findViewById(R.id.btn_spotify);
                        btn_spotify.setVisibility(View.GONE);
                        btn_beschreibung.setVisibility(View.GONE);

                        new folge_laden().execute();

                    }

                }catch (Exception e){
                    Log.e(TAG, "Fehler: " + e.toString());
                }
            }
        });

        RelativeLayout btn_left = findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vf_bottom_bar.showPrevious();
                getViewflipper();

                try{
                    if(isNetworkAvailable()){
                        new folge_laden().execute();
                    }else{
                        RelativeLayout rl_main_snackbar = findViewById(R.id.rl_main_layout);
                        Snackbar.make(rl_main_snackbar, "Internet nicht verfügbar, diese App braucht Internet um zu funktionieren!", Snackbar.LENGTH_SHORT).show();

                    }
                }catch (Exception e){
                    Log.e(TAG, "Fehler: " + e.toString());
                }
            }
        });
        RelativeLayout btn_right = findViewById(R.id.btn_right);
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vf_bottom_bar.showNext();
                getViewflipper();

                try{
                    if(isNetworkAvailable()){
                        new folge_laden().execute();
                    }else{
                        RelativeLayout rl_main_snackbar = findViewById(R.id.rl_main_layout);
                        Snackbar.make(rl_main_snackbar, "Internet nicht verfügbar, diese App braucht Internet um zu funktionieren!", Snackbar.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e(TAG, "Fehler: " + e.toString());
                }
            }
        });
        AppCompatButton btn_beschreibung =  findViewById(R.id.btn_beschreibung);
        btn_beschreibung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beschreibungAnzeigen();
            }
        });
        AppCompatButton btn_spotify =  findViewById(R.id.btn_spotify);
        btn_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_folgen_name = findViewById(R.id.tv_folgen_name);

                if(tv_folgen_name.length() != 0) {
                    try {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                        intent.setComponent(new ComponentName(
                                "com.spotify.music",
                                "com.spotify.music.MainActivity"));
                        intent.putExtra(SearchManager.QUERY, tv_folgen_name.getText().toString() + " Teil 1");
                        startActivity(intent);


                    } catch (Exception e) {
                        Spotify();
                    }

                }else{
                    RelativeLayout rl_main_snackbar = findViewById(R.id.rl_main_layout);
                    Snackbar.make(rl_main_snackbar, "Spotify ist nicht installiert.", Snackbar.LENGTH_SHORT).show();

                }
            }
        });
    }

    //Folgen laden lassen
    @SuppressLint("StaticFieldLeak")
    private class folge_laden extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FloatingActionButton fab_favo = findViewById(R.id.fab_favo);
            fab_favo.hide();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler folge_laden = new HttpHandler();
            // Making a request to url and getting response
            SharedPreferences sp_url_part = getSharedPreferences(getPackageName(), 0);
            String url_part = sp_url_part.getString("viewflipper_position", "");

            int part_eins = sp_url_part.getInt("picker_one", 0);
            int part_zwei = sp_url_part.getInt("picker_two", 0);


            url_extra = "https://marvin-stelter.de/ddf/server.php?key=" + url_part  + "&extra_eins=" + part_eins + "&extra_zwei=" + part_zwei + "&device_id=" + device_id;


            String answer = folge_laden.data(url_extra);

            Log.e(TAG, "Antwort vom Server: " + answer);
            if (answer != null) {
                try {
                    SharedPreferences sp_folge_anzeigen = getSharedPreferences("server_antwort", 0);
                    SharedPreferences.Editor sp_folgen_editor = sp_folge_anzeigen.edit();
                    sp_folgen_editor.putString("folge", answer);
                    sp_folgen_editor.apply();

                } catch (final Exception e) {
                    RelativeLayout rl_main_snackbar =  findViewById(R.id.rl_main_layout);
                    Snackbar.make(rl_main_snackbar, "Überprüfe deine Internetvebindung.", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Fehler: Es konnte keine Folgen geladen werden.");

                RelativeLayout rl_main_snackbar =  findViewById(R.id.rl_main_layout);
                Snackbar.make(rl_main_snackbar, "Versuche es bitte erneut.", Snackbar.LENGTH_SHORT).show();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SharedPreferences sp_folge_anzeigen = getSharedPreferences("server_antwort", 0);
            TextView tv_name =  findViewById(R.id.tv_folgen_name);
            TextView tv_nummer =  findViewById(R.id.tv_folgen_nummer);

            String data = sp_folge_anzeigen.getString("folge", "");
            try {
                assert data != null;
                folgen_Daten = data.split(":");
            }catch (NullPointerException e){
                Log.e(TAG, "Fehler: " + e.toString());
            }
            try{
                 folgen_name = folgen_Daten[0];
                 folgen_nummer = folgen_Daten[1];
                 favoresiert = Boolean.valueOf(folgen_Daten[2]);
                 changeFavoButton();
                folgen_typ = folgen_Daten[3];

                name = folgen_name;
                nummer = folgen_nummer;
            }catch (ArrayIndexOutOfBoundsException e){

                new folge_laden().execute();
            }

            tv_name.setText(R.string.dot + folgen_name);
            tv_nummer.setText(folgen_nummer);


            //Beschreibung der Folge laden lassen
            try{
                new beschreibung_laden().execute();
            }catch (Exception e){
                Log.e(TAG, "Fehler: " + e.toString());
            }


        }
    }

    //Auf Update überprüfen lassen
    @SuppressLint("StaticFieldLeak")
    private class checkForUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler checkUpdate = new HttpHandler();
            String url = "https://marvin-stelter.de/ddf/web/version_code.txt";
            String answer = checkUpdate.data(url);

            if (answer != null) {
                antwort = answer;
            } else {
                new checkForUpdate().execute();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Server Antwort in Int konvertieren um einen Abgleich durchzuführen


            //Antwort aus den SharedPreferences laden zum abgleichen
            SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
            int aktuelle_version = sp_app_prefs.getInt("server_version", 0);


            //Überprüfen ob ein Update vorhanden ist.
            if(aktuelle_version != Integer.valueOf(antwort)){

                SharedPreferences.Editor app_prefs_editor = sp_app_prefs.edit();
                app_prefs_editor.putInt("server_version", Integer.valueOf(antwort));
                app_prefs_editor.apply();
                if(app_version < aktuelle_version){
                    alertAttention("Update verfügbar","Eine optionale Aktualisierung ist verfügbar.","1");
                }

            }

        }
    }
    //Beschreibung laden lassen
    @SuppressLint("StaticFieldLeak")
    private class beschreibung_laden extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler  beschreibung = new HttpHandler();
            //Folgennummer aus den SharedPreferences laden
            //SharedPreferences sp_folge_anzeigen = getSharedPreferences("server_antwort", 0);
            SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);

            String url_part = sp_app_prefs.getString("viewflipper_position", "");

            String url = "https://marvin-stelter.de/ddf/server.php?beschreibung="  + folgen_nummer + "&key_beschreibung=" + url_part;
            url_debug_tost = url;
            String answer = beschreibung.data(url);

            if (answer != null) {
                if(answer.equals("")){
                    beschreibung_folge = "Zu dieser Folge ist noch keine Beschreibung verfügbar. Diese folgt in kürze...";
                }else{
                    beschreibung_folge = answer;
                }
            }else{
                new beschreibung_laden().execute();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            AppCompatButton btn_beschreibung = findViewById(R.id.btn_beschreibung);
            btn_beschreibung.setVisibility(View.VISIBLE);
            AppCompatButton btn_spotify = findViewById(R.id.btn_spotify);
            btn_spotify.setVisibility(View.VISIBLE);
            //Nun kann wieder eine neue Folge ausgewählt werden.
            neue_folge_laden = true;

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class folge_favorisieren extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FloatingActionButton fab_favo = findViewById(R.id.fab_favo);
            fab_favo.hide();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler folge_laden = new HttpHandler();

            if(vf_bottom_bar.getDisplayedChild() == 3) {
                extra_parameter = "&extra_parameter=dd";
            }else if(vf_bottom_bar.getDisplayedChild() == 5){
                if (folgen_typ.equals("dd")){
                    extra_parameter = "&extra_parameter=dd";
                }else{
                    extra_parameter = "&extra_parameter=normal";
                }
            }else{
                extra_parameter = "&extra_parameter=normal";
            }
            url_extra = "https://marvin-stelter.de/ddf/favorisieren.php?device_id=" + device_id + "&nummer=" + folgen_nummer + extra_parameter;

            String answer = folge_laden.data(url_extra);

            Log.e(TAG, "Antwort vom Server: " + answer);
            if (answer != null) {
                string_antwort_favo = answer;
            } else {
                RelativeLayout rl_main_snackbar =  findViewById(R.id.rl_main_layout);
                Snackbar.make(rl_main_snackbar, "Versuche es bitte erneut.", Snackbar.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            FloatingActionButton fab_favo = findViewById(R.id.fab_favo);

            if(string_antwort_favo.equals("no_favo")){
                fab_favo.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }else if(string_antwort_favo.equals("favo")) {
                fab_favo.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
            fab_favo.show();
        }
    }
    private void changeFavoButton(){
        FloatingActionButton fab_favo = findViewById(R.id.fab_favo);
        if(favoresiert) {
            fab_favo.setImageResource(R.drawable.ic_favorite_white_24dp);
        }else{
            fab_favo.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        fab_favo.show();
    }
    public void ini(){
        //App Daten laden lassen und verschiedenes überprüfen lassen.
        SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);


        if(Objects.equals(sp_app_prefs.getString("id", ""), "")){
            Random r = new Random();
            int i = r.nextInt(1000001 - 1) + 1;
            device_id = String.valueOf(i);

            SharedPreferences.Editor sp_app_prefs_editor = sp_app_prefs.edit();
            sp_app_prefs_editor.putString("id", device_id);
            sp_app_prefs_editor.apply();
        }else{
            device_id = sp_app_prefs.getString("id","");
        }
        //Einstellungen
        if(Objects.equals(sp_app_prefs.getString("first_app_start", ""), "")){
            alertAttention("Willkommen", "Ich wünsche ihnen viel Spaß mit meiner App.\nWeitere Informationen über diese App finden Sie unter:\nhttps://marvin-stelter.de\nDer Quellcode ist verfügbar auf Github:\nhttps://github.com/MarvinStelter/DieDreiFragezeichenEinschlafhilfe\n", "0");
        }
        if(sp_app_prefs.getInt("picker_one",0) == 0 && sp_app_prefs.getInt("picker_two",0) == 0){
            SharedPreferences.Editor sp_app_prefs_editor = sp_app_prefs.edit();
            sp_app_prefs_editor.putInt("picker_one", 1);
            sp_app_prefs_editor.apply();

            sp_app_prefs_editor.putInt("picker_two", 50);
            sp_app_prefs_editor.apply();


        }

        if("".equals(sp_app_prefs.getString("viewflipper_position", ""))){
            sharedPreferences_viewflipper_editor("1");
        }
        ViewFlipper vf_bottom_bar = findViewById(R.id.bottom_bar_view_flipper);
        int viewflipper =  Integer.valueOf(Objects.requireNonNull(sp_app_prefs.getString("viewflipper_position", ""))) - 1;
        vf_bottom_bar.setDisplayedChild(viewflipper);

        //Folge laden lassen
        try{
            new folge_laden().execute();
        }catch (Exception e){
            Log.e(TAG, "Fehler: " + e.toString());
        }
        //Auf updates überprüfen
        try{
            new checkForUpdate().execute();
        }catch (Exception e){
            Log.e(TAG, "Fehler: " + e.toString());
        }
    }

    public void getViewflipper(){
        ViewFlipper vf_change =  findViewById(R.id.bottom_bar_view_flipper);
        int viewflipper = vf_change.getDisplayedChild() + 1;
        String vf_displayed_child = String.valueOf(viewflipper);
        sharedPreferences_viewflipper_editor(vf_displayed_child);
    }
    public void sharedPreferences_viewflipper_editor(String child){
        SharedPreferences sp_viewflipper_child = getSharedPreferences(getPackageName(), 0);
        SharedPreferences.Editor sp_edit = sp_viewflipper_child.edit();
        sp_edit.putString("viewflipper_position", child);
        sp_edit.apply();
    }

    private void alertAttention(String title, String text, final String param){
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle(title);

        @SuppressLint("InflateParams") View dialogview = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null);

        TextView tv_dialog = dialogview.findViewById(R.id.tv_dialog_text);
        tv_dialog.setText(text);

        alertDialog.setView(dialogview);
        alertDialog.setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            if(param.equals("0")){
                SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(),0);
                SharedPreferences.Editor editor = sp_app_prefs.edit();
                editor.putString("first_app_start","false");
                editor.apply();

            }
            }
        });
        if(param.equals("1")){

            alertDialog.setPositiveButton("Herunterladen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    //Öffnet Google Play zum updaten der App
                    Intent intent_google_play = new Intent(Intent.ACTION_VIEW);
                    intent_google_play.setData(Uri.parse("https://play.google.com/store/apps/details?id=de.msdevs.einschlafhilfe"));
                    startActivity(intent_google_play);
                }
            });
        }
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public void Spotify() {
        try {
            TextView tv_folgen_name = findViewById(R.id.tv_folgen_name);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            intent.setComponent(new ComponentName(
                    "com.spotify.music",
                    "com.spotify.music.MainActivity"));
            intent.putExtra(SearchManager.QUERY, tv_folgen_name.getText().toString() + " Teil 1");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            RelativeLayout rl_main_snackbar = findViewById(R.id.rl_main_layout);
            Snackbar.make(rl_main_snackbar, "Spotify ist nicht installiert.", Snackbar.LENGTH_SHORT).show();

        }
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    public void beschreibungAnzeigen(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Beschreibung");
        alert.setMessage(beschreibung_folge);
        alert.setNeutralButton("Schließen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int i) {
                 dlg.dismiss();
            }
        });
        alert.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent i = new Intent(MainActivity.this, EinstellungenActivity.class);
            startActivity(i);

            return true;
        }
        if (id == R.id.action_datenschutzerklärung) {

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://marvin-stelter.de/privacy/privacy.html"));
            startActivity(i);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}


