package rs.elgor.technician.ui.components

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import rs.elgor.technician.model.Nalog
import rs.elgor.technician.model.NoteView
import rs.elgor.technician.model.MaterialLine
import android.util.Base64

// Company details printed in the header. Keep in sync with frontend/src/print/company.ts on the web.
// TODO(uncle): replace with ELGOR's real legal details before printing anything for customers.
object PrintCompany {
    const val NAME = "ELGOR"
    const val ADDRESS = "Kruševac"
    const val PHONE = "+381 ..."
    const val EMAIL = "office@elgor.rs"
    const val PIB = "000000000"
    const val MB = "00000000"
    const val BANK_ACCOUNT = "000-0000000000000-00"
    const val WARRANTY = ""
}

// Kept so the WebView is not garbage collected before the print dialog has taken its content.
private var nalogPrintWebView: WebView? = null

private fun h(s: String?) = (s ?: "").replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

/**
 * Radni nalog or revers as an A4 document through the system print dialog (any installed printer service, or
 * "Save as PDF"). Same content as the web page. DISPLAY ONLY: every amount is the string the server sent.
 * Not a fiscal receipt. A nalog is always printed twice, one page each (technician's copy, customer's copy);
 * the revers stays a single page.
 */
fun printNalog(context: Context, n: Nalog, notes: List<NoteView>, revers: Boolean, material: List<MaterialLine> = emptyList()) {
    val c = n.customer
    val a = n.appliance
    val title = if (revers) "REVERS" else "RADNI NALOG"
    val dates = "Datum: ${formatInstantDate(n.createdAt)}" +
        (n.completedAt?.let { " · Završeno: ${formatInstantDate(it)}" } ?: "")

    // Company stamp (app/src/main/assets/elgor-stamp.png) at the bottom of every page; the page still prints without it.
    val stamp = try {
        val bytes = context.assets.open("elgor-stamp.png").use { it.readBytes() }
        "<div class=\"stamp\"><img src=\"data:image/png;base64,${Base64.encodeToString(bytes, Base64.NO_WRAP)}\"></div>"
    } catch (e: Exception) { "" }

    val body = if (revers) {
        """<div class="box" style="margin-bottom:14px;text-align:center;font-size:18px;font-weight:bold;background:#FFF100">APARAT PREUZET U SERVIS</div>
           <div style="margin-bottom:14px">Uređaj je preuzet u servis dana ${formatInstantDate(n.createdAt)}.</div>"""
    } else {
        val konst = if (notes.isEmpty()) "" else
            """<div class="box" style="margin-bottom:14px"><h2>Konstatacija / izvršeni radovi</h2>""" +
                notes.joinToString("") { "<div style=\"white-space:pre-wrap\">${h(it.body)}</div>" } + "</div>"
        val mat = if (material.isEmpty()) "" else
            """<div class="box" style="margin-bottom:14px"><h2>Materijal</h2>""" +
                material.joinToString("") { m ->
                    "<div>${h(m.name)}${if (m.sku.isNullOrBlank()) "" else " (" + h(m.sku) + ")"} — ${h(m.quantity)} ${h(m.unit ?: "")}</div>"
                } + "</div>"
        val cancelled = if (n.status != "CANCELLED") "" else
            """<div class="box" style="margin-bottom:14px"><h2>Razlog otkazivanja</h2><div style="white-space:pre-wrap">${h(n.cancelReason ?: "—")}</div></div>"""
        """$konst$mat$cancelled
           <table class="totals"><tbody><tr class="grand"><td>CENA</td><td class="num">${h(formatMoney(n.total))} RSD</td></tr></tbody></table>
           <div>Način plaćanja: <b>${h(paymentLabel(n.paymentType))}</b></div>
           <div class="ack">Potpisom korisnik potvrđuje da su navedeni radovi izvršeni, da je uređaj preuzet i da je upoznat sa iznosom.</div>
           <div class="sign"><div>Serviser</div><div>Korisnik (potpis)</div></div>"""
    }

    fun sheet(copy: String?, pageBreak: Boolean) = """
        <div class="nalog-print${if (pageBreak) " sheet-break" else ""}">
        ${if (copy == null) "" else "<div class=\"copy\">${h(copy)}</div>"}
        <div class="bar">
          <div>
            <h1>${h(PrintCompany.NAME)}</h1>
            <div class="muted">${h(PrintCompany.ADDRESS)} · ${h(PrintCompany.PHONE)} · ${h(PrintCompany.EMAIL)}</div>
            <div class="muted">PIB ${h(PrintCompany.PIB)} · MB ${h(PrintCompany.MB)} · Tekući račun ${h(PrintCompany.BANK_ACCOUNT)}</div>
          </div>
          <div style="text-align:right">
            <div style="font-size:16px;font-weight:bold">$title</div>
            <div>Br. ${h(n.nalogNumber)}</div>
            <div class="muted">$dates</div>
            ${if (revers) "" else "<div class=\"muted\">Status: ${h(nalogStatusLabel(n.status))}</div>"}
          </div>
        </div>
        <div class="grid">
          <div class="box"><h2>Korisnik</h2><b>${h(c.fullName)}</b><br>
            ${h(listOfNotNull(c.address, c.city).filter { it.isNotBlank() }.joinToString(", "))}<br>
            Tel: ${h(c.phone)}${if (c.email.isNullOrBlank()) "" else " · " + h(c.email)}
            ${if (c.locationNotes.isNullOrBlank()) "" else "<br><span class=\"muted\">${h(c.locationNotes)}</span>"}
          </div>
          <div class="box"><h2>Uređaj</h2>
            <b>${h(applianceLabel(a.type))}</b> — ${h(a.brand)}${if (a.model.isNullOrBlank()) "" else " " + h(a.model)}<br>
            Serijski broj: ${h(a.serialNumber ?: "—")}<br>
            Serviser: ${h(n.assignedToName ?: "—")}
          </div>
        </div>
        <div class="box" style="margin-bottom:14px"><h2>Opis kvara / dijagnoza</h2><div style="white-space:pre-wrap">${h(n.diagnosis)}</div></div>
        $body
        $stamp
        <footer>
          ${if (PrintCompany.WARRANTY.isBlank()) "" else "<div style=\"margin-bottom:4px\">${h(PrintCompany.WARRANTY)}</div>"}
          ${if (revers) "Ovaj dokument je revers o preuzimanju uređaja u servis." else "Ovaj dokument je radni nalog i nije fiskalni račun."}
        </footer>
        </div>
    """

    val sheets = if (revers) sheet(null, false) else sheet("Primerak servisera", true) + sheet("Primerak korisnika", false)

    val html = """
        <html><head><meta charset="utf-8"><style>
        /* Same stylesheet as PRINT_CSS in frontend/src/components/NalogPrint.tsx; the 14mm page margin is the body padding. */
        body { margin: 0; padding: 14mm; }
        .nalog-print { font: 12px/1.45 Arial, Helvetica, sans-serif; color: #000; max-width: 182mm; margin: 0 auto; -webkit-print-color-adjust: exact; print-color-adjust: exact; }
        .nalog-print.sheet-break { break-after: page; page-break-after: always; }
        .nalog-print .copy { text-align: right; font-size: 10px; font-weight: bold; text-transform: uppercase; letter-spacing: .08em; margin-bottom: 4px; }
        .nalog-print h1 { font-size: 20px; margin: 0; }
        .nalog-print .bar { border-bottom: 2px solid #000; box-shadow: 0 4px 0 #FFF100; padding-bottom: 8px; margin-bottom: 18px; display: flex; justify-content: space-between; align-items: flex-end; }
        .nalog-print .muted { color: #5C6B75; }
        .nalog-print .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 14px; }
        .nalog-print .box { border: 1px solid #000; padding: 8px 10px; }
        .nalog-print .box h2 { font-size: 11px; text-transform: uppercase; letter-spacing: .06em; margin: 0 0 4px; }
        .nalog-print table { width: 100%; border-collapse: collapse; margin-bottom: 10px; }
        .nalog-print th { color: #000; text-align: left; padding: 5px 8px; font-size: 11px; border-top: 2px solid #000; border-bottom: 2px solid #000; }
        .nalog-print td { border-bottom: 1px solid #ccc; padding: 5px 8px; }
        .nalog-print .num { text-align: right; white-space: nowrap; }
        .nalog-print .totals { margin-left: auto; width: 62mm; }
        .nalog-print .totals td { border: 0; padding: 2px 8px; }
        .nalog-print .totals .grand td { border-top: 2px solid #000; border-bottom: 2px solid #000; font-weight: bold; font-size: 14px; background: #FFF100; }
        .nalog-print thead { display: table-header-group; }
        .nalog-print tr, .nalog-print .box, .nalog-print .sign, .nalog-print .totals { break-inside: avoid; page-break-inside: avoid; }
        .nalog-print .ack { margin-top: 10mm; font-size: 11px; }
        .nalog-print .sign { display: grid; grid-template-columns: 1fr 1fr; gap: 30mm; margin-top: 22mm; text-align: center; }
        .nalog-print .sign div { border-top: 1px solid #000; padding-top: 4px; }
        .nalog-print .stamp { text-align: right; margin-top: 6mm; break-inside: avoid; page-break-inside: avoid; }
        .nalog-print .stamp img { height: 28mm; width: auto; }
        .nalog-print footer { margin-top: 12mm; font-size: 10px; color: #5C6B75; }
        </style></head><body>
        $sheets
        </body></html>
    """.trimIndent()

    val docName = (if (revers) "Revers " else "Nalog ") + n.nalogNumber
    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            val pm = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            pm.print(
                docName,
                view.createPrintDocumentAdapter(docName),
                PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
            )
            nalogPrintWebView = null
        }
    }
    nalogPrintWebView = webView
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}
