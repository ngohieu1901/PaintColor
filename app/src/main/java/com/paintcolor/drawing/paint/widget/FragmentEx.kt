package com.paintcolor.drawing.paint.widget

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

//start activity
fun Fragment.launchActivity(
    clazz: Class<*>
) {
    startActivity(Intent(context, clazz))
}

fun Fragment.launchActivity(
    option: Bundle? = null,
    clazz: Class<*>
) {
    val intent = Intent(context, clazz)
    intent.putExtra("data_bundle", option)
    startActivity(intent)
}

fun Fragment.finishActivity() {
    activity?.finish()
}

fun Fragment.finishAffinity() {
    activity?.finishAffinity()
}

fun Fragment.currentBundle(): Bundle? {
    return activity?.intent?.getBundleExtra("data_bundle")
}

fun Fragment.launchActivityForResult(
    callback: (Boolean) -> Unit
) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        callback.invoke(result.resultCode == AppCompatActivity.RESULT_OK)
    }
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.findParentNavController(): NavController? {
    var parent = parentFragment
    while (parent != null && parent !is NavHostFragment) {
        parent = parent.parentFragment
    }
    return parent?.parentFragment?.findNavController()
}
