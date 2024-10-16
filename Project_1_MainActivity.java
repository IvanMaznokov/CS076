package com.example.project1;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(view
                -> {
            Toast.makeText(this, "Hello SBVC!", Toast.LENGTH_SHORT).show();
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(view -> {
            Intent intent2 = new Intent(Intent.ACTION_SENDTO);

            intent2.setData(Uri.parse("mailto:"));
            startActivity(intent2);
        });

        Button button3 = findViewById(R.id.button3);
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    String url = "https://www.valleycollege.edu";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
    }
}
