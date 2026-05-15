package com.example.smart_energy_optimizer

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun exportUsagePdf(context: Context, vm: EnergyViewModel) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        val titlePaint = Paint()

        var y = 60f
        val x = 50f

        // ── Header ────────────────────────────────────────────────────────────
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 24f
        titlePaint.color = Color.BLACK
        canvas.drawText("Smart Energy Optimizer Report", x, y, titlePaint)
        
        y += 40f
        paint.textSize = 12f
        paint.color = Color.GRAY
        canvas.drawText("Generated on: ${java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}", x, y, paint)

        // ── Summary Section ───────────────────────────────────────────────────
        y += 50f
        titlePaint.textSize = 18f
        titlePaint.color = Color.rgb(139, 92, 246) // Purple color from theme
        canvas.drawText("Usage Summary", x, y, titlePaint)

        y += 30f
        paint.color = Color.BLACK
        paint.textSize = 14f
        canvas.drawText("Daily Consumption: ${String.format("%.2f", vm.getDailyKWh())} kWh", x, y, paint)
        y += 20f
        canvas.drawText("Estimated Monthly Cost: INR ${vm.getMonthlyCost().toInt()}", x, y, paint)
        y += 20f
        canvas.drawText("Eco Mode: ${if (vm.ecoMode) "Enabled" else "Disabled"}", x, y, paint)

        // ── Devices Table ─────────────────────────────────────────────────────
        y += 50f
        titlePaint.textSize = 16f
        canvas.drawText("Connected Appliances", x, y, titlePaint)

        y += 30f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Device", x, y, paint)
        canvas.drawText("Power", x + 180f, y, paint)
        canvas.drawText("Usage", x + 280f, y, paint)
        canvas.drawText("Cost/Day", x + 380f, y, paint)

        y += 10f
        canvas.drawLine(x, y, 545f, y, paint)
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        vm.devices.forEach { device ->
            y += 25f
            if (y > 780f) return@forEach // Simple page overflow check
            
            val kwh = (device.powerRating * device.usageHours) / 1000.0
            val cost = kwh * COST_PER_KWH
            
            canvas.drawText(device.name, x, y, paint)
            canvas.drawText("${device.powerRating.toInt()}W", x + 180f, y, paint)
            canvas.drawText("${device.usageHours.toInt()}h", x + 280f, y, paint)
            canvas.drawText("INR ${String.format("%.2f", cost)}", x + 380f, y, paint)
        }

        // ── AI Insights ───────────────────────────────────────────────────────
        if (vm.insights.isNotEmpty()) {
            y += 60f
            if (y < 750f) {
                titlePaint.textSize = 16f
                canvas.drawText("AI Energy Insights", x, y, titlePaint)
                y += 25f
                paint.textSize = 12f
                vm.insights.forEach { insight ->
                    if (y > 800f) return@forEach
                    canvas.drawText("• $insight", x, y, paint)
                    y += 20f
                }
            }
        }

        pdfDocument.finishPage(page)

        // ── Save and Share ────────────────────────────────────────────────────
        val file = File(context.cacheDir, "Energy_Usage_Report.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Download Usage Report"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}