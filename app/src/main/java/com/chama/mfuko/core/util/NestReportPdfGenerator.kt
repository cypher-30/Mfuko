package com.chama.mfuko.core.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import android.net.Uri
import com.chama.mfuko.data.remote.MemberStatusDto
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Renders a one-page-per-overflow text report of a nest's contribution status to PDF,
 * saved under the app cache (`cacheDir/reports/`) so it can be shared via [FileProvider].
 */
object NestReportPdfGenerator {

    private const val PAGE_WIDTH = 595   // A4 at 72dpi
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f
    private const val LINE_HEIGHT = 24f

    fun generate(context: Context, nestName: String, members: List<MemberStatusDto>): Uri {
        val document = PdfDocument()
        val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 12f }
        val headerPaint = Paint().apply { textSize = 12f; isFakeBoldText = true }

        var page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, document.pages.size).create())
        var canvas = page.canvas
        var y = MARGIN

        canvas.drawText(nestName, MARGIN, y, titlePaint)
        y += LINE_HEIGHT
        canvas.drawText(
            "Contribution report — ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}",
            MARGIN, y, bodyPaint
        )
        y += LINE_HEIGHT * 1.5f

        canvas.drawText("Member", MARGIN, y, headerPaint)
        canvas.drawText("Paid", MARGIN + 220, y, headerPaint)
        canvas.drawText("Due", MARGIN + 320, y, headerPaint)
        canvas.drawText("Status", MARGIN + 420, y, headerPaint)
        y += LINE_HEIGHT

        var totalPaid = 0.0
        var totalDue = 0.0

        for (member in members) {
            if (y > PAGE_HEIGHT - MARGIN) {
                document.finishPage(page)
                page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, document.pages.size).create())
                canvas = page.canvas
                y = MARGIN
            }
            canvas.drawText(member.name, MARGIN, y, bodyPaint)
            canvas.drawText("%.2f".format(member.amountPaid), MARGIN + 220, y, bodyPaint)
            canvas.drawText("%.2f".format(member.totalDue), MARGIN + 320, y, bodyPaint)
            canvas.drawText(member.status.replaceFirstChar { it.uppercase() }, MARGIN + 420, y, bodyPaint)
            y += LINE_HEIGHT
            totalPaid += member.amountPaid
            totalDue += member.totalDue
        }

        y += LINE_HEIGHT * 0.5f
        canvas.drawText("Total collected: KES %.2f / KES %.2f".format(totalPaid, totalDue), MARGIN, y, headerPaint)

        document.finishPage(page)

        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(reportsDir, "nest_report_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
