package com.whitelink.app;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final int REQ_PERMS = 41;
    private static final int REQ_ENABLE_BT = 42;
    private static final UUID APP_UUID =
        UUID.fromString("8d7b2f10-4e35-4c8a-9f12-2d4a6b8c0e11");

    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private OutputStream output;
    private TextView status, chat;
    private EditText messageBox;
    private LinearLayout devicesBox;
    private final ArrayList<BluetoothDevice> foundDevices = new ArrayList<>();
    private boolean receiverRegistered = false;

    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !containsDevice(device)) {
                    foundDevices.add(device);
                    addDeviceButton(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setStatus("An gama neman na'urori. Zaɓi wayar da ta bayyana.");
            }
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        adapter = BluetoothAdapter.getDefaultAdapter();
        buildScreen();
        if (adapter == null) {
            setStatus("Wannan waya ba ta da Bluetooth.");
            disableControls();
            return;
        }
        requestNeededPermissions();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(22), dp(18), dp(24));
        page.setBackgroundColor(0xFFF4F7FA);
        scroll.addView(page);

        TextView title = new TextView(this);
        title.setText("WHITE-LINK");
        title.setTextSize(27);
        title.setTextColor(0xFF102A43);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, 1);
        page.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("Sadarwa kai tsaye — ba tare da data ko airtime ba");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF52606D);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(5), 0, dp(18));
        page.addView(subtitle, matchWrap());

        status = new TextView(this);
        status.setText("Ana shirin farawa...");
        status.setTextSize(14);
        status.setTextColor(0xFF176B45);
        status.setPadding(dp(12), dp(12), dp(12), dp(12));
        status.setBackgroundColor(0xFFE1F3E8);
        page.addView(status, matchWrap());

        Button enable = makeButton("Kunna Bluetooth");
        enable.setOnClickListener(v -> enableBluetooth());
        page.addView(enable, matchWrap());

        Button scan = makeButton("Nemo wayoyin da ke kusa");
        scan.setOnClickListener(v -> startScan());
        page.addView(scan, matchWrap());

        Button host = makeButton("Bari wasu su haɗu da ni");
        host.setOnClickListener(v -> startServer());
        page.addView(host, matchWrap());

        TextView devicesTitle = sectionTitle("Wayoyin da aka gano");
        page.addView(devicesTitle, matchWrap());
        devicesBox = new LinearLayout(this);
        devicesBox.setOrientation(LinearLayout.VERTICAL);
        page.addView(devicesBox, matchWrap());

        TextView chatTitle = sectionTitle("Saƙonni");
        page.addView(chatTitle, matchWrap());
        chat = new TextView(this);
        chat.setText("Ba a haɗa waya ba tukuna.");
        chat.setTextSize(14);
        chat.setTextColor(0xFF243B53);
        chat.setPadding(dp(12), dp(12), dp(12), dp(12));
        chat.setMinHeight(dp(150));
        chat.setBackgroundColor(0xFFFFFFFF);
        page.addView(chat, matchWrap());

        messageBox = new EditText(this);
        messageBox.setHint("Rubuta saƙonka...");
        messageBox.setSingleLine(true);
        page.addView(messageBox, matchWrap());

        Button send = makeButton("Aika saƙo");
        send.setOnClickListener(v -> sendMessage());
        page.addView(send, matchWrap());

        TextView note = new TextView(this);
        note.setText("Lura: Don haɗawa, kunna Bluetooth a wayoyin biyu. Wayoyin za su iya buƙatar pairing/accept. A fara gwaji da wayoyi biyu kusa da juna.");
        note.setTextSize(12);
        note.setTextColor(0xFF52606D);
        note.setPadding(0, dp(16), 0, 0);
        page.addView(note, matchWrap());

        setContentView(scroll);
    }

    private TextView sectionTitle(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(18);
        t.setTextColor(0xFF102A43);
        t.setTypeface(null, 1);
        t.setPadding(0, dp(18), 0, dp(8));
        return t;
    }

    private Button makeButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        return b;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setStatus(String value) {
        runOnUiThread(() -> status.setText(value));
    }

    private void appendChat(String value) {
        runOnUiThread(() -> {
            if (chat.getText().toString().equals("Ba a haɗa waya ba tukuna.")) chat.setText("");
            chat.append(value + "\n");
        });
    }

    private void disableControls() {
        setStatus("Ba a samu Bluetooth a wannan wayar ba.");
    }

    private String[] requiredPermissions() {
        if (Build.VERSION.SDK_INT >= 31) {
            return new String[]{Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT};
        }
        if (Build.VERSION.SDK_INT >= 23) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        }
        return new String[0];
    }

    private boolean hasPermissions() {
        for (String p : requiredPermissions()) {
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    private void requestNeededPermissions() {
        String[] permissions = requiredPermissions();
        if (Build.VERSION.SDK_INT >= 23 && !hasPermissions()) {
            requestPermissions(permissions, REQ_PERMS);
        } else {
            setStatus("A shirye. Kunna Bluetooth sannan ku nemi wayoyin kusa.");
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQ_PERMS) {
            boolean ok = true;
            for (int result : results) if (result != PackageManager.PERMISSION_GRANTED) ok = false;
            setStatus(ok ? "An ba da izini. Kunna Bluetooth." :
                "Ba a ba da izinin Bluetooth ba. Ba za a iya nemo waya ba.");
        }
    }

    private boolean hasConnectPermission() {
        return Build.VERSION.SDK_INT < 31 ||
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    private void enableBluetooth() {
        if (!hasPermissions()) { requestNeededPermissions(); return; }
        try {
            if (!adapter.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQ_ENABLE_BT);
            } else setStatus("Bluetooth yana kunne.");
        } catch (SecurityException e) {
            setStatus("An hana izinin Bluetooth. Duba izini a Settings.");
        }
    }

    private void registerDiscoveryReceiver() {
        if (receiverRegistered) return;
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(discoveryReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(discoveryReceiver, filter);
        }
        receiverRegistered = true;
    }

    private void startScan() {
        if (!hasPermissions()) { requestNeededPermissions(); return; }
        try {
            if (!adapter.isEnabled()) {
                setStatus("Da farko ka kunna Bluetooth.");
                enableBluetooth();
                return;
            }
            foundDevices.clear();
            devicesBox.removeAllViews();
            registerDiscoveryReceiver();

            // Show already paired devices too.
            Set<BluetoothDevice> paired = adapter.getBondedDevices();
            for (BluetoothDevice d : paired) {
                if (!containsDevice(d)) {
                    foundDevices.add(d);
                    addDeviceButton(d);
                }
            }
            if (adapter.isDiscovering()) adapter.cancelDiscovery();
            boolean started = adapter.startDiscovery();
            setStatus(started ? "Ana neman wayoyin kusa... Ka jira kaɗan." :
                "Ba a fara bincike ba. Ka sake gwadawa.");
        } catch (SecurityException e) {
            setStatus("Izinin neman na'urori bai samu ba.");
        }
    }

    private boolean containsDevice(BluetoothDevice candidate) {
        for (BluetoothDevice d : foundDevices) {
            try { if (d.getAddress().equals(candidate.getAddress())) return true; }
            catch (SecurityException ignored) {}
        }
        return false;
    }

    private void addDeviceButton(BluetoothDevice device) {
        String label;
        try {
            String name = device.getName();
            label = (name == null || name.trim().isEmpty() ? "Waya marar suna" : name)
                + "  •  " + device.getAddress();
        } catch (SecurityException e) {
            label = "Na'ura kusa";
        }
        Button b = makeButton(label + " — Haɗa");
        b.setOnClickListener(v -> connectTo(device));
        runOnUiThread(() -> devicesBox.addView(b, matchWrap()));
    }

    private void startServer() {
        if (!hasPermissions()) { requestNeededPermissions(); return; }
        try {
            if (!adapter.isEnabled()) { setStatus("Da farko ka kunna Bluetooth."); enableBluetooth(); return; }
            setStatus("Ana jiran wata waya ta haɗu. Ka bar wannan shafin a buɗe.");
            new Thread(() -> {
                BluetoothServerSocket server = null;
                try {
                    server = adapter.listenUsingRfcommWithServiceRecord("WHITE-LINK", APP_UUID);
                    BluetoothSocket accepted = server.accept();
                    attachSocket(accepted);
                } catch (Exception e) {
                    setStatus("Ba a samu haɗi ba: " + safeMessage(e));
                } finally {
                    try { if (server != null) server.close(); } catch (Exception ignored) {}
                }
            }).start();
        } catch (SecurityException e) {
            setStatus("Ba a samu izinin Bluetooth Connect ba.");
        }
    }

    private void connectTo(BluetoothDevice device) {
        if (!hasPermissions()) { requestNeededPermissions(); return; }
        new Thread(() -> {
            try {
                if (adapter.isDiscovering()) adapter.cancelDiscovery();
                BluetoothSocket candidate = device.createRfcommSocketToServiceRecord(APP_UUID);
                setStatus("Ana ƙoƙarin haɗawa...");
                candidate.connect();
                attachSocket(candidate);
            } catch (Exception e) {
                setStatus("Haɗin bai yi nasara ba: " + safeMessage(e) +
                    ". Tabbatar ɗayan wayar tana jiran haɗi.");
            }
        }).start();
    }

    private synchronized void attachSocket(BluetoothSocket connected) {
        try {
            socket = connected;
            output = connected.getOutputStream();
            String remote = "wani";
            try { if (connected.getRemoteDevice().getName() != null) remote = connected.getRemoteDevice().getName(); }
            catch (Exception ignored) {}
            setStatus("An haɗu da " + remote + ". Za ku iya aika saƙo.");
            appendChat("— An haɗu —");
            new Thread(() -> readMessages(connected)).start();
        } catch (Exception e) {
            setStatus("An samu matsala wajen buɗe haɗin: " + safeMessage(e));
        }
    }

    private void readMessages(BluetoothSocket activeSocket) {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(activeSocket.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) appendChat("Su: " + line);
        } catch (Exception e) {
            appendChat("— Haɗin ya katse —");
            setStatus("Haɗin ya katse. Za ku iya sake haɗawa.");
        }
    }

    private void sendMessage() {
        String message = messageBox.getText().toString().trim();
        if (message.isEmpty()) return;
        if (socket == null || !socket.isConnected() || output == null) {
            setStatus("Ba a haɗa da wata waya ba tukuna.");
            return;
        }
        new Thread(() -> {
            try {
                output.write((message + "\n").getBytes(StandardCharsets.UTF_8));
                output.flush();
                appendChat("Ni: " + message);
                runOnUiThread(() -> messageBox.setText(""));
            } catch (Exception e) {
                setStatus("Saƙon bai tafi ba: " + safeMessage(e));
            }
        }).start();
    }

    private String safeMessage(Exception e) {
        String m = e.getMessage();
        return (m == null || m.trim().isEmpty()) ? e.getClass().getSimpleName() : m;
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        try { if (adapter != null && adapter.isDiscovering()) adapter.cancelDiscovery(); } catch (Exception ignored) {}
        if (receiverRegistered) {
            try { unregisterReceiver(discoveryReceiver); } catch (Exception ignored) {}
            receiverRegistered = false;
        }
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
    }
}
