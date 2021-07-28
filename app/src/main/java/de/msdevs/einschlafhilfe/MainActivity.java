package de.msdevs.einschlafhilfe;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import de.msdevs.einschlafhilfe.utils.Folgen;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab_refresh, fab_description, fab_links;
    String name, beschreibung;
    ImageView iv_cover;
    RelativeLayout rl_root;
    TextView tv_details;
    String nummer;
    String spotify;
    ViewFlipper mViewFlipper;
    int folgen_nummer;
    RelativeLayout rl_bb;
    Button btn_spotify;
    RelativeLayout left, right;
    int start = 1;
    int end = 51;
    int folgen_nummer_dd;
    int folgen_gesamt;
    ArrayList<String> arrayNeuvertonung;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name_long));
        iniViews();
        iniApp();
        folgenLaden();


        fab_refresh.setOnClickListener(v -> folgenLaden());
        fab_description.setOnClickListener(v -> beschreibungAnzeigen());
        btn_spotify.setOnClickListener(v -> Spotify(spotify));
        left.setOnClickListener(v -> {
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
        });
        right.setOnClickListener(v -> {
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
        });
        fab_links.setOnClickListener(v -> moreDialog());
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
        fab_links = findViewById(R.id.fab_links);

        arrayNeuvertonung = new ArrayList<>();
        fillList();

    }
    private void iniApp(){
       //Add things later here
    }
    private void moreDialog(){
        String[] liste;

        if(mViewFlipper.getDisplayedChild() == 4){
            liste = new String[]{getResources().getString(R.string.informationen)};
        }else{
            int neuvertonung = 0;
            for(int i = 0; i < arrayNeuvertonung.size();i++){
                if(nummer.equals(arrayNeuvertonung.get(i))){
                    neuvertonung ++;
                }
            }
            if(neuvertonung != 0){
                liste = new String[]{getResources().getString(R.string.informationen), getResources().getString(R.string.neuvertonung)};
            }else{
                liste = new String[]{getResources().getString(R.string.informationen)};
            }
        }


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setTitle("Links:");

        builder.setItems(liste, (dialog, which) -> {
            switch (which) {
                case 0:
                    i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getRockyBeachLink(nummer)));
                    startActivity(i);
                    break;
                case 1:
                    i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://fragezeichen.neuvertonung.de/"));
                    startActivity(i);
                    break;

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String getRockyBeachLink(String nummer){
        String url = "";
        if(nummer.length() == 3){
            url = "https://www.rocky-beach.com/hoerspiel/folgen/" + nummer + ".html";
        }else  if(nummer.length() == 2){
            url = "https://www.rocky-beach.com/hoerspiel/folgen/0" + nummer + ".html";
        }else  if(nummer.length() == 1){
            url =  "https://www.rocky-beach.com/hoerspiel/folgen/00" + nummer + ".html";
        }

        if(mViewFlipper.getDisplayedChild() == 4){
            url = "https://www.rocky-beach.com/hoerspiel/folgen/50" + nummer + ".html";
        }
        return url;
    }
    public void Spotify(String id) {
        try {
            if(id.contains("https://")){
                String[] separated = id.split("https://open.spotify.com/album/");
                String pathOneRemoved = separated[1];
                String[] separatedLastPath = pathOneRemoved.split("\\?si=");
                id = separatedLastPath[0];
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("spotify:album:" + id));
            intent.putExtra(Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            RelativeLayout rl_main_snackbar = findViewById(R.id.rl_root);
            Snackbar.make(rl_main_snackbar, getResources().getString(R.string.not_installed), Snackbar.LENGTH_SHORT).show();
        }
    }
    private void folgenLaden(){
        btn_spotify.setVisibility(View.VISIBLE);
            if (mViewFlipper.getDisplayedChild() == 4) {
                Random r = new Random();
                folgen_nummer_dd = r.nextInt(7);
                new folgen_daten_laden_diedrei().execute();
                btn_spotify.setVisibility(View.GONE);
            }else{
                Random r = new Random();
                folgen_nummer = r.nextInt(end - start) + start;
                new folgen_daten_laden().execute();

            }
    }

    @SuppressLint("CheckResult")
    private void CoverWithGlide(){
        try{
            iv_cover.setImageBitmap(null);

            if(mViewFlipper.getDisplayedChild() == 4){
                Glide.with(this).load("https://api.citroncode.com/android/ddf/android_api/cover/dd" + nummer + ".jpg").into(iv_cover);
            }else {
                Glide.with(this).load("https://api.citroncode.com/android/ddf/android_api/cover/" + nummer + ".jpg").into(iv_cover);
            }


        }catch (Exception e){
            e.printStackTrace();
            RelativeLayout rl_main_snackbar = findViewById(R.id.rl_root);
            Snackbar.make(rl_main_snackbar, getResources().getString(R.string.cover_error), Snackbar.LENGTH_SHORT).show();
        }

    }
    public void beschreibungAnzeigen() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(nummer + ". " + name);
        alert.setMessage(beschreibung);
        alert.setNeutralButton(getResources().getString(R.string.close), (dlg, i) -> dlg.dismiss());
        alert.show();
    }
    private class folgen_daten_laden extends AsyncTask<String, Object,  ArrayList<Folgen>> {
        @Override
        protected ArrayList<Folgen> doInBackground(String... jsonFileUrlString) {

            ArrayList<Folgen> persons = new ArrayList<>();

            try {


                JsonReader jsonReader = new JsonReader(new StringReader(assetsStream(0)));
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
                CoverWithGlide();


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

                JsonReader jsonReader = new JsonReader(new StringReader(assetsStream(1)));
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
                CoverWithGlide();
                tv_details.setText(nummer + ". " + name);
            }catch (Exception e){
                folgenLaden();
            }

        }
    }

    public String assetsStream(int id) {
        String json = null;
        InputStream is;
        try {
            is = null;
            if(id == 0){
                is = getAssets().open("folgen.json");
            }else if(id == 1){
                is = getAssets().open("folgen_diedrei.json");
            }

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    private void fillList(){
        arrayNeuvertonung.add("11");
        arrayNeuvertonung.add("1");
        arrayNeuvertonung.add("10");
        arrayNeuvertonung.add("8");
        arrayNeuvertonung.add("22");
        arrayNeuvertonung.add("18");
        arrayNeuvertonung.add("5");
        arrayNeuvertonung.add("24");
        arrayNeuvertonung.add("12");
        arrayNeuvertonung.add("19");
        arrayNeuvertonung.add("14");
        arrayNeuvertonung.add("3");
        arrayNeuvertonung.add("28");
        arrayNeuvertonung.add("73");
        arrayNeuvertonung.add("74");
        arrayNeuvertonung.add("76");
        arrayNeuvertonung.add("77");
        arrayNeuvertonung.add("78");
        arrayNeuvertonung.add("86");
        arrayNeuvertonung.add("90");
        arrayNeuvertonung.add("92");
        arrayNeuvertonung.add("95");
        arrayNeuvertonung.add("97");
        arrayNeuvertonung.add("100");
        arrayNeuvertonung.add("101");
        arrayNeuvertonung.add("103");
        arrayNeuvertonung.add("109");
        arrayNeuvertonung.add("117");
        arrayNeuvertonung.add("121");
        arrayNeuvertonung.add("122");
        arrayNeuvertonung.add("123");
        arrayNeuvertonung.add("124");
        arrayNeuvertonung.add("125");
        arrayNeuvertonung.add("126");
        arrayNeuvertonung.add("127");
        arrayNeuvertonung.add("128");
        arrayNeuvertonung.add("129");
        arrayNeuvertonung.add("130");
        arrayNeuvertonung.add("131");
        arrayNeuvertonung.add("135");
        arrayNeuvertonung.add("140");
    }
}