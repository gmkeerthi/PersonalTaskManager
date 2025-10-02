package com.example.personaltaskmanager.di

import android.content.Context
import androidx.room.Room
import com.example.personaltaskmanager.data.local.TaskDao
import com.example.personaltaskmanager.data.local.TaskDatabase
import com.example.personaltaskmanager.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): TaskDatabase {
        return Room.databaseBuilder(ctx, TaskDatabase::class.java, "tasks.db")
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    fun provideDao(db: TaskDatabase): TaskDao = db.taskDao()


    @Provides
    @Singleton
    fun provideRepository(dao: TaskDao): TaskRepository = TaskRepository(dao)
}