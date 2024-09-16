package info.sergeikolinichenko.myapplication.local.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import info.sergeikolinichenko.myapplication.local.models.CityDbModel
import info.sergeikolinichenko.myapplication.local.models.ForecastDbModel

/** Created by Sergei Kolinichenko on 23.02.2024 at 18:36 (GMT+3) **/
@Database(
  entities = [
    CityDbModel::class,
    ForecastDbModel::class
  ], 
  version = 5,
  exportSchema = true,
  autoMigrations = [
    AutoMigration(from = 1, to = 2, spec = AutoMigration_1_2::class),
    AutoMigration(from = 2, to = 3, spec = AutoMigration_2_3::class),
    AutoMigration(from = 3, to = 4)
  ]
)
@TypeConverters(Converters::class)
abstract class FreshWeatherDatabase : RoomDatabase() {
  abstract fun citiesDao(): FreshWeatherDao

  companion object {
    private const val DATABASE_NAME = "cities_database"
    private var INSTANCE: FreshWeatherDatabase? = null
    private val LOCK = Any()
    fun getInstance(context: Context): FreshWeatherDatabase {
      INSTANCE?.let { return it }

      synchronized(LOCK) {
        INSTANCE?.let { return it }

        val database = Room.databaseBuilder(
          context.applicationContext,
          FreshWeatherDatabase::class.java,
          DATABASE_NAME
        )
          .addMigrations(MIGRATION_4_5)
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

val MIGRATION_4_5 = object : Migration(4, 5) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.execSQL(
      """
      CREATE TABLE IF NOT EXISTS `forecast` 
      (
      `id` INTEGER PRIMARY KEY NOT NULL,
       `tzId` TEXT NOT NULL,
        `currentForecast` TEXT NOT NULL,
         `upcomingDays` TEXT NOT NULL,
          `upcomingHours` TEXT NOT NULL)
          """
        .trimIndent()
    )
  }
}
