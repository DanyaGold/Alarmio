package com.example.alarmio.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.CodeScanner
import com.example.alarmio.R
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.QRCODE
import com.example.alarmio.databinding.ActivityScanBinding
import com.example.alarmio.service.AlarmService
import com.google.zxing.BarcodeFormat

class ScanActivity : AppCompatActivity() {

    private var mCodeScanner: CodeScanner? = null
    private var mScanMode = 0
    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        setDismissKeyguard()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        binding.activity = this
        retrieveScanMode()
        checkForPermissionsAndStartScanning()
    }

    private fun setDismissKeyguard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    }

    private fun checkForPermissionsAndStartScanning() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        } else {
            startScanning()
        }
    }

    private fun retrieveScanMode() {
        val intent = intent
        mScanMode = intent.getIntExtra(SCAN_MODE_KEY, MODE_DISMISS_ALARM)
    }

    private fun startScanning() {
        mCodeScanner = CodeScanner(this, binding.scannerView)
        setScanFormatToQR()
        mCodeScanner!!.setDecodeCallback { result ->
            runOnUiThread {
                if (mScanMode == MODE_DISMISS_ALARM) {
                    verifyQrCode(result.text)
                } else if (mScanMode == MODE_SET_DISMISS_CODE) {
                    val intent = Intent()
                    intent.putExtra(EXTRA_QR_CODE_HASH, result.text)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
        binding.scannerView.setOnClickListener { mCodeScanner!!.startPreview() }
    }

    private fun verifyQrCode(scannedData: String) {
        val qr = intent.getStringExtra(QRCODE)
        if (qr == scannedData) {
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
            finishAffinity()
        } else {
            showWrongCodeDialog()
        }
    }

    private fun setScanFormatToQR() {
        mCodeScanner!!.formats = listOf(BarcodeFormat.QR_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        val toastText = String.format("Разрешение не получено")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showWrongCodeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Неправильный QR-код")
            .setMessage("QR-код, сканированный вами, неверный.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                mCodeScanner?.startPreview()
            }
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        mCodeScanner?.startPreview()
    }

    override fun onPause() {
        mCodeScanner?.releaseResources()
        super.onPause()
    }

    companion object {
        const val SCAN_MODE_KEY = "ScanMode"
        const val MODE_DISMISS_ALARM = 0
        const val MODE_SET_DISMISS_CODE = 1
        const val EXTRA_QR_CODE_HASH = "QrCodeHash"
    }
}
