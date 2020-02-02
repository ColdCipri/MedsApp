package com.example.medsapp.database

import androidx.lifecycle.LiveData
import com.example.medsapp.service.Model;

class MedRepository(private val medDao: MedDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allMeds: LiveData<List<Model.Med>> = medDao.getAll()

    suspend fun insert(med: Model.Med) {
        medDao.insert(med)
    }

    suspend fun insertAll(med: List<Model.Med>) {
        medDao.insertAll(med)
    }

    suspend fun update(med: Model.Med) {
        medDao.update(med)
    }

    suspend fun deleteAll() {
        medDao.deleteAll()
    }
    suspend fun delete(med: Model.Med) {
        medDao.delete(med)
    }
}