package com.redstar.runningapp.repositories

import com.redstar.runningapp.DB.Run
import com.redstar.runningapp.DB.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDao:RunDAO
) {

    suspend fun insertRun(run: Run)=runDao.inserRun(run)

    suspend fun  deleteRun(run:Run)= runDao.deleteRun(run)

    fun getAllRunStoredByDate()= runDao.getAllRunSortedByDate()

    fun getAllRunsSortedByDistance()= runDao.getAllRunSortedByDistance()

    fun getAllRunStoredTimeMillis()= runDao.getAllRunSortedByTimeMillis()

    fun getAllRunStoredByAvgSpeed()= runDao.getAllRunSortedByAvgSpeed()

    fun getAllRunStoredByCaloriesBurned()= runDao.getAllRunSortedByCaloriesBurned()

    fun getTotalDistance()=runDao.getTotalDistance()

    fun getTotalAvgSpeed()=runDao.getTotalAvgSpeed()

    fun getTotalCaloriesBurned()=runDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis()=runDao.getTotalTimeInMillis()

}
