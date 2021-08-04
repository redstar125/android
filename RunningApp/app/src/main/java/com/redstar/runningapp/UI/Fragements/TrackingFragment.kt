package com.redstar.runningapp.UI.Fragements

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.redstar.runningapp.DB.Run
import com.redstar.runningapp.R
import com.redstar.runningapp.Services.Polyline
import com.redstar.runningapp.Services.TrackingService
import com.redstar.runningapp.UI.ViewModels.MainViewModels
import com.redstar.runningapp.other.Constants.ACTION_Pause_SERVICE
import com.redstar.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.redstar.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.redstar.runningapp.other.Constants.CANCEL_TRACKING_DIALOG_TAG
import com.redstar.runningapp.other.Constants.MAP_ZOOM
import com.redstar.runningapp.other.Constants.POLYLINE_COLOR
import com.redstar.runningapp.other.Constants.POLYLINE_WIDTH
import com.redstar.runningapp.other.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val  viewModel: MainViewModels by viewModels()

    private var  map:GoogleMap?=null

    private var isTracking=false
    private var pathPoints= mutableListOf<Polyline>()

    private var curTimeInMillis=0L

    private  var menu:Menu?=null

    @set:Inject
    var  weight=80f


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
       btnToggleRun.setOnClickListener{
          // sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        toogleRun()
       }

        if(savedInstanceState!=null){
            val cancelTrackingDialog=parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG
            )as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }

        }
        btnFinishRun.setOnClickListener{
            zoomToSeeWholeTrack()
            enRunAndSaveToDb()
        }
        mapView.getMapAsync {
            map= it
            addAllPolylines()
        }
        subscribeToservers()

    }

    private fun subscribeToservers()
    {

        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLasestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis=it
            val formattedTime=TrackingUtility.getFormatedStopWatchTime(curTimeInMillis,true)
            tvTimer.text=formattedTime
        })
    }

    private fun toogleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible=true
            sendCommandToService(ACTION_Pause_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu=menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis>0L){
            this.menu?.getItem(0)?.isVisible=true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking->{
                showCnacelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showCnacelTrackingDialog(){
    CancelTrackingDialog().apply {
        setYesListener {
            stopRun()
        }
    }.show(parentFragmentManager,CANCEL_TRACKING_DIALOG_TAG)

    }
    private  fun stopRun(){

        tvTimer.text="00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }
    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking && curTimeInMillis>0L){
            btnToggleRun.text="Start"
            btnFinishRun.visibility=View.VISIBLE
        }else if (isTracking){
            btnToggleRun.text="Stop"
            menu?.getItem(0)?.isVisible=true
            btnFinishRun.visibility=View.GONE
        }
    }
    private fun moveCameraToUser(){
        if (pathPoints.isNotEmpty()&& pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            pathPoints.last().last(),
                            MAP_ZOOM
                    )
            )
        }
    }

    private  fun zoomToSeeWholeTrack(){
        val bounds =LatLngBounds.Builder()
        for (polyline in pathPoints){
            for (pos in polyline){
                bounds.include(pos)
            }
        }

        map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                        bounds.build(),
                        mapView.width,
                        mapView.height,
                        (mapView.height*0.05f).toInt()
                )
        )
    }

    private fun enRunAndSaveToDb(){
        map?.snapshot { bmp->
        var distanceInMeters=0
            for (polyline in pathPoints){
                distanceInMeters+=TrackingUtility.calculatePolylineLength(polyline).toInt()

            }
            val avgSpeed= round((distanceInMeters/1000f) /(curTimeInMillis/1000f/60/60)*10)/10f
            val dateTimestap=Calendar.getInstance().timeInMillis
            val caloriesBurned=((distanceInMeters/1000f)*weight).toInt()
            val run=Run(bmp,dateTimestap,avgSpeed,distanceInMeters,curTimeInMillis,caloriesBurned)
            viewModel.inserRun(run)
            Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    "Run Saved Successfully",
                    Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }
    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions=PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }
    private fun addLasestPolyline(){
        if(pathPoints.isNotEmpty()&& pathPoints.last().size>1){
            val preLastLatLng=pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng=pathPoints.last().last()
            val polylineOptions= PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .add(preLastLatLng)
                    .add(lastLatLng)
            map?.addPolyline(polylineOptions)

        }
    }

    private fun sendCommandToService(action:String)=
            Intent(requireContext(),TrackingService::class.java).also{
                it.action=action
                requireContext().startService(it)
            }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


}