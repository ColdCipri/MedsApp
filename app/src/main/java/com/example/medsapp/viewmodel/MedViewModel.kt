package com.example.medsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.medsapp.database.MedRepository
import com.example.medsapp.database.MedRoomDatabase
import com.example.medsapp.service.Model
import kotlinx.coroutines.launch

class MedViewModel(application: Application) : AndroidViewModel(application)  {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository: MedRepository
    // LiveData gives us updated words when they change.
    val allMeds: LiveData<List<Model.Med>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val medDao = MedRoomDatabase.getDatabase(application, viewModelScope).medDao()
        repository = MedRepository(medDao)
        allMeds = repository.allMeds
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(med: Model.Med) = viewModelScope.launch {
        repository.insert(med)
    }
    fun update(med: Model.Med) = viewModelScope.launch {
        repository.update(med)
    }
    fun delete(med: Model.Med) = viewModelScope.launch {
        repository.delete(med)
    }
    fun insertAll(med: List<Model.Med>) = viewModelScope.launch {
        repository.insertAll(med)
    }
    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
    fun getMed(id: Int) : Model.Med {
        return repository.getMed(id)
    }
}