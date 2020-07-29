package de.msdevs.einschlafhilfe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Random;

import de.msdevs.einschlafhilfe.utils.Folgen;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab_refresh, fab_description;
    String name, beschreibung;
    ImageView iv_cover;
    RelativeLayout rl_root;
    TextView tv_details;
    String nummer;
    String spotify;
    ViewFlipper mViewFlipper;
    int folgen_nummer;
    final int REQ_CODE_EXTERNAL_STORAGE_PERMISSION = 45;
    RelativeLayout rl_bb;
    Button btn_spotify;
    RelativeLayout left, right;
    int start = 1;
    int end = 51;
    int folgen_nummer_dd;
    int folgen_gesamt;
    String spotifyStart = "spotify:album:";
    String spotifyEnd = ":play";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Die drei ??? - Einschlafhilfe");
        iniViews();
        iniApp();
        folgenLaden();




        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              folgenLaden();
            }
        });
        fab_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beschreibungAnzeigen();
            }
        });
        btn_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spotify.length() != 0){
                    Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                    intent.setData(Uri.parse(
                            spotifyStart + spotify + spotifyEnd));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    if(name.length() != 0) {
                        try {

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                            intent.setComponent(new ComponentName(
                                    "com.spotify.music",
                                    "com.spotify.music.MainActivity"));
                            intent.putExtra(SearchManager.QUERY, name + " Teil 1");
                            startActivity(intent);

                        } catch (Exception e) {
                            Spotify();
                        }

                    }else{
                        RelativeLayout rl_main_snackbar = findViewById(R.id.rl_root);
                        Snackbar.make(rl_main_snackbar, "Spotify ist nicht installiert.", Snackbar.LENGTH_SHORT).show();
                    }
                }

            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.setInAnimation(MainActivity.this, R.anim.anim_flipper_item_in_left);
                mViewFlipper.setOutAnimation(MainActivity.this, R.anim.anim_flipper_item_out_right);
                mViewFlipper.showPrevious();

                if (mViewFlipper.getDisplayedChild() == 0) {
                    // 1 - 50
                    start = 0;
                    end = 50;
                }else if (mViewFlipper.getDisplayedChild() == 1) {
                    // 1 - 100
                    start = 0;
                    end = 100;
                }else if (mViewFlipper.getDisplayedChild() == 2) {
                    // 1 - 150
                    start = 0;
                    end = 150;
                }else if (mViewFlipper.getDisplayedChild() == 3) {
                    // 1 - Ende
                    start = 0;
                    end = folgen_gesamt;
                }
                folgenLaden();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.setInAnimation(MainActivity.this, R.anim.anim_flipper_item_in_right);
                mViewFlipper.setOutAnimation(MainActivity.this, R.anim.anim_flipper_item_out_left);
                mViewFlipper.showNext();

                rl_bb.setVisibility(View.VISIBLE);
                if (mViewFlipper.getDisplayedChild() == 0) {
                    // 1 - 50
                    start = 0;
                    end = 50 - 1;
                }else if (mViewFlipper.getDisplayedChild() == 1) {
                    // 1 - 100
                    start = 0;
                    end = 100 - 1;
                }else if (mViewFlipper.getDisplayedChild() == 2) {
                    // 1 - 150
                    start = 0;
                    end = 150 - 1;
                }else if (mViewFlipper.getDisplayedChild() == 3) {
                    // 1 - Ende
                    start = 0;
                    end = folgen_gesamt - 1;
                }
                folgenLaden();
            }
        });
    }
    private void iniViews(){

        fab_refresh = findViewById(R.id.fab_refresh);
        iv_cover = findViewById(R.id.iv_cover);
        tv_details = findViewById(R.id.tv_details);
        fab_description = findViewById(R.id.fab_description);
        btn_spotify = findViewById(R.id.btn_spotify);
        rl_root = findViewById(R.id.rl_root);
        mViewFlipper = findViewById(R.id.bottom_bar_view_flipper);
        left = findViewById(R.id.btn_left);
        right = findViewById(R.id.btn_right);
        rl_bb = findViewById(R.id.rl_bb);

    }
    private void iniApp(){
        SharedPreferences sp_app_prefs = getSharedPreferences("download_prefs",0);
        if(sp_app_prefs.getString("json","").length() == 0){
            Intent i = new Intent(MainActivity.this, DownloadActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void Spotify() {
        try {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            intent.setComponent(new ComponentName(
                    "com.spotify.music",
                    "com.spotify.music.MainActivity"));
            intent.putExtra(SearchManager.QUERY, name + " Teil 1");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            RelativeLayout rl_main_snackbar = findViewById(R.id.rl_root);
            Snackbar.make(rl_main_snackbar, "Spotify ist nicht installiert.", Snackbar.LENGTH_SHORT).show();
        }
    }
    private void folgenLaden(){
        btn_spotify.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (mViewFlipper.getDisplayedChild() == 4) {
                Random r = new Random();
                folgen_nummer_dd = r.nextInt(7 - 0) + 0;
                new folgen_daten_laden_diedrei().execute();
                btn_spotify.setVisibility(View.GONE);
            }else{
                Random r = new Random();
                folgen_nummer = r.nextInt(end - start) + start;
                new folgen_daten_laden().execute();

            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_EXTERNAL_STORAGE_PERMISSION);
        }
    }
    private Bitmap cover(int i, File directory){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(directory), options);
        return bitmap;
    }
    @SuppressLint("CheckResult")
    private void CoverWithGlide(){
        try{
            iv_cover.setImageBitmap(null);
            Glide.with(this).load("https://api.citroncode.com/android/ddf/android_api/cover/" + nummer + ".jpg").into(iv_cover);
        }catch (Exception e){
            e.printStackTrace();
            RelativeLayout rl_main_snackbar = findViewById(R.id.rl_root);
            Snackbar.make(rl_main_snackbar, "Fehler beim laden des Covers.", Snackbar.LENGTH_SHORT).show();
        }

    }
    public void beschreibungAnzeigen() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(nummer + ". " + name);
        alert.setMessage(beschreibung);
        alert.setNeutralButton("Schließen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int i) {
                dlg.dismiss();
            }
        });
        alert.show();
    }
    private class folgen_daten_laden extends AsyncTask<String, Object,  ArrayList<Folgen>> {
        @Override
        protected ArrayList<Folgen> doInBackground(String... jsonFileUrlString) {

            ArrayList<Folgen> persons = new ArrayList<>();

            try {

                File storage = Environment.getExternalStorageDirectory();
                File directory;
                directory = new File(storage.getAbsolutePath() + "/.ddf/folgen.json");
                AssetManager am = getApplicationContext().getAssets();

                FileInputStream fileInputStream = new FileInputStream(directory);

                InputStreamReader isReader = new InputStreamReader(fileInputStream);

                JsonReader jsonReader = new JsonReader(isReader);
                jsonReader.beginObject();
                jsonReader.nextName();
                jsonReader.beginArray();
                while(jsonReader.hasNext()){
                    jsonReader.beginObject();
                    Folgen currentFolge = new Folgen();
                    while (jsonReader.hasNext()){

                        switch (jsonReader.nextName()){
                            case "name": {
                                currentFolge.setName(jsonReader.nextString());
                                break;
                            } case "beschreibung":{
                                currentFolge.setBeschreibung(jsonReader.nextString());
                                break;
                            } case "nummer":{
                                currentFolge.setNummer(jsonReader.nextString());
                                break;
                            } case "spotify":{
                                currentFolge.setSpotify(jsonReader.nextString());
                                break;
                            } default:{
                                jsonReader.skipValue();
                                break;
                            }
                        }
                    }
                    persons.add(currentFolge);
                    jsonReader.endObject();
                }
                jsonReader.endArray();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return persons;
        }

        @Override
        protected void onPostExecute(ArrayList<Folgen> folge) {
            super.onPostExecute(folge);

            try{
                folgen_gesamt = folge.size();
                beschreibung =  folge.get(folgen_nummer).getBeschreibung();
                name =  folge.get(folgen_nummer).getName();
                nummer =  folge.get(folgen_nummer).getNummer();

                try{
                    spotify = folge.get(folgen_nummer).getSpotify();
                }catch (Exception e){

                }

                File storage = Environment.getExternalStorageDirectory();
                File directory = new File(storage.getAbsolutePath() + "/.ddf/cover_" + nummer + ".png");

                if (directory.exists()) {
                    iv_cover.setImageBitmap(cover(Integer.valueOf(nummer), directory));
                }else{
                    CoverWithGlide();
                }

                tv_details.setText(nummer + ". " + name);
            }catch (Exception e){
               folgenLaden();
            }

        }
    }
    private class folgen_daten_laden_diedrei extends AsyncTask<String, Object,  ArrayList<Folgen>> {
        @Override
        protected ArrayList<Folgen> doInBackground(String... jsonFileUrlString) {

            ArrayList<Folgen> persons = new ArrayList<>();

            try {

                File storage = Environment.getExternalStorageDirectory();
                File directory;
                directory = new File(storage.getAbsolutePath() + "/.ddf/folgen_diedrei.json");
                AssetManager am = getApplicationContext().getAssets();

                FileInputStream fileInputStream = new FileInputStream(directory);

                InputStreamReader isReader = new InputStreamReader(fileInputStream);

                JsonReader jsonReader = new JsonReader(isReader);
                jsonReader.beginObject();
                jsonReader.nextName();
                jsonReader.beginArray();
                while(jsonReader.hasNext()){
                    jsonReader.beginObject();
                    Folgen currentFolge = new Folgen();
                    while (jsonReader.hasNext()){

                        switch (jsonReader.nextName()){
                            case "name": {
                                currentFolge.setName(jsonReader.nextString());
                                break;
                            } case "beschreibung":{
                                currentFolge.setBeschreibung(jsonReader.nextString());
                                break;
                            } case "nummer":{
                                currentFolge.setNummer(jsonReader.nextString());
                                break;

                            } default:{
                                jsonReader.skipValue();
                                break;
                            }
                        }
                    }
                    persons.add(currentFolge);
                    jsonReader.endObject();
                }
                jsonReader.endArray();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return persons;
        }

        @Override
        protected void onPostExecute(ArrayList<Folgen> folge) {
            super.onPostExecute(folge);

            try{
                folgen_gesamt = folge.size();
                beschreibung =  folge.get(folgen_nummer_dd).getBeschreibung();
                name =  folge.get(folgen_nummer_dd).getName();
                nummer =  folge.get(folgen_nummer_dd).getNummer();

                File storage = Environment.getExternalStorageDirectory();
                File directory = new File(storage.getAbsolutePath() + "/.ddf/cover_dd_" + nummer + ".png");

                if (directory.exists()) {
                    iv_cover.setImageBitmap(cover(Integer.valueOf(nummer), directory));
                }else{
                    CoverWithGlide();
                }

                tv_details.setText(nummer + ". " + name);
            }catch (Exception e){
                folgenLaden();
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_EXTERNAL_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           folgenLaden();
        }else{
            Toast.makeText(this, "Ohne diese Berechtigung kann die App nicht benutzt werden.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}