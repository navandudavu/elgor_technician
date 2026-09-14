package rs.elgor.technician.data.remote

/**
 * Konfiguracija mrežnih parametara za razvojno okruženje.
 */
object NetworkConfig {
    /**
     * IP adresa vašeg računara na lokalnoj mreži.
     * Zamenite "192.168.1.50" vašom stvarnom IPv4 adresom (npr. iz komande ipconfig).
     */
    const val DEVELOPMENT_PC_IP = "192.168.1.30"
    
    /**
     * Port na kojem sluša Express backend.
     */
    const val BACKEND_PORT = 4000
    
    /**
     * Pomoćna metoda za kreiranje osnovnog URL-a.
     */
    fun getDefaultBaseUrl(): String = "http://$DEVELOPMENT_PC_IP:$BACKEND_PORT"
}
