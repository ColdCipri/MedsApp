package com.example.medsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.medsapp.service.Model;


@Dao
interface MedDao {
    @Query("SELECT * FROM med")
    fun getAll(): LiveData<List<Model.Med>>

    @Update
    suspend fun update(med: Model.Med)

    @Insert
    suspend fun insertAll(meds: List<Model.Med>)

    @Insert
    suspend fun insert(med: Model.Med)

    @Delete
    suspend fun delete(med: Model.Med)

    @Query("DELETE FROM med")
    suspend fun deleteAll()
}