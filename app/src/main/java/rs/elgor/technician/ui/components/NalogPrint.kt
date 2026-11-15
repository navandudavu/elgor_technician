package rs.elgor.technician.ui.components

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import rs.elgor.technician.model.Nalog

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
 * Not a fiscal receipt.
 */
fun printNalog(context: Context, n: Nalog, revers: Boolean) {
    val c = n.customer
    val a = n.appliance
    val title = if (revers) "REVERS" else "RADNI NALOG"
    val dates = "Datum: ${formatInstantDate(n.createdAt)}" +
        (n.completedAt?.let { " · Završeno: ${formatInstantDate(it)}" } ?: "")

    val body = if (revers) {
        """<div class="box" style="text-align:center;font-size:18px;font-weight:bold;background:#FFF100">APARAT PREUZET U SERVIS</div>
           <div class="mb">Uređaj je preuzet u servis dana ${formatInstantDate(n.createdAt)}.</div>"""
    } else {
        val work = if (n.workDone.isNullOrBlank()) "" else
            """<div class="box mb"><h2>Izvršeni radovi</h2><div class="pre">${h(n.workDone)}</div></div>"""
        val parts = if (n.partLines.isEmpty()) "" else
            """<table><thead><tr><th>Ugrađeni delovi</th><th class="n">Kol.</th></tr></thead><tbody>""" +
                n.partLines.joinToString("") { l ->
                    "<tr><td>${h(l.name)} <span class=\"muted\">(${h(l.sku)})</span></td><td class=\"n\">${l.quantity}</td></tr>"
                } + "</tbody></table>"
        """$work$parts
           <table class="totals"><tbody><tr class="grand"><td>CENA</td><td class="n">${h(formatMoney(n.total))} RSD</td></tr></tbody></table>
           <div>Način plaćanja: <b>${h(paymentLabel(n.paymentType))}</b></div>
           <div class="ack">Potpisom korisnik potvrđuje da su navedeni radovi izvršeni, da je uređaj preuzet i da je upoznat sa iznosom.</div>
           <div class="sign"><div>Serviser</div><div>Korisnik (potpis)</div></div>"""
    }

    val html = """
        <html><head><meta charset="utf-8"><style>
        @page { margin: 14mm; }
        body { font-family: sans-serif; font-size: 12px; color: #000; }
        h1 { font-size: 20px; margin: 0; }
        h2 { font-size: 11px; text-transform: uppercase; margin: 0 0 4px; }
        .bar { border-bottom: 2px solid #000; padding-bottom: 8px; margin-bottom: 16px; display: flex; justify-content: space-between; }
        .muted { color: #5C6B75; }
        .grid { display: flex; gap: 12px; margin-bottom: 12px; }
        .grid .box { flex: 1; }
        .box { border: 1px solid #000; padding: 8px 10px; }
        .mb { margin-bottom: 12px; }
        .pre { white-space: pre-wrap; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 10px; }
        th { text-align: left; padding: 5px 8px; font-size: 11px; border-top: 2px solid #000; border-bottom: 2px solid #000; }
        td { border-bottom: 1px solid #ccc; padding: 5px 8px; }
        .n { text-align: right; white-space: nowrap; }
        .totals { width: 55%; margin-left: auto; }
        .totals td { border: 0; }
        .grand td { border-top: 2px solid #000; border-bottom: 2px solid #000; font-weight: bold; font-size: 14px; background: #FFF100; }
        .ack { margin-top: 24px; font-size: 11px; }
        .sign { display: flex; justify-content: space-between; margin-top: 60px; text-align: center; }
        .sign div { width: 42%; border-top: 1px solid #000; padding-top: 4px; }
        footer { margin-top: 30px; font-size: 10px; color: #5C6B75; }
        </style></head><body>
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
        <div class="box mb"><h2>Opis kvara / dijagnoza</h2><div class="pre">${h(n.diagnosis)}</div></div>
        $body
        <footer>
          ${if (PrintCompany.WARRANTY.isBlank()) "" else "<div>${h(PrintCompany.WARRANTY)}</div>"}
          ${if (revers) "Ovaj dokument je revers o preuzimanju uređaja u servis." else "Ovaj dokument je radni nalog i nije fiskalni račun."}
        </footer>
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
                PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
            )
            nalogPrintWebView = null
        }
    }
    nalogPrintWebView = webView
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}
