package com.pujh.ffmpeg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.ACTION_REQUEST_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSIONS
import androidx.core.app.ActivityCompat

class ExternalStoragePermission : ActivityResultContract<Void?, Boolean>() {

    override fun createIntent(context: Context, input: Void?): Intent {
        return if (Build.VERSION.SDK_INT >= 30) {
            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        } else {
            Intent(ACTION_REQUEST_PERMISSIONS)
                .putExtra(
                    EXTRA_PERMISSIONS,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        if (Build.VERSION.SDK_INT >= 30) {
            return Environment.isExternalStorageManager()
        } else {
            if (intent == null || resultCode != Activity.RESULT_OK) return false
            val grantResults =
                intent.getIntArrayExtra(RequestMultiplePermissions.EXTRA_PERMISSION_GRANT_RESULTS)
            return grantResults?.any { result ->
                result == PackageManager.PERMISSION_GRANTED
            } == true
        }
    }
}

fun Activity.checkExternalStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= 30) {
        Environment.isExternalStorageManager()
    } else {
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}