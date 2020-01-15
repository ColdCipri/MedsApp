package com.example.medsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.medsapp.service.Model;


@Dao
interface MedDao {
    @Query("SELECT * FROM med")
    fun getAll(): LiveData<List<Model.Med>>

    @Query("SELECT * FROM med WHERE id=:id")
    fun getMed(id:Int): Model.Med

    @Update
    fun update(med: Model.Med)

    @Insert
    suspend fun insertAll(meds: List<Model.Med>)

    @Insert
    fun insert(med: Model.Med)

    @Delete
    fun delete(med: Model.Med)

    @Query("DELETE FROM med")
    suspend fun deleteAll()
}