package rs.elgor.technician.data.local

import rs.elgor.technician.model.Job
import rs.elgor.technician.model.JobNote
import rs.elgor.technician.model.JobPhoto

fun Job.toEntity(): JobEntity = JobEntity(
    id = id,
    customerId = customerId,
    applianceId = applianceId,
    assignedTo = assignedTo,
    createdBy = createdBy,
    title = title,
    description = description,
    status = status,
    priority = priority,
    scheduledAt = scheduledAt,
    finishedAt = finishedAt,
    address = address,
    hoursLogged = hoursLogged,
    createdAt = createdAt,
    updatedAt = updatedAt,
    customerName = customerName,
    customerPhone = customerPhone,
    applianceType = applianceType,
    applianceBrand = applianceBrand,
    applianceModel = applianceModel,
    applianceSerial = applianceSerial,
    assignedToName = assignedToName
)

fun JobNote.toEntity(): JobNoteEntity = JobNoteEntity(
    id = id,
    jobId = jobId,
    authorId = authorId,
    note = note,
    createdAt = createdAt,
    authorName = authorName
)

fun JobPhoto.toEntity(): JobPhotoEntity = JobPhotoEntity(
    id = id,
    jobId = jobId,
    uploadedBy = uploadedBy,
    filename = filename,
    originalName = originalName,
    caption = caption,
    category = category,
    createdAt = createdAt,
    uploadedByName = uploadedByName
)

fun JobEntity.toDomain(
    notes: List<JobNoteEntity> = emptyList(),
    photos: List<JobPhotoEntity> = emptyList()
): Job = Job(
    id = id,
    customerId = customerId,
    applianceId = applianceId,
    assignedTo = assignedTo,
    createdBy = createdBy,
    title = title,
    description = description,
    status = status,
    priority = priority,
    scheduledAt = scheduledAt,
    finishedAt = finishedAt,
    address = address,
    hoursLogged = hoursLogged,
    createdAt = createdAt,
    updatedAt = updatedAt,
    customerName = customerName,
    customerPhone = customerPhone,
    applianceType = applianceType,
    applianceBrand = applianceBrand,
    applianceModel = applianceModel,
    applianceSerial = applianceSerial,
    assignedToName = assignedToName,
    notes = notes.map { it.toDomain() },
    photos = photos.map { it.toDomain() }
)

fun JobNoteEntity.toDomain(): JobNote = JobNote(
    id = id,
    jobId = jobId,
    authorId = authorId,
    note = note,
    createdAt = createdAt,
    authorName = authorName
)

fun JobPhotoEntity.toDomain(): JobPhoto = JobPhoto(
    id = id,
    jobId = jobId,
    uploadedBy = uploadedBy,
    filename = filename,
    originalName = originalName,
    caption = caption,
    category = category,
    createdAt = createdAt,
    uploadedByName = uploadedByName
)
