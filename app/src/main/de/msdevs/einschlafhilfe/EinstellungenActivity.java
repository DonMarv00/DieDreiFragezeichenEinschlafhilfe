package de.msdevs.einschlafhilfe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class EinstellungenActivity extends AppCompatActivity {

    int new_number;
    int new_number_zwei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_einstellungen);
        ini();


        CardView btn_hoch_one = (CardView) findViewById(R.id.btn_up_one);
        btn_hoch_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker_one("up");
            }
        });
        CardView btn_runter_one = (CardView) findViewById(R.id.btn_down_one);
        btn_runter_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker_one("down");
            }
        });

        CardView btn_hoch_two = (CardView) findViewById(R.id.btn_up_two);
        btn_hoch_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker_two("up");
            }
        });
        CardView btn_runter_two = (CardView) findViewById(R.id.btn_down_two);
        btn_runter_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker_two("down");
            }
        });


        Switch switch_notifications = (Switch) findViewById(R.id.switch_benachrichtigungen);
        switch_notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
                SharedPreferences.Editor sp_app_prefs_editor = sp_app_prefs.edit();
                if(isChecked){
                    sp_app_prefs_editor.putString("notfications_allowed", "");
                }else{
                    sp_app_prefs_editor.putString("notfications_allowed", "false");
                }
                sp_app_prefs_editor.commit();
            }
        });
    }
    public void ini(){
        SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);

        Switch switch_notifications = (Switch)findViewById(R.id.switch_benachrichtigungen);
        if(sp_app_prefs.getString("notfications_allowed", "") == ""){
            switch_notifications.setChecked(true);
        }

        TextView tv_picker_one = (TextView)findViewById(R.id.tv_number_one);
        TextView tv_picker_two = (TextView)findViewById(R.id.tv_number_two);

        tv_picker_one.setText(String.valueOf(sp_app_prefs.getInt("picker_one",0)));
        tv_picker_two.setText(String.valueOf(sp_app_prefs.getInt("picker_two",0)));
    }
    public void picker_one (String parameter){
        SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
        TextView tv_picker_one = (TextView)findViewById(R.id.tv_number_one);
        TextView tv_picker_two = (TextView)findViewById(R.id.tv_number_two);


        int number = Integer.valueOf(tv_picker_one.getText().toString());

        if(parameter == "up"){



            if (new_number > 200) {
            }else{
                new_number = number + 1;
            }
        }else{
            if (new_number > 1) {
                new_number = number - 1;

            }

            if(Integer.valueOf(tv_picker_two.getText().toString()) < new_number){
                new_number_zwei = new_number + 1;
            }
        }
        SharedPreferences.Editor sp_app_prefs_editor = sp_app_prefs.edit();
        sp_app_prefs_editor.putInt("picker_one", new_number);
        sp_app_prefs_editor.commit();
        sp_app_prefs_editor.putString("nummer_eins", String.valueOf(new_number));
        sp_app_prefs_editor.commit();

        tv_picker_one.setText(String.valueOf(new_number));
    }
    public void picker_two (String parameter){
        SharedPreferences sp_app_prefs = getSharedPreferences(getPackageName(), 0);
        TextView tv_picker_two = (TextView)findViewById(R.id.tv_number_two);


        int number =  Integer.valueOf(tv_picker_two.getText().toString());

        if(parameter == "up"){

            if (new_number_zwei > 200) {
            }else{
                new_number_zwei = number + 1;
            }
        }else{
            if (new_number_zwei > 1) {
                new_number_zwei = number - 1;

            }

        }
        SharedPreferences.Editor sp_app_prefs_editor = sp_app_prefs.edit();

        sp_app_prefs_editor.putInt("picker_two", new_number_zwei);
        sp_app_prefs_editor.commit();

        sp_app_prefs_editor.putString("nummer_zwei", String.valueOf(new_number_zwei));
        sp_app_prefs_editor.commit();

        tv_picker_two.setText(String.valueOf(new_number_zwei));
    }
}
