package com.johnai.apkmaker;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    EditText ideaInput, serverInput;
    TextView output, status;
    Button buildButton;
    LinearLayout root;

    int cyan = Color.rgb(0, 195, 255);
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(4, 9, 24));
        getWindow().setNavigationBarColor(Color.rgb(4, 9, 24));
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 24, 22, 20);
        root.setBackgroundColor(Color.rgb(4, 9, 24));
        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        setContentView(scroll);

        TextView brand = text("◈  JOHN AI", 30, cyan, true);
        root.addView(brand);
        TextView subtitle = text("APK MAKER  •  YOUR IDEAS. REAL APPS.", 13, Color.LTGRAY, true);
        root.addView(subtitle);
        space(22);

        root.addView(text("What do you want to build?", 22, Color.WHITE, true));
        root.addView(text("Describe your Android app idea. John AI will help turn it into a project specification and starter code.", 14, Color.LTGRAY, false));
        space(12);

        ideaInput = new EditText(this);
        ideaInput.setMinLines(5);
        ideaInput.setGravity(Gravity.TOP);
        ideaInput.setHint("Example: Make a notes app with dark mode, folders, search, and offline saving...");
        ideaInput.setTextColor(Color.WHITE);
        ideaInput.setHintTextColor(Color.GRAY);
        ideaInput.setBackgroundColor(Color.rgb(14, 28, 52));
        ideaInput.setPadding(14, 14, 14, 14);
        root.addView(ideaInput, new LinearLayout.LayoutParams(-1, -2));
        space(12);

        root.addView(text("AI BACKEND URL", 13, cyan, true));
        serverInput = new EditText(this);
        serverInput.setSingleLine(true);
        serverInput.setText("https://YOUR-BACKEND.example.com/generate");
        serverInput.setTextColor(Color.WHITE);
        serverInput.setHintTextColor(Color.GRAY);
        serverInput.setBackgroundColor(Color.rgb(14, 28, 52));
        serverInput.setPadding(12, 12, 12, 12);
        root.addView(serverInput);
        root.addView(text("Deploy the included backend and replace this example URL. Never put your OpenAI API key in this app.", 12, Color.LTGRAY, false));
        space(14);

        buildButton = new Button(this);
        buildButton.setText("✦  GENERATE APP PLAN");
        buildButton.setTextColor(Color.WHITE);
        buildButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(0, 130, 255)));
        root.addView(buildButton, new LinearLayout.LayoutParams(-1, -2));
        space(12);
        status = text("Ready when you are.", 14, cyan, true);
        root.addView(status);
        output = text("Your generated plan and starter guidance will appear here.", 14, Color.WHITE, false);
        output.setPadding(14, 14, 14, 14);
        output.setBackgroundColor(Color.rgb(10, 20, 38));
        root.addView(output);

        buildButton.setOnClickListener(v -> generate());
    }

    TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(0, 5, 0, 5); return t;
    }
    void space(int h) { View v = new View(this); root.addView(v, new LinearLayout.LayoutParams(1,h)); }

    void generate() {
        String idea = ideaInput.getText().toString().trim();
        String endpoint = serverInput.getText().toString().trim();
        if (idea.isEmpty()) { ideaInput.setError("Describe your app idea first"); return; }
        if (!endpoint.startsWith("https://") || endpoint.contains("YOUR-BACKEND")) {
            status.setText("Set your deployed HTTPS backend URL first.");
            return;
        }
        buildButton.setEnabled(false); status.setText("John AI is working...");
        new Thread(() -> {
            HttpURLConnection c = null;
            try {
                JSONObject payload = new JSONObject(); payload.put("idea", idea);
                c = (HttpURLConnection) new URL(endpoint).openConnection();
                c.setRequestMethod("POST"); c.setConnectTimeout(20000); c.setReadTimeout(90000);
                c.setDoOutput(true); c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                try (OutputStream os = c.getOutputStream()) { os.write(payload.toString().getBytes("UTF-8")); }
                int code = c.getResponseCode();
                InputStream is = code >= 200 && code < 300 ? c.getInputStream() : c.getErrorStream();
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                byte[] bytes = new byte[4096]; int n;
                while ((n = is.read(bytes)) != -1) buf.write(bytes, 0, n);
                JSONObject result = new JSONObject(buf.toString("UTF-8"));
                String answer = result.optString("result", result.optString("error", buf.toString("UTF-8")));
                runOnUiThread(() -> { output.setText(answer); status.setText(code >= 200 && code < 300 ? "Plan generated." : "Backend returned an error."); buildButton.setEnabled(true); });
            } catch (Exception e) {
                runOnUiThread(() -> { status.setText("Could not connect. Check the backend URL and internet."); output.setText(e.toString()); buildButton.setEnabled(true); });
            } finally { if (c != null) c.disconnect(); }
        }).start();
    }
}
