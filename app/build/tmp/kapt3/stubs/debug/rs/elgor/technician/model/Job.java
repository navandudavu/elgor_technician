package rs.elgor.technician.model;

@com.squareup.moshi.JsonClass(generateAdapter = true)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b=\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u00a3\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0001\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0001\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0001\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\u000b\u001a\u00020\t\u0012\u0006\u0010\f\u001a\u00020\t\u0012\n\b\u0001\u0010\r\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u000e\u001a\u0004\u0018\u00010\t\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\b\b\u0001\u0010\u0012\u001a\u00020\t\u0012\b\b\u0001\u0010\u0013\u001a\u00020\t\u0012\n\b\u0001\u0010\u0014\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u0015\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u0016\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u0017\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u0018\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u0019\u001a\u0004\u0018\u00010\t\u0012\n\b\u0001\u0010\u001a\u001a\u0004\u0018\u00010\t\u0012\u000e\b\u0002\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c\u0012\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001c\u0012\u000e\b\u0002\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u001c\u00a2\u0006\u0002\u0010\"J\t\u0010C\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010D\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010E\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010F\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u0010\u0010G\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u00109J\t\u0010H\u001a\u00020\tH\u00c6\u0003J\t\u0010I\u001a\u00020\tH\u00c6\u0003J\u000b\u0010J\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010K\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010L\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010M\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010N\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010O\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010P\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010Q\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000f\u0010R\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cH\u00c6\u0003J\u000f\u0010S\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001cH\u00c6\u0003J\u000f\u0010T\u001a\b\u0012\u0004\u0012\u00020!0\u001cH\u00c6\u0003J\u0010\u0010U\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\'J\u0010\u0010V\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\'J\u0010\u0010W\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\'J\t\u0010X\u001a\u00020\tH\u00c6\u0003J\u000b\u0010Y\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010Z\u001a\u00020\tH\u00c6\u0003J\t\u0010[\u001a\u00020\tH\u00c6\u0003J\u00b8\u0002\u0010\\\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0003\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0003\u0010\u0007\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\t2\n\b\u0003\u0010\r\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u000e\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0003\u0010\u0012\u001a\u00020\t2\b\b\u0003\u0010\u0013\u001a\u00020\t2\n\b\u0003\u0010\u0014\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u0015\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u0016\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u0017\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u0018\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u0019\u001a\u0004\u0018\u00010\t2\n\b\u0003\u0010\u001a\u001a\u0004\u0018\u00010\t2\u000e\b\u0002\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001c2\u000e\b\u0002\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u001cH\u00c6\u0001\u00a2\u0006\u0002\u0010]J\u0013\u0010^\u001a\u00020_2\b\u0010`\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010a\u001a\u00020\u0003H\u00d6\u0001J\t\u0010b\u001a\u00020\tH\u00d6\u0001R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0013\u0010\u0017\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010$R\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010(\u001a\u0004\b&\u0010\'R\u0013\u0010\u0018\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010$R\u0013\u0010\u0019\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010$R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010$R\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010(\u001a\u0004\b,\u0010\'R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010$R\u0011\u0010\u0012\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010$R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010(\u001a\u0004\b/\u0010\'R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010$R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010$R\u0013\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u0010$R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010$R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001c\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u0010:\u001a\u0004\b8\u00109R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u00101R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u00107R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u001c\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u00107R\u0011\u0010\f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u0010$R\u0013\u0010\r\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010$R\u0011\u0010\u000b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b@\u0010$R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u0010$R\u0011\u0010\u0013\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u0010$\u00a8\u0006c"}, d2 = {"Lrs/elgor/technician/model/Job;", "", "id", "", "customerId", "applianceId", "assignedTo", "createdBy", "title", "", "description", "status", "priority", "scheduledAt", "finishedAt", "address", "hoursLogged", "", "createdAt", "updatedAt", "customerName", "customerPhone", "applianceType", "applianceBrand", "applianceModel", "applianceSerial", "assignedToName", "notes", "", "Lrs/elgor/technician/model/JobNote;", "history", "Lrs/elgor/technician/model/JobStatusHistoryEntry;", "photos", "Lrs/elgor/technician/model/JobPhoto;", "(IILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "getAddress", "()Ljava/lang/String;", "getApplianceBrand", "getApplianceId", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getApplianceModel", "getApplianceSerial", "getApplianceType", "getAssignedTo", "getAssignedToName", "getCreatedAt", "getCreatedBy", "getCustomerId", "()I", "getCustomerName", "getCustomerPhone", "getDescription", "getFinishedAt", "getHistory", "()Ljava/util/List;", "getHoursLogged", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getId", "getNotes", "getPhotos", "getPriority", "getScheduledAt", "getStatus", "getTitle", "getUpdatedAt", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(IILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;)Lrs/elgor/technician/model/Job;", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class Job {
    private final int id = 0;
    private final int customerId = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer applianceId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer assignedTo = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer createdBy = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String description = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String status = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String priority = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String scheduledAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String finishedAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String address = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double hoursLogged = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String createdAt = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String updatedAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String customerName = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String customerPhone = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String applianceType = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String applianceBrand = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String applianceModel = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String applianceSerial = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String assignedToName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<rs.elgor.technician.model.JobNote> notes = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<rs.elgor.technician.model.JobStatusHistoryEntry> history = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<rs.elgor.technician.model.JobPhoto> photos = null;
    
    public Job(int id, @com.squareup.moshi.Json(name = "customer_id")
    int customerId, @com.squareup.moshi.Json(name = "appliance_id")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer applianceId, @com.squareup.moshi.Json(name = "assigned_to")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer assignedTo, @com.squareup.moshi.Json(name = "created_by")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer createdBy, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    java.lang.String priority, @com.squareup.moshi.Json(name = "scheduled_at")
    @org.jetbrains.annotations.Nullable()
    java.lang.String scheduledAt, @com.squareup.moshi.Json(name = "finished_at")
    @org.jetbrains.annotations.Nullable()
    java.lang.String finishedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String address, @com.squareup.moshi.Json(name = "hours_logged")
    @org.jetbrains.annotations.Nullable()
    java.lang.Double hoursLogged, @com.squareup.moshi.Json(name = "created_at")
    @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @com.squareup.moshi.Json(name = "updated_at")
    @org.jetbrains.annotations.NotNull()
    java.lang.String updatedAt, @com.squareup.moshi.Json(name = "customer_name")
    @org.jetbrains.annotations.Nullable()
    java.lang.String customerName, @com.squareup.moshi.Json(name = "customer_phone")
    @org.jetbrains.annotations.Nullable()
    java.lang.String customerPhone, @com.squareup.moshi.Json(name = "appliance_type")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceType, @com.squareup.moshi.Json(name = "appliance_brand")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceBrand, @com.squareup.moshi.Json(name = "appliance_model")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceModel, @com.squareup.moshi.Json(name = "appliance_serial")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceSerial, @com.squareup.moshi.Json(name = "assigned_to_name")
    @org.jetbrains.annotations.Nullable()
    java.lang.String assignedToName, @org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.model.JobNote> notes, @org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.model.JobStatusHistoryEntry> history, @org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.model.JobPhoto> photos) {
        super();
    }
    
    public final int getId() {
        return 0;
    }
    
    public final int getCustomerId() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getApplianceId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getAssignedTo() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getCreatedBy() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDescription() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPriority() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getScheduledAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFinishedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getHoursLogged() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUpdatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCustomerName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCustomerPhone() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApplianceType() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApplianceBrand() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApplianceModel() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApplianceSerial() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAssignedToName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<rs.elgor.technician.model.JobNote> getNotes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<rs.elgor.technician.model.JobStatusHistoryEntry> getHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<rs.elgor.technician.model.JobPhoto> getPhotos() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component17() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component18() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component19() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component20() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component21() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component22() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<rs.elgor.technician.model.JobNote> component23() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<rs.elgor.technician.model.JobStatusHistoryEntry> component24() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<rs.elgor.technician.model.JobPhoto> component25() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component4() {
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
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final rs.elgor.technician.model.Job copy(int id, @com.squareup.moshi.Json(name = "customer_id")
    int customerId, @com.squareup.moshi.Json(name = "appliance_id")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer applianceId, @com.squareup.moshi.Json(name = "assigned_to")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer assignedTo, @com.squareup.moshi.Json(name = "created_by")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer createdBy, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    java.lang.String priority, @com.squareup.moshi.Json(name = "scheduled_at")
    @org.jetbrains.annotations.Nullable()
    java.lang.String scheduledAt, @com.squareup.moshi.Json(name = "finished_at")
    @org.jetbrains.annotations.Nullable()
    java.lang.String finishedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String address, @com.squareup.moshi.Json(name = "hours_logged")
    @org.jetbrains.annotations.Nullable()
    java.lang.Double hoursLogged, @com.squareup.moshi.Json(name = "created_at")
    @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @com.squareup.moshi.Json(name = "updated_at")
    @org.jetbrains.annotations.NotNull()
    java.lang.String updatedAt, @com.squareup.moshi.Json(name = "customer_name")
    @org.jetbrains.annotations.Nullable()
    java.lang.String customerName, @com.squareup.moshi.Json(name = "customer_phone")
    @org.jetbrains.annotations.Nullable()
    java.lang.String customerPhone, @com.squareup.moshi.Json(name = "appliance_type")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceType, @com.squareup.moshi.Json(name = "appliance_brand")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceBrand, @com.squareup.moshi.Json(name = "appliance_model")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceModel, @com.squareup.moshi.Json(name = "appliance_serial")
    @org.jetbrains.annotations.Nullable()
    java.lang.String applianceSerial, @com.squareup.moshi.Json(name = "assigned_to_name")
    @org.jetbrains.annotations.Nullable()
    java.lang.String assignedToName, @org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.model.JobNote> notes, @org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.model.JobStatusHistoryEntry> history, @org.jetbrains.annotations.NotNull()
    java.util.List<rs.elgor.technician.model.JobPhoto> photos) {
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