package rs.elgor.technician.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000bH\'J\u0018\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u000b2\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\f0\u000b2\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\f0\u000b2\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u0016\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u001c\u0010\u0016\u001a\u00020\u00032\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001c\u0010\u0019\u001a\u00020\u00032\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00100\fH\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001c\u0010\u001b\u001a\u00020\u00032\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00120\fH\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001c\u0010\u001d\u001a\u00020\u00032\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0097@\u00a2\u0006\u0002\u0010\u0018\u00a8\u0006\u001e"}, d2 = {"Lrs/elgor/technician/data/local/JobDao;", "", "deleteAllJobs", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteNotesForJob", "jobId", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletePhotosForJob", "getAllJobs", "Lkotlinx/coroutines/flow/Flow;", "", "Lrs/elgor/technician/data/local/JobEntity;", "getJobById", "getNotesForJob", "Lrs/elgor/technician/data/local/JobNoteEntity;", "getPhotosForJob", "Lrs/elgor/technician/data/local/JobPhotoEntity;", "insertJob", "job", "(Lrs/elgor/technician/data/local/JobEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertJobs", "jobs", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertNotes", "notes", "insertPhotos", "photos", "replaceJobs", "app_release"})
@androidx.room.Dao()
public abstract interface JobDao {
    
    @androidx.room.Query(value = "SELECT * FROM jobs ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<rs.elgor.technician.data.local.JobEntity>> getAllJobs();
    
    @androidx.room.Query(value = "SELECT * FROM jobs WHERE id = :jobId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<rs.elgor.technician.data.local.JobEntity> getJobById(int jobId);
    
    @androidx.room.Query(value = "SELECT * FROM job_notes WHERE jobId = :jobId ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<rs.elgor.technician.data.local.JobNoteEntity>> getNotesForJob(int jobId);
    
    @androidx.room.Query(value = "SELECT * FROM job_photos WHERE jobId = :jobId ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<rs.elgor.technician.data.local.JobPhotoEntity>> getPhotosForJob(int jobId);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertJobs(@org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.data.local.JobEntity> jobs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertJob(@org.jetbrains.annotations.NotNull()
    rs.elgor.technician.data.local.JobEntity job, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertNotes(@org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.data.local.JobNoteEntity> notes, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPhotos(@org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.data.local.JobPhotoEntity> photos, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM jobs")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllJobs(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM job_notes WHERE jobId = :jobId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteNotesForJob(int jobId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM job_photos WHERE jobId = :jobId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deletePhotosForJob(int jobId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object replaceJobs(@org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.data.local.JobEntity> jobs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object replaceJobs(@org.jetbrains.annotations.NotNull()
        rs.elgor.technician.data.local.JobDao $this, @org.jetbrains.annotations.NotNull()
        java.util.List<rs.elgor.technician.data.local.JobEntity> jobs, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
    }
}