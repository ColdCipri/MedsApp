package com.example.medsapp.service

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object Model {
    data class User(@ColumnInfo(name = "email") var email: String,
                    @ColumnInfo(name = "password") var password: String,
                    @ColumnInfo(name = "name") var name: String,
                    @ColumnInfo(name = "age") var age: Int,
                    @ColumnInfo(name = "color") var color: String)
    {
        @PrimaryKey(autoGenerate = true) var id: Int = 1
    }

    @Entity
    data class Med(@ColumnInfo(name = "name") var name: String,
                   @ColumnInfo(name = "exp_date") var best_before: String,
                   @ColumnInfo(name = "pieces") var pieces: Int,
                   @ColumnInfo(name = "base_subst") var base_substance: String,
                   @ColumnInfo(name = "quantity") var base_substance_quantity: String,
                   @ColumnInfo(name = "description") var description: String,
                   @ColumnInfo(name = "userId") var userId: Int)
    {
        @PrimaryKey(autoGenerate = true) var id: Int = 1
    }
}