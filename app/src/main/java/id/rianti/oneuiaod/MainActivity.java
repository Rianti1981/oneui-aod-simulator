package id.rianti.oneuiaod;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView clockView;
    private TextView dateView;
    private TextView hintView;
    private long lastInteraction;
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (clockView != null) {
                Date now = new Date();
                clockView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now));
                dateView.setText(new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now));
            }
            handler.postDelayed(this, 1000);
        }
    };
    private final Runnable timeout = new Runnable() {
        @Override public void run() {
            if (System.currentTimeMillis() - lastInteraction >= 10_000L) {
                finish();
            } else {
                handler.postDelayed(this, 10_000L - (System.currentTimeMillis() - lastInteraction));
            }
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = 0.025f;
        window.setAttributes(params);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(24, 24, 24, 24);

        TextView brand = new TextView(this);
        brand.setText("SAMSUNG  •  ALWAYS ON DISPLAY");
        brand.setTextColor(Color.rgb(110, 180, 235));
        brand.setTextSize(11);
        brand.setLetterSpacing(0.12f);
        brand.setGravity(Gravity.CENTER);

        clockView = new TextView(this);
        clockView.setTextColor(Color.WHITE);
        clockView.setTextSize(76);
        clockView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        clockView.setGravity(Gravity.CENTER);

        dateView = new TextView(this);
        dateView.setTextColor(Color.rgb(205, 215, 225));
        dateView.setTextSize(16);
        dateView.setGravity(Gravity.CENTER);

        TextView separator = new TextView(this);
        separator.setText("•");
        separator.setTextColor(Color.rgb(80, 155, 220));
        separator.setTextSize(24);
        separator.setGravity(Gravity.CENTER);
        separator.setPadding(0, 22, 0, 22);

        hintView = new TextView(this);
        hintView.setText("Ketuk layar untuk mengulang 10 detik");
        hintView.setTextColor(Color.rgb(115, 125, 140));
        hintView.setTextSize(12);
        hintView.setGravity(Gravity.CENTER);

        root.addView(brand);
        root.addView(clockView);
        root.addView(dateView);
        root.addView(separator);
        root.addView(hintView);
        setContentView(root);

        root.setOnClickListener(v -> resetTimeout());
        ticker.run();
        resetTimeout();
    }

    private void resetTimeout() {
        lastInteraction = System.currentTimeMillis();
        handler.removeCallbacks(timeout);
        handler.postDelayed(timeout, 10_000L);
    }

    @Override protected void onDestroy() {
        handler.removeCallbacks(ticker);
        handler.removeCallbacks(timeout);
        super.onDestroy();
    }
}
