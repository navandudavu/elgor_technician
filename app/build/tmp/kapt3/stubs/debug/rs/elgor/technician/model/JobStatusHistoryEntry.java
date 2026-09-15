package rs.elgor.technician.model;

@com.squareup.moshi.JsonClass(generateAdapter = true)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0001\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0006\u0012\n\b\u0001\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0001\u0010\t\u001a\u00020\u0006\u0012\n\b\u0001\u0010\n\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0006H\u00c6\u0003J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003JZ\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\b\u0003\u0010\u0007\u001a\u00020\u00062\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\t\u001a\u00020\u00062\n\b\u0003\u0010\n\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001fJ\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001J\t\u0010$\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0015\u0010\b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\rR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\r\u00a8\u0006%"}, d2 = {"Lrs/elgor/technician/model/JobStatusHistoryEntry;", "", "id", "", "jobId", "oldStatus", "", "newStatus", "changedBy", "changedAt", "changedByName", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;)V", "getChangedAt", "()Ljava/lang/String;", "getChangedBy", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getChangedByName", "getId", "()I", "getJobId", "getNewStatus", "getOldStatus", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;)Lrs/elgor/technician/model/JobStatusHistoryEntry;", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class JobStatusHistoryEntry {
    private final int id = 0;
    private final int jobId = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String oldStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String newStatus = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer changedBy = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String changedAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String changedByName = null;
    
    public JobStatusHistoryEntry(int id, @com.squareup.moshi.Json(name = "job_id")
    int jobId, @com.squareup.moshi.Json(name = "old_status")
    @org.jetbrains.annotations.Nullable()
    java.lang.String oldStatus, @com.squareup.moshi.Json(name = "new_status")
    @org.jetbrains.annotations.NotNull()
    java.lang.String newStatus, @com.squareup.moshi.Json(name = "changed_by")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer changedBy, @com.squareup.moshi.Json(name = "changed_at")
    @org.jetbrains.annotations.NotNull()
    java.lang.String changedAt, @com.squareup.moshi.Json(name = "changed_by_name")
    @org.jetbrains.annotations.Nullable()
    java.lang.String changedByName) {
        super();
    }
    
    public final int getId() {
        return 0;
    }
    
    public final int getJobId() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getOldStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNewStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getChangedBy() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getChangedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getChangedByName() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final rs.elgor.technician.model.JobStatusHistoryEntry copy(int id, @com.squareup.moshi.Json(name = "job_id")
    int jobId, @com.squareup.moshi.Json(name = "old_status")
    @org.jetbrains.annotations.Nullable()
    java.lang.String oldStatus, @com.squareup.moshi.Json(name = "new_status")
    @org.jetbrains.annotations.NotNull()
    java.lang.String newStatus, @com.squareup.moshi.Json(name = "changed_by")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer changedBy, @com.squareup.moshi.Json(name = "changed_at")
    @org.jetbrains.annotations.NotNull()
    java.lang.String changedAt, @com.squareup.moshi.Json(name = "changed_by_name")
    @org.jetbrains.annotations.Nullable()
    java.lang.String changedByName) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}