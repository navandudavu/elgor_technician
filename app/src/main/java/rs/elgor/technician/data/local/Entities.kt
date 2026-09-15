package rs.elgor.technician.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: Int,
    val customerId: Int,
    val applianceId: Int?,
    val assignedTo: Int?,
    val createdBy: Int?,
    val title: String,
    val description: String?,
    val status: String,
    val priority: String,
    val scheduledAt: String?,
    val finishedAt: String?,
    val address: String?,
    val hoursLogged: Double?,
    val createdAt: String,
    val updatedAt: String,
    val customerName: String?,
    val customerPhone: String?,
    val applianceType: String?,
    val applianceBrand: String?,
    val applianceModel: String?,
    val applianceSerial: String?,
    val assignedToName: String?
)

@Entity(
    tableName = "job_notes",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("jobId")]
)
data class JobNoteEntity(
    @PrimaryKey val id: Int,
    val jobId: Int,
    val authorId: Int?,
    val note: String,
    val createdAt: String,
    val authorName: String?
)

@Entity(
    tableName = "job_photos",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("jobId")]
)
data class JobPhotoEntity(
    @PrimaryKey val id: Int,
    val jobId: Int,
    val uploadedBy: Int?,
    val filename: String,
    val originalName: String?,
    val caption: String?,
    val category: String,
    val createdAt: String,
    val uploadedByName: String?
)
