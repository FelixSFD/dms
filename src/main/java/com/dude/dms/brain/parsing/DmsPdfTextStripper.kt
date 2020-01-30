package com.dude.dms.brain.parsing

import com.dude.dms.backend.data.docs.TextBlock
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.math.min

@Component
class DmsPdfTextStripper : PDFTextStripper() {

    private var textBlockListOut = mutableListOf<TextBlock>()

    fun getTextWithPositions(doc: PDDocument, textBlockListOut: MutableList<TextBlock>): String {
        this.textBlockListOut = textBlockListOut
        return getText(doc)
    }

    override fun writeString(text: String?, textPositions: List<TextPosition>) {
        createTextBlockEntity(text, textPositions)
        super.writeString(text, textPositions)
    }

    private fun createTextBlockEntity(text: String?, textPositions: List<TextPosition>) {
        if (!text.isNullOrEmpty() && textPositions.isNotEmpty()) {
            var xMin = Float.MAX_VALUE
            var yMin = Float.MAX_VALUE
            var xMax = Float.MIN_VALUE
            var yMax = Float.MIN_VALUE
            val pageWidth = textPositions[0].pageWidth
            val pageHeight = textPositions[0].pageHeight
            val fontSize = textPositions[0].fontSizeInPt
            for (textPosition in textPositions) {
                xMin = min(xMin, textPosition.xDirAdj)
                yMin = min(yMin, textPosition.yDirAdj)
                xMax = max(xMax, textPosition.xDirAdj + textPosition.widthDirAdj)
                yMax = max(yMax, textPosition.yDirAdj + textPosition.heightDir)
            }
            val width = (xMax - xMin) / pageWidth * 100.0f
            val height = (yMax - yMin) / pageHeight * 100.0f
            val x = xMin / pageWidth * 100.0f
            val y = yMin / pageHeight * 100.0f - height * 2.0f
            textBlockListOut.add(TextBlock(null, text, x, y, width, height * 2.0f, fontSize, pageWidth, pageHeight))
        }
    }
}