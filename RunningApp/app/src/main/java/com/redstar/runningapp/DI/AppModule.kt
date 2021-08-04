package com.redstar.runningapp.DI

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.redstar.runningapp.DB.RunningDataBase
import com.redstar.runningapp.DB.RunningDataBase_Impl
import com.redstar.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.redstar.runningapp.other.Constants.KEY_NAME
import com.redstar.runningapp.other.Constants.KEY_WEIGHT
import com.redstar.runningapp.other.Constants.RUNNING_DATABASE_NAME
import com.redstar.runningapp.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDataBase(
        @ApplicationContext app:Context
    )= Room.databaseBuilder(
        app,
        RunningDataBase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides

    fun provideRunDao(db:RunningDataBase)=db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context)=app.getSharedPreferences(
        SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref:SharedPreferences)=sharedPref.getString(KEY_NAME,"")?:""

    @Singleton
    @Provides
    fun provideWeight(sharedPref:SharedPreferences)=sharedPref.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeToogle(sharedPref:SharedPreferences)=sharedPref.getBoolean(
        KEY_FIRST_TIME_TOGGLE,true)

}