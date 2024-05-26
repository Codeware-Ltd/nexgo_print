package com.example.nexgo_print;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.device.printer.GrayLevelEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;

import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    String CHANNEL = "com.example.nexgo_print/printer";
    DeviceEngine deviceEngine;
    Printer printer;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NexGoTest");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ref.push().setValue("in onCreate method");
            deviceEngine = ((NexgoApplication) getApplication()).deviceEngine;
            ref.push().setValue("deviceEngine: " + deviceEngine);
            printer = deviceEngine.getPrinter();
            printer.setTypeface(Typeface.DEFAULT);
            ref.push().setValue("printer: " + printer);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            ref.push().setValue("Initialize Error: " + e.getMessage());
        }
    }
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if(Objects.equals(call.method, "printing")){
                                try {
                                    System.out.println(printer);
                                    printer.initPrinter();
                                    printer.appendPrnStr("NexGo Printer Test", 24, AlignEnum.LEFT, false);
                                    printer.appendPrnStr("NexGo Printer Test2", 24, AlignEnum.CENTER, true);
                                    printer.appendQRcode("Hello", 200, AlignEnum.CENTER);
                                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logonexgo);
                                    printer.appendImage(bitmap, AlignEnum.CENTER);
                                    printer.startPrint(false, new OnPrintListener() {       //roll paper or not
                                        @Override
                                        public void onPrintResult(final int retCode) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(MainActivity.this, "Print Done", Toast.LENGTH_SHORT).show();
                                                    ref.push().setValue("Printing Done" );
                                                }
                                            });
                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    ref.push().setValue("Printing Error: " + e.getMessage());
                                }
                                result.success(deviceEngine.getDeviceInfo().getSn());
                            }
                        }
                );
    }
}
