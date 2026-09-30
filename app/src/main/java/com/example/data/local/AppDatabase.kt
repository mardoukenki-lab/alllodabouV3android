package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
    @Query("SELECT * FROM rides WHERE status = 'PENDING' ORDER BY createdAtMillis DESC LIMIT 50")
    fun getPendingRides(): Flow<List<RideEntity>>

    @Query("SELECT * FROM rides WHERE driverId = :driverId ORDER BY createdAtMillis DESC LIMIT 100")
    fun getDriverRides(driverId: String): Flow<List<RideEntity>>

    @Query("SELECT * FROM rides WHERE driverId = :driverId AND status = 'CONFIRMED' LIMIT 1")
    fun getActiveRide(driverId: String): Flow<RideEntity?>

    @Query("SELECT * FROM rides WHERE id = :rideId LIMIT 1")
    suspend fun getRideById(rideId: String): RideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRides(rides: List<RideEntity>)

    @Update
    suspend fun updateRide(ride: RideEntity)

    @Query("DELETE FROM rides WHERE id = :rideId")
    suspend fun deleteRide(rideId: String)
}

@Dao
interface DriverProfileDao {
    @Query("SELECT * FROM driver_profile LIMIT 1")
    fun getProfileFlow(): Flow<DriverProfileEntity?>

    @Query("SELECT * FROM driver_profile LIMIT 1")
    suspend fun getProfile(): DriverProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: DriverProfileEntity)

    @Query("UPDATE driver_profile SET available = :available WHERE uid = :uid")
    suspend fun updateAvailability(uid: String, available: Boolean)

    @Query("UPDATE driver_profile SET status = :status WHERE uid = :uid")
    suspend fun updateStatus(uid: String, status: String)

    @Query("DELETE FROM driver_profile")
    suspend fun clearProfile()
}

@Database(entities = [RideEntity::class, DriverProfileEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rideDao(): RideDao
    abstract fun driverProfileDao(): DriverProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "allo_dabou_driver.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
