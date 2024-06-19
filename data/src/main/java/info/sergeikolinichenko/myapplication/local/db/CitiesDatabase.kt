package info.sergeikolinichenko.myapplication.local.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import info.sergeikolinichenko.myapplication.local.models.CityDbModel

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:36 (GMT+3) **/
@Database(
  entities = [CityDbModel::class],
  version = 3,
  exportSchema = true,
  autoMigrations = [
    AutoMigration(from = 1, to = 2, spec = AutoMigration_1_2::class),
    AutoMigration(from = 2, to = 3, spec = AutoMigration_2_3::class)
  ]
)
abstract class CitiesDatabase : RoomDatabase() {
  abstract fun citiesDao(): CitiesDao

  companion object {
    const val DATABASE_NAME = "cities_database"
    private var INSTANCE: CitiesDatabase? = null
    private val LOCK = Any()
    fun getInstance(context: Context): CitiesDatabase {
      INSTANCE?.let { return it }

      synchronized(LOCK) {
        INSTANCE?.let { return it }

        val database = Room.databaseBuilder(
          context.applicationContext,
          CitiesDatabase::class.java,
          DATABASE_NAME
        )
          .build()
        INSTANCE = database
        return database
      }
    }
  }
}

@RenameColumn(tableName = "favourite_cities", fromColumnName = "url", toColumnName = "timeZone")
class AutoMigration_1_2 : AutoMigrationSpec

@DeleteColumn(tableName = "favourite_cities", columnName = "timeZone")
class AutoMigration_2_3 : AutoMigrationSpec