package com.redstar.runningapp.UI.ViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.redstar.runningapp.repositories.MainRepository
import javax.inject.Inject

class SatisticViewModels @ViewModelInject constructor(
    val mainRepository: MainRepository
):ViewModel() {
 val totalTimeRun=mainRepository.getTotalTimeInMillis()
 val totalDistance=mainRepository.getTotalDistance()
 val totalCaloriesBurned=mainRepository.getTotalCaloriesBurned()
 val totalAvgSpeed=mainRepository.getTotalAvgSpeed()

 val runSortedByDate=mainRepository.getAllRunStoredByDate()

}