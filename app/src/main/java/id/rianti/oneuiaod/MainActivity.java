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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root;
    private TextView status;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        buildMenu();
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(12, 12, 12, 12);
        return t;
    }

    private Button button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void buildMenu() {
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(22, 28, 22, 24);
        root.setBackgroundColor(Color.rgb(7, 9, 14));

        root.addView(text("ONE UI  •  AOD TOOLS", 13, Color.rgb(100, 181, 246)));
        TextView title = text("AOD Simulator", 30, Color.WHITE);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        root.addView(title);
        root.addView(text("SukiSU / Root diagnostic edition", 14, Color.LTGRAY));
        root.addView(text("SM-A066B • LCD display", 12, Color.rgb(145, 155, 170)));

        root.addView(button("Periksa akses ROOT", this::checkRoot));
        root.addView(button("Periksa dukungan touch wake (read-only)", this::checkTouchWake));
        root.addView(button("Buka preview jam 10 detik", this::showPreview));

        status = text("Status: belum diperiksa. Pemeriksaan hanya membaca informasi perangkat.", 13, Color.rgb(190, 205, 220));
        status.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(status);
        root.addView(text("Catatan: aplikasi tidak mengubah thermal, kernel, atau node sysfs. ROOT dipakai hanya untuk pemeriksaan baca-saja. Dukungan ketuk saat layar mati tetap bergantung pada driver touchscreen.", 12, Color.rgb(135, 145, 160)));

        scroll.addView(root);
        setContentView(scroll);
    }

    private void runRootCommand(String command, String heading) {
        status.setText(heading + "\nMeminta izin SukiSU...");
        new Thread(() -> {
            StringBuilder out = new StringBuilder();
            try {
                Process p = new ProcessBuilder("su", "-c", command).redirectErrorStream(true).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) out.append(line).append('\n');
                int code = p.waitFor();
                if (out.length() == 0) out.append("Tidak ada keluaran.");
                out.append("\nExit code: ").append(code);
            } catch (Exception e) {
                out.append("Gagal menjalankan su: ").append(e.getClass().getSimpleName())
                   .append("\nPastikan SukiSU aktif dan izin root diberikan.");
            }
            String result = out.toString();
            runOnUiThread(() -> status.setText(heading + "\n" + result));
        }).start();
    }

    private void checkRoot() {
        runRootCommand("id; echo MODEL=$(getprop ro.product.model); echo ANDROID=$(getprop ro.build.version.release)", "Hasil pemeriksaan ROOT");
    }

    private void checkTouchWake() {
        String cmd = "echo '--- Input devices ---'; "
            + "cat /proc/bus/input/devices | grep -iE 'name=|NVT|touch' ; "
            + "echo '--- Input wakeup flags ---'; "
            + "for f in /sys/class/input/input*/device/power/wakeup; do "
            + "if [ -r \"$f\" ]; then echo \"$f: $(cat $f)\"; fi; done";
        runRootCommand(cmd, "Pemeriksaan touch wake (read-only)");
    }

    private void showPreview() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER);
        screen.setBackgroundColor(Color.BLACK);
        screen.addView(text("SAMSUNG  •  ALWAYS ON DISPLAY", 11, Color.rgb(100, 181, 246)));
        TextView clock = text("", 76, Color.WHITE);
        clock.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        screen.addView(clock);
        screen.addView(text(new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(new Date()), 16, Color.LTGRAY));
        screen.addView(text("Preview berakhir otomatis dalam 10 detik", 12, Color.GRAY));
        setContentView(screen);
        Window w = getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.screenBrightness = 0.025f;
        w.setAttributes(lp);

        final long end = System.currentTimeMillis() + 10_000L;
        final Runnable tick = new Runnable() {
            @Override public void run() {
                clock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                if (System.currentTimeMillis() < end) handler.postDelayed(this, 1000);
                else {
                    WindowManager.LayoutParams p = getWindow().getAttributes();
                    p.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                    getWindow().setAttributes(p);
                    buildMenu();
                }
            }
        };
        handler.post(tick);
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
