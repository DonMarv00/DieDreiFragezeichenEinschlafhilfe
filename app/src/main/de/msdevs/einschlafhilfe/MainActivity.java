package de.msdevs.einschlafhilfe;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayoutgesamt;
    ActionBarDrawerToggle drawerToggle;
    private String TAG = MainActivity.class.getSimpleName();
    String antwort;
    String beschreibung_folge;
    //Damit die Update Funktion funktioniert muss die Version bei jeden Update hochgesetzt werden.
    int app_version = 1;
    //Dies wird benötigt, damit keine neue Folge geladen wird, während eine bereits geladen wird.
    boolean neue_folge_laden = true;
    String url_extra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //App initalisieren



        if(isNetworkAvailable()){
            ini();
        }else{
            RelativeLayout rl_main_snackbar = (RelativeLayout)findViewById(R.id.rl_main_layout);
            Snackbar.make(rl_main_snackbar, "Internet nicht verfügbar, diese App braucht Internet um zu funktionieren!", Snackbar.LENGTH_SHORT).show();

        }


        drawerLayoutgesamt = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayoutgesamt,R.string.auf, R.string.zu);
        drawerLayoutgesamt.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();

        final ViewFlipper vf_bottom_bar = (ViewFlipper)findViewById(R.id.bottom_bar_view_flipper);
        RelativeLayout btn_left = (RelativeLayout)findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vf_bottom_bar.showPrevious();
                getViewflipper();

                try{
                    if(isNetworkAvailable()){
                        new folge_laden().execute();
                    }else{
                        RelativeLayout rl_main_snackbar = (RelativeLayout)findViewById(R.id.rl_main_layout);
                        Snackbar.make(rl_main_snackbar, "Internet nicht verfügbar, diese App braucht Internet um zu funktionieren!", Snackbar.LENGTH_SHORT).show();

                    }

                }catch (Exception e){
                }
            }
        });
        RelativeLayout btn_right = (RelativeLayout)findViewById(R.id.btn_right);
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vf_bottom_bar.showNext();
                getViewflipper();

                try{
                    if(isNetworkAvailable()){
                        new folge_laden().execute();
                    }else{
                        RelativeLayout rl_main_snackbar = (RelativeLayout)findViewById(R.id.rl_main_layout);
                        Snackbar.make(rl_main_snackbar, "Internet nicht verfügbar, diese App braucht Internet um zu funktionieren!", Snackbar.LENGTH_SHORT).show();

                    }
                }catch (Exception e){
                }
            }
        });

        CardView btn_start = (CardView)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayoutgesamt.closeDrawer(Gravity.START, false);
            }
        });
        CardView btn_einstellungen = (CardView)findViewById(R.id.btn_einstellungen);
        btn_einstellungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayoutgesamt.closeDrawer(Gravity.START, false);
                Intent i = new Intent(MainActivity.this, EinstellungenActivity.class);
                startActivity(i);
            }
        });
        CardView btn_information = (CardView)findViewById(R.id.btn_information);
        btn_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Öffnet Google Play zum updaten der App
                Intent intent_google_play = new Intent(Intent.ACTION_VIEW);
                intent_google_play.setData(Uri.parse("https://play.google.com/store/apps/details?id=de.msdevs.einschlafhilfe"));
                startActivity(intent_google_play);
            }
        });
        CardView btn_beschreibung = (CardView)findViewById(R.id.btn_beschreibung);
        btn_beschreibung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beschreibungAnzeigen();
            }
        });
        CardView btn_spotify = (CardView)findViewById(R.id.btn_spotify);
        btn_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_folgen_name = (TextView)findViewById(R.id.tv_folgen_name);

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
                    RelativeLayout rl_main_snackbar = (RelativeLayout)findViewById(R.id.rl_main_layout);
                    Snackbar.make(rl_main_snackbar, "Spotify ist nicht installiert.", Snackbar.LENGTH_SHORT).show();

                }
            }
        });
    }
    //Folgen laden lassen
    private class folge_laden extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler folge_laden = new HttpHandler();
            // Making a request to url and getting response
            SharedPreferences sp_url_part = getSharedPreferences(getPackageName(), 0);
            String url_part = sp_url_part.getString("viewflipper_position", "");

            String part_eins = sp_url_part.getString("nummer_eins", "");
            String part_zwei = sp_url_part.getString("nummer_zwei", "");

            url_extra = "https://marvin-stelter.de/ddf/server.php?key=" + url_part  + "&extra_eins=" + part_eins + "&extra_zwei=" + part_zwei;


            String answer = folge_laden.data(url_extra);

            Log.e(TAG, "Antwort vom Server: " + answer);
            if (answer != null) {
                try {
                    SharedPreferences sp_folge_anzeigen = getSharedPreferences("server_antwort", 0);
                    SharedPreferences.Editor sp_folgen_editor = sp_folge_anzeigen.edit();
                    sp_folgen_editor.putString("folge", answer);
                    sp_folgen_editor.commit();

                } catch (final Exception e) {
                    RelativeLayout rl_main_snackbar = (RelativeLayout) findViewById(R.id.rl_main_layout);
                    Snackbar.make(rl_main_snackbar, "Überprüfe deine Internetvebindung.", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Fehler: Es konnte keine Folgen geladen werden.");

                RelativeLayout rl_main_snackbar = (RelativeLayout) findViewById(R.id.rl_main_layout);
                Snackbar.make(rl_main_snackbar, "Überprüfe deine Internetvebindung.", Snackbar.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SharedPreferences sp_folge_anzeigen = getSharedPreferences("server_antwort", 0);
            TextView tv_name = (TextView) findViewById(R.id.tv_folgen_name);
            TextView tv_nummer = (TextView) findViewById(R.id.tv_folgen_nummer);

            String data = sp_folge_anzeigen.getString("folge", "");
            String[] folgen_Daten = data.split(":");

            String folgen_name = folgen_Daten[0];
            String folgen_nummer = folgen_Daten[1];

            //Folgennummer zum laden der Beschreibung speichern
            SharedPreferences.Editor sp_folgen_editor = sp_folge_anzeigen.edit();
            sp_folgen_editor.putString("nummer", folgen_nummer);
            sp_folgen_editor.commit();

            tv_name.setText(". " + folgen_name);
            tv_nummer.setText(folgen_nummer);


            //Beschreibung der Folge laden lassen
            try{
                new beschreibung_laden().execute();
            }catch (Exception e){
            }
        }
    }
    //Auf Update überprüfen lassen
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
                app_prefs_editor.commit();

                if(app_version < aktuelle_version){
                    CardView btn_information = (CardView)findViewById(R.id.btn_information);
                    btn_information.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Update avaiable", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
    //Beschreibung laden lassen
    //Auf Update überprüfen lassen
    private class beschreibung_laden extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler  beschreibung = new HttpHandler();
            //Folgennummer aus den SharedPreferences laden
            SharedPreferences sp_folge_anzeigen = getSharedPreferences("server_antwort", 0);

            String url = "https://marvin-stelter.de/ddf/server.php?beschreibung="  + sp_folge_anzeigen.getString("nummer", "");
            String answer = beschreibung.data(url);

            if (answer != null) {
                beschreibung_folge = answer;
            } else {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            CardView btn_beschreibung = (CardView)findViewById(R.id.btn_beschreibung);
            btn_beschreibung.setVisibility(View.VISIBLE);
            CardView btn_spotify = (CardView)findViewById(R.id.btn_spotify);
            btn_spotify.setVisibility(View.VISIBLE);
            //Nun kann wieder eine neue Folge ausgewählt werden.
            neue_folge_laden = true;

        }
    }
    public void ini(){
        //App Daten laden lassen und verschiedenes überprüfen lassen.
        SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
        //Einstellungen
        if(sp_app_prefs.getInt("picker_one",0) == 0 && sp_app_prefs.getInt("picker_two",0) == 0){
            SharedPreferences.Editor sp_app_prefs_editor = sp_app_prefs.edit();
            sp_app_prefs_editor.putInt("picker_one", 1);
            sp_app_prefs_editor.commit();

            sp_app_prefs_editor.putInt("picker_two", 1);
            sp_app_prefs_editor.commit();

            sp_app_prefs_editor.putString("nummer_eins", "1");
            sp_app_prefs_editor.commit();

            sp_app_prefs_editor.putString("nummer_zwei", "1");
            sp_app_prefs_editor.commit();
        }

        if(sp_app_prefs.getString("viewflipper_position","") == ""){
            sharedPreferences_viewflipper_editor("1");
        }
        ViewFlipper vf_bottom_bar = (ViewFlipper)findViewById(R.id.bottom_bar_view_flipper);
        int viewflipper =  Integer.valueOf(sp_app_prefs.getString("viewflipper_position","")) - 1;
        vf_bottom_bar.setDisplayedChild(viewflipper);

        //Folge laden lassen
        try{
            new folge_laden().execute();
        }catch (Exception e){
        }
        //Auf updates überprüfen
        try{
            new checkForUpdate().execute();
        }catch (Exception e){
        }
    }

    public void getViewflipper(){
        ViewFlipper vf_change = (ViewFlipper) findViewById(R.id.bottom_bar_view_flipper);
        int viewflipper = vf_change.getDisplayedChild() + 1;
        String vf_displayed_child = String.valueOf(viewflipper);
        sharedPreferences_viewflipper_editor(vf_displayed_child);
    }
    public void sharedPreferences_viewflipper_editor(String child){
        SharedPreferences sp_viewflipper_child = getSharedPreferences(getPackageName(), 0);
        SharedPreferences.Editor sp_edit = sp_viewflipper_child.edit();
        sp_edit.putString("viewflipper_position", child);
        sp_edit.commit();
    }

    public void Spotify() {
        try {
            TextView tv_folgen_name = (TextView)findViewById(R.id.tv_folgen_name);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            intent.setComponent(new ComponentName(
                    "com.spotify.music",
                    "com.spotify.music.MainActivity"));
            intent.putExtra(SearchManager.QUERY, tv_folgen_name.getText().toString() + " Teil 1");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            RelativeLayout rl_main_snackbar = (RelativeLayout)findViewById(R.id.rl_main_layout);
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



        if (id == R.id.action_refresh) {
            getViewflipper();

            try{
                if(neue_folge_laden == true){
                    neue_folge_laden = false;
                    CardView btn_beschreibung = (CardView)findViewById(R.id.btn_beschreibung);
                    CardView btn_spotify = (CardView)findViewById(R.id.btn_spotify);
                    btn_spotify.setVisibility(View.GONE);
                    btn_beschreibung.setVisibility(View.GONE);
                    new folge_laden().execute();
                }

            }catch (Exception e){
            }
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(new Configuration());
    }
}


