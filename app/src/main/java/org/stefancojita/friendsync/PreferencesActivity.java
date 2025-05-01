package org.stefancojita.friendsync;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.widget.SwitchCompat;

public class PreferencesActivity extends AppCompatActivity {

    // private SwitchCompat switchTema;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_preferences);

//        switchTema = findViewById(R.id.switchTema);
        sharedPreferences = getSharedPreferences("preferencias", MODE_PRIVATE);


//        boolean temaOscuro = sharedPreferences.getBoolean("temaOscuro", false);
//        switchTema.setChecked(temaOscuro);
//
//        AppCompatDelegate.setDefaultNightMode(
//                temaOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//
//        switchTema.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("temaOscuro", isChecked);
//            editor.apply();
//
//            AppCompatDelegate.setDefaultNightMode(
//                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//        });

    }
}