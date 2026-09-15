package rs.elgor.technician.data.remote;

/**
 * Konfiguracija mrežnih parametara za razvojno okruženje.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lrs/elgor/technician/data/remote/NetworkConfig;", "", "()V", "BACKEND_PORT", "", "DEVELOPMENT_PC_IP", "", "getDefaultBaseUrl", "app_release"})
public final class NetworkConfig {
    
    /**
     * IP adresa vašeg računara na lokalnoj mreži.
     * Zamenite "192.168.1.50" vašom stvarnom IPv4 adresom (npr. iz komande ipconfig).
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEVELOPMENT_PC_IP = "192.168.1.30";
    
    /**
     * Port na kojem sluša Express backend.
     */
    public static final int BACKEND_PORT = 4000;
    @org.jetbrains.annotations.NotNull()
    public static final rs.elgor.technician.data.remote.NetworkConfig INSTANCE = null;
    
    private NetworkConfig() {
        super();
    }
    
    /**
     * Pomoćna metoda za kreiranje osnovnog URL-a.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDefaultBaseUrl() {
        return null;
    }
}