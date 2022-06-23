package com.minwoo.playstoretester

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : AppCompatActivity() {


    private lateinit var appUpdateManager: AppUpdateManager

    companion object {
        const val MY_REQUEST_CODE = 700
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appUpdateManager = AppUpdateManagerFactory.create(MainActivity@this)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener {
            appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    MY_REQUEST_CODE
                )
            }
        }
        appUpdateInfoTask.addOnFailureListener {
            Log.d("dad", "onCreate: ddd")
        }




        findViewById<View>(R.id.button).setOnClickListener {
            try {
                val pi = applicationContext.packageManager.getPackageInfo(
                    applicationContext.packageName, 0
                )
                Toast.makeText(
                    this@MainActivity,
                    """
                        versionName : ${pi.versionName}
                        versionCode:${pi.versionCode}
                        """.trimIndent(),
                    Toast.LENGTH_SHORT
                ).show()
                (findViewById<View>(R.id.versionText) as TextView).text =
                    """
                    version6Name : ${pi.versionName}
                    versionCode:${pi.versionCode}
                    """.trimIndent()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                MaterialAlertDialogBuilder(this)
                    .setPositiveButton("OK") {
                        _, _ ->
                    }
                    .setMessage("Update is always good.")
                    .show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener {
                appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                    )
                }
            }
    }

    private fun popupSnackbarForCompleteUpdate() {
        appUpdateManager?.completeUpdate()
//        val snackbar = Snackbar.make(findViewById(R.id.clActivityMain), "업데이트 버전 다운로드 완료", 5000)
//            .setAction("설치/재시작") {
//                appUpdateManager?.completeUpdate()
//            }
//
//        snackbar.show()
    }
}