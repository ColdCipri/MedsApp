package com.example.medsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.medsapp.service.Model;
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch




@Database(entities = arrayOf(Model.Med::class), version = 1, exportSchema = false)
abstract class MedRoomDatabase : RoomDatabase() {

    abstract fun medDao(): MedDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MedRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MedRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedRoomDatabase::class.java,
                    "med_database"
                ).addCallback(MedDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }


    private class MedDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.medDao())
                }
            }
        }

        suspend fun populateDatabase(medDao: MedDao) {
            // Delete all content here.
            medDao.deleteAll()

            // Add sample words.
            /*var med = Model.Med( 0,
                "Coldrex",
                "25-10-2020",
                10,
                "acetilina",
                "12mg",
                "Pentru durere de cap",
                "admin"
            )
            medDao.insert(med)
            med = Model.Med(1,"Acc", "23-08-2020", 11, "cilcion", "8mg", "Pentru durere de gat", "admin")
            medDao.insert(med)*/
        }
    }
}