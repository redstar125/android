package com.redstar.runningapp.UI.Fragements

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentOnAttachListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.redstar.runningapp.R

class CancelTrackingDialog : DialogFragment() {

    private var yesListener:(()-> Unit)?=null
    fun setYesListener(listener: ()-> Unit){
        yesListener=listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cncel the Run")
            .setMessage("Are you Sure to cancel the run an delete the data ?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_ ->
                yesListener?.let { yes->yes() }
            }
            .setNegativeButton("No"){dialogInterface,_ ->
                dialogInterface.cancel()
            }
            .create()

    }
}