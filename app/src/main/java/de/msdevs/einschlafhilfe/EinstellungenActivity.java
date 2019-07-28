package de.msdevs.einschlafhilfe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class EinstellungenActivity extends AppCompatActivity {

    EditText et_von, et_bis;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_einstellungen);
        ini();

        et_von.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_von.length() > 0){
                    SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
                    SharedPreferences.Editor editor = sp_app_prefs.edit();
                    editor.putInt("picker_one", Integer.valueOf(et_von.getText().toString()));

                    if(Integer.valueOf(et_von.getText().toString()) > Integer.valueOf(et_bis.getText().toString())){
                        int i4 = Integer.valueOf(et_von.getText().toString()) + 1;
                        String changed = String.valueOf(i4);
                        et_bis.setText(changed);
                        editor.putInt("picker_two", i4);
                    }

                    editor.apply();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        et_bis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_bis.length() > 0){
                    SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
                    SharedPreferences.Editor editor = sp_app_prefs.edit();
                    editor.putInt("picker_two", Integer.valueOf(et_bis.getText().toString()));

                    if(Integer.valueOf(et_bis.getText().toString()) < Integer.valueOf(et_von.getText().toString())){
                        int i4 = Integer.valueOf(et_von.getText().toString()) + 1;
                        String changed = String.valueOf(i4);
                        et_bis.setText(changed);
                        editor.putInt("picker_two", i4);
                    }
                    editor.apply();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    public void ini(){
        SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
        //Benutzerdefinierte Einstellungen laden
        et_von = findViewById(R.id.editText_von);
        et_bis = findViewById(R.id.editText_bis);

        et_von.setText(String.valueOf(sp_app_prefs.getInt("picker_one",0)));
        et_bis.setText(String.valueOf(sp_app_prefs.getInt("picker_two",0)));
    }


}
