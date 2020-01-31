package com.example.medsapp.service

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object Model {
    data class User(@PrimaryKey
                    @ColumnInfo(name = "email") val email: String,
                    @ColumnInfo(name = "password") val password: String,
                    @ColumnInfo(name = "name") val name: String,
                    @ColumnInfo(name = "age") val age: Int,
                    @ColumnInfo(name = "color") val color: String)


    @Entity
    data class Med(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "exp_date") val exp_date: String,
        @ColumnInfo(name = "pieces") val pieces: Int,
        @ColumnInfo(name = "base_subst") val base_subst: String,
        @ColumnInfo(name = "quantity") val quantity: String,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "userEmail") val userEmail: String
    )
}