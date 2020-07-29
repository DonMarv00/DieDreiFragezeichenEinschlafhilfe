package de.msdevs.einschlafhilfe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import de.msdevs.einschlafhilfe.utils.HttpHandler;


public class DownloadActivity extends AppCompatActivity {

    String url_to_json = "https://api.citroncode.com/android/ddf/android_api/folgen.json";
    String url_to_json_diedrei = "https://api.citroncode.com/android/ddf/android_api/folgen_diedrei.json";
    final int REQ_CODE_EXTERNAL_STORAGE_PERMISSION = 45;
    ProgressBar pg_download;
    int counter;
    TextView tv_status;
    int exI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Einschlafhilfe - Offline Download");
        iniViews();
        iniApp();

    }

    private void iniViews() {
        pg_download = findViewById(R.id.pg_download);
        tv_status = findViewById(R.id.tv_status);

    }



    private void iniApp() {
        if (ActivityCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                File storage = Environment.getExternalStorageDirectory();
                // TODO: When changing the path be sure to also modify the path in  filepaths.xml(res/xml/filepaths.xml)
                File directory = new File(storage.getAbsolutePath() + "/.ddf/");
                directory.mkdirs();
                if (directory.exists()) {
                    SharedPreferences sp_app_data_prefs = getSharedPreferences("download_prefs", 0);
                    if (sp_app_data_prefs.getString("json", "").length() == 0) {
                        if (ActivityCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            new download_json().execute();
                        } else {
                            ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_EXTERNAL_STORAGE_PERMISSION);
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class download_json extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                File storage = Environment.getExternalStorageDirectory();
                File directory = new File(storage.getAbsolutePath() + "/.ddf/folgen.json");
                URL u = new URL(url_to_json);
                URLConnection conn = u.openConnection();
                int contentLength = conn.getContentLength();

                DataInputStream stream = new DataInputStream(u.openStream());

                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                DataOutputStream fos = new DataOutputStream(new FileOutputStream(directory));
                fos.write(buffer);
                fos.flush();
                fos.close();
            } catch (IOException ignored) {

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                File storage = Environment.getExternalStorageDirectory();
                File directory = new File(storage.getAbsolutePath() + "/.ddf/folgen.json");
                if (directory.exists()) {
                    new download_json_diedrei().execute();
                }
            } catch (Exception ignored) {

            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class download_json_diedrei extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                File storage = Environment.getExternalStorageDirectory();
                File directory = new File(storage.getAbsolutePath() + "/.ddf/folgen_diedrei.json");
                URL u = new URL(url_to_json_diedrei);
                URLConnection conn = u.openConnection();
                int contentLength = conn.getContentLength();

                DataInputStream stream = new DataInputStream(u.openStream());

                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                DataOutputStream fos = new DataOutputStream(new FileOutputStream(directory));
                fos.write(buffer);
                fos.flush();
                fos.close();
            } catch (IOException ignored) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                File storage = Environment.getExternalStorageDirectory();
                File directory = new File(storage.getAbsolutePath() + "/.ddf/folgen_diedrei.json");
                if (directory.exists()) {

                    SharedPreferences sp_app_prefs = getSharedPreferences("download_prefs",0);
                    SharedPreferences.Editor editor = sp_app_prefs.edit();
                    editor.putString("json","downloaded");
                    editor.apply();


                    new download_covers().execute();
                    pg_download.setProgress(10);
                }
            } catch (Exception ignored) {

            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class download_covers extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler daten_laden = new HttpHandler();
            String answer = daten_laden.load("https://api.citroncode.com/android/ddf/android_api/folgen_counter.txt");

            if (answer != null) {
                counter = Integer.parseInt(answer);

                for (int i = 1; i <= counter; i++) {
                    exI = i;
                    try {
                        File storage = Environment.getExternalStorageDirectory();
                        File directory = new File(storage.getAbsolutePath() + "/.ddf/cover_" + i + ".png");
                        URL u = new URL("https://api.citroncode.com/android/ddf/android_api/cover/" + i + ".jpg");
                        URLConnection conn = u.openConnection();
                        int contentLength = conn.getContentLength();

                        DataInputStream stream = new DataInputStream(u.openStream());

                        byte[] buffer = new byte[contentLength];
                        stream.readFully(buffer);
                        stream.close();

                        DataOutputStream fos = new DataOutputStream(new FileOutputStream(directory));
                        fos.write(buffer);
                        fos.flush();
                        fos.close();
                        publishProgress();
                    } catch (IOException ignored) {

                    }
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Void... arg1) {
            pg_download.setMax(counter + 10);
            int currentProgress = pg_download.getProgress();
            pg_download.setProgress(currentProgress + 1);
            tv_status.setText("Cover: " + exI + " / " + counter + " lädt");

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
              new download_covers_dd().execute();
            } catch (Exception ignored) {

            }
        }
    }
    private class download_covers_dd extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler daten_laden = new HttpHandler();
            String answer = daten_laden.load("https://api.citroncode.com/android/ddf/android_api/folgen_counter.txt");

            if (answer != null) {

                for (int i = 1; i <= 8; i++) {
                    exI = i;
                    try {
                        File storage = Environment.getExternalStorageDirectory();
                        File directory = new File(storage.getAbsolutePath() + "/.ddf/cover_dd_" + i + ".png");
                        URL u = new URL("https://api.citroncode.com/android/ddf/android_api/cover/dd" + i + ".jpg");
                        URLConnection conn = u.openConnection();
                        int contentLength = conn.getContentLength();

                        DataInputStream stream = new DataInputStream(u.openStream());

                        byte[] buffer = new byte[contentLength];
                        stream.readFully(buffer);
                        stream.close();

                        DataOutputStream fos = new DataOutputStream(new FileOutputStream(directory));
                        fos.write(buffer);
                        fos.flush();
                        fos.close();
                        publishProgress();
                    } catch (IOException ignored) {

                    }
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Void... arg1) {
            pg_download.setMax(counter + 18);
            pg_download.setProgress(pg_download.getProgress() + 1);
            tv_status.setText("Cover (Die Dr3i): " + exI + " / 8 lädt");

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                SharedPreferences sp_app_prefs = getSharedPreferences("download_prefs",0);
                SharedPreferences.Editor editor = sp_app_prefs.edit();
                editor.putString("json","downloaded");
                editor.apply();

                Intent i = new Intent(DownloadActivity.this, MainActivity.class);
                startActivity(i);
            } catch (Exception ignored) {

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_EXTERNAL_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniApp();
        }else{
            Toast.makeText(this, "Ohne diese Berechtigung kann die App nicht benutzt werden.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}