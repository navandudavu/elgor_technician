package rs.elgor.technician.data.remote

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

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
     * Docker: sve ide preko Caddy-ja na portu 80, pa adresa nema port.
     */
    fun getDefaultBaseUrl(): String = "http://$DEVELOPMENT_PC_IP"

    /** Port of the Java nalog-service in development (behind a reverse proxy both share one address). */
    const val NALOG_PORT = 8081

    /** The old Node backend is gone; a phone that still has :4000 saved is pointed at the Java service. */
    private const val LEGACY_NODE_PORT = 4000

    /**
     * The phone stores ONE server address (the Node backend). In development the Java service lives
     * on the same machine at port 8081, so swap the port; without :4000 in the address nothing changes.
     */
    fun nalogBaseUrl(serverUrl: String): String {
        val url = serverUrl.toHttpUrlOrNull() ?: return serverUrl
        return if (url.port == LEGACY_NODE_PORT) url.newBuilder().port(NALOG_PORT).build().toString() else serverUrl
    }
}
