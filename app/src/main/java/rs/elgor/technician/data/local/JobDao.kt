package rs.elgor.technician.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :jobId")
    fun getJobById(jobId: Int): Flow<JobEntity?>

    @Query("SELECT * FROM job_notes WHERE jobId = :jobId ORDER BY createdAt DESC")
    fun getNotesForJob(jobId: Int): Flow<List<JobNoteEntity>>

    @Query("SELECT * FROM job_photos WHERE jobId = :jobId ORDER BY createdAt DESC")
    fun getPhotosForJob(jobId: Int): Flow<List<JobPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<JobNoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<JobPhotoEntity>)

    @Query("DELETE FROM jobs")
    suspend fun deleteAllJobs()

    @Query("DELETE FROM job_notes WHERE jobId = :jobId")
    suspend fun deleteNotesForJob(jobId: Int)

    @Query("DELETE FROM job_photos WHERE jobId = :jobId")
    suspend fun deletePhotosForJob(jobId: Int)

    @Transaction
    suspend fun replaceJobs(jobs: List<JobEntity>) {
        deleteAllJobs()
        insertJobs(jobs)
    }
}
