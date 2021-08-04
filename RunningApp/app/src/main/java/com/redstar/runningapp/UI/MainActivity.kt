package com.redstar.runningapp.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.redstar.runningapp.DB.RunDAO
import com.redstar.runningapp.R
import com.redstar.runningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


//    @Inject
  //  lateinit var  runDAO:RunDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // Log.d("eunDAo","RUNDAO : ${runDAO.hashCode()}")
 //   setSupportActionBar(toolbar)

    navigateToTrackingFramgmentIfNedded(intent)
    bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    bottomNavigationView.setOnNavigationItemReselectedListener { /**/}
    navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id){
                    R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment->
                        bottomNavigationView.visibility=View.VISIBLE
                    else-> bottomNavigationView.visibility=View.GONE
                }
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFramgmentIfNedded(intent)
    }
    private fun navigateToTrackingFramgmentIfNedded(itent:Intent?){
        if (intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
}