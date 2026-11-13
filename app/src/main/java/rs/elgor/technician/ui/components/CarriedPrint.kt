package rs.elgor.technician.ui.components

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import rs.elgor.technician.model.CarriedItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Kept so the WebView is not garbage collected before the print dialog has taken its content.
private var printWebView: WebView? = null

private fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

/**
 * Zaduzenje as an A4 document through the system print dialog (any installed printer service, or "Save as PDF").
 * Same layout as the web page: technician, date, numbered table and two signature lines.
 */
fun printCarried(context: Context, technicianName: String, items: List<CarriedItem>) {
    val rows = items.mapIndexed { i, c ->
        "<tr><td>${i + 1}</td><td>${esc(c.sku ?: "")}</td><td>${esc(c.name)}</td><td class=\"n\">${c.quantity}</td></tr>"
    }.joinToString("")
    val today = SimpleDateFormat("dd.MM.yyyy.", Locale.US).format(Date())
    val html = """
        <html><head><meta charset="utf-8"><style>
        body { font-family: sans-serif; font-size: 12px; color: #000; }
        h1 { font-size: 18px; margin: 0 0 6px; }
        table { width: 100%; border-collapse: collapse; margin: 14px 0; }
        th, td { border: 1px solid #999; padding: 5px 8px; text-align: left; }
        .n { text-align: right; }
        .sign { display: flex; justify-content: space-between; margin-top: 60px; }
        .sign div { width: 45%; border-top: 1px solid #000; padding-top: 4px; text-align: center; }
        </style></head><body>
        <h1>Zaduženje</h1>
        <div>Serviser: <b>${esc(technicianName)}</b> · Datum: $today</div>
        <table><thead><tr><th>#</th><th>Šifra</th><th>Naziv</th><th class="n">Količina</th></tr></thead>
        <tbody>$rows</tbody></table>
        <div class="sign"><div>Potpis servisera</div><div>Potpis izdavaoca</div></div>
        </body></html>
    """.trimIndent()

    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            val pm = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            pm.print(
                "Zaduzenje",
                view.createPrintDocumentAdapter("Zaduzenje"),
                PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
            )
            printWebView = null
        }
    }
    printWebView = webView
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}
