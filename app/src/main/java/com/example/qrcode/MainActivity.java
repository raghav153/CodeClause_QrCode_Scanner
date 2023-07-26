package com.example.qrcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qrcode.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private CompoundBarcodeView barcodeScannerView;
    private EditText qrCodeText;
    private ImageView qrCodeImageView;
    private Button generateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeScannerView = findViewById(R.id.barcodeScanner);
        qrCodeText = findViewById(R.id.qrCodeText);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        generateButton = findViewById(R.id.generateButton);

        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            setupScanner();
        }

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });
    }

    private void setupScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null) {
                    Toast.makeText(MainActivity.this, "Scanned: " + result.getText(),
                            Toast.LENGTH_SHORT).show();
                    // Handle the scanned result here.
                }
            }
        @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {}
        });
    }

    private void generateQRCode() {
        String qrCodeContent = qrCodeText.getText().toString().trim();
        if (!qrCodeContent.isEmpty()) {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            try {
                Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeContent,
                        BarcodeFormat.QR_CODE, 200, 200);
                qrCodeImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter text for QR code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupScanner();
            } else {
                Toast.makeText(this, "Camera permission required for scanning",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}
