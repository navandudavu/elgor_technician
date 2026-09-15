package rs.elgor.technician;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0010\u001a\u0004\u0018\u00010\bJ\b\u0010\u0011\u001a\u00020\u0012H\u0016R\u001e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u0004@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\"\u0010\t\u001a\u0004\u0018\u00010\b2\b\u0010\u0003\u001a\u0004\u0018\u00010\b@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001e\u0010\r\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0013"}, d2 = {"Lrs/elgor/technician/ElgorApplication;", "Landroid/app/Application;", "()V", "<set-?>", "Lrs/elgor/technician/data/local/AppDatabase;", "database", "getDatabase", "()Lrs/elgor/technician/data/local/AppDatabase;", "Lrs/elgor/technician/data/JobRepository;", "repository", "getRepository", "()Lrs/elgor/technician/data/JobRepository;", "Lrs/elgor/technician/data/SessionManager;", "sessionManager", "getSessionManager", "()Lrs/elgor/technician/data/SessionManager;", "getOrCreateRepository", "onCreate", "", "app_debug"})
public final class ElgorApplication extends android.app.Application {
    private rs.elgor.technician.data.SessionManager sessionManager;
    private rs.elgor.technician.data.local.AppDatabase database;
    @org.jetbrains.annotations.Nullable()
    private rs.elgor.technician.data.JobRepository repository;
    
    public ElgorApplication() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final rs.elgor.technician.data.SessionManager getSessionManager() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final rs.elgor.technician.data.local.AppDatabase getDatabase() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final rs.elgor.technician.data.JobRepository getRepository() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final rs.elgor.technician.data.JobRepository getOrCreateRepository() {
        return null;
    }
}