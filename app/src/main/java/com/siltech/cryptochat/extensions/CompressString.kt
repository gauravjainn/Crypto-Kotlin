package com.siltech.cryptochat.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.itextpdf.text.DocumentException
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.parser.PdfImageObject
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.logging.Logger
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater


object CompressString {
    private val LOG: Logger = Logger.getLogger(CompressString::class.java.toString())
    @Throws(IOException::class)

    fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        val outputStream = ByteArrayOutputStream(data.size)
        deflater.finish()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer) // returns the generated code... index
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        val output = outputStream.toByteArray()
//        LOG.debug("Original: " + data.size / 1024 + " Kb")
//        LOG.debug("Compressed: " + output.size / 1024 + " Kb")
        return output
    }

    @Throws(IOException::class, DataFormatException::class)
    fun decompress(data: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)
        val outputStream = ByteArrayOutputStream(data.size)
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        val output = outputStream.toByteArray()
//        LOG.debug("Original: " + data.size)
//        LOG.debug("Compressed: " + output.size)
        return output
    }
}


class ResizeImage {
    /**
     * Manipulates a PDF file src with the file dest as result
     * @param src the original PDF
     * @param dest the resulting PDF
     * @throws IOException
     * @throws DocumentException
     */
//    @Throws(IOException::class, DocumentException::class)
//    fun manipulatePdf(src: String?, dest: String?) {
//        val key = PdfName("ITXT_SpecialId")
//        val value = PdfName("123456789")
//        // Read the file
//        val reader = PdfReader(src)
//        val n = reader.xrefSize
//        var `object`: PdfObject?
//        var stream: PRStream
//        // Look for image and manipulate image stream
//        for (i in 0 until n) {
//            `object` = reader.getPdfObject(i)
//            if (`object` == null || !`object`.isStream) continue
//            stream = `object` as PRStream
//            // if (value.equals(stream.get(key))) {
//            val pdfsubtype = stream[PdfName.SUBTYPE]
//            println(stream.type())
//            if (pdfsubtype != null && pdfsubtype.toString() == PdfName.IMAGE.toString()) {
//                val image = PdfImageObject(stream)
//                val bi: Bitmap = BitmapFactory.decodeFile(image.toString()) ?: continue
//                val width = (bi.getWidth() * FACTOR) as Int
//                val height = (bi.getHeight() * FACTOR) as Int
//                val img = Bitmap(width, height, BufferedImage.TYPE_INT_RGB)
//                val at: java.awt.geom.AffineTransform =
//                    java.awt.geom.AffineTransform.getScaleInstance(
//                        FACTOR.toDouble(), FACTOR.toDouble()
//                    )
//                val g: Graphics2D = img.createGraphics()
//                g.drawRenderedImage(bi, at)
//                val imgBytes = ByteArrayOutputStream()
//                ImageIO.write(img, "JPG", imgBytes)
//                stream.clear()
//                stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION)
//                stream.put(PdfName.TYPE, PdfName.XOBJECT)
//                stream.put(PdfName.SUBTYPE, PdfName.IMAGE)
//                stream.put(key, value)
//                stream.put(PdfName.FILTER, PdfName.DCTDECODE)
//                stream.put(PdfName.WIDTH, PdfNumber(width))
//                stream.put(PdfName.HEIGHT, PdfNumber(height))
//                stream.put(PdfName.BITSPERCOMPONENT, PdfNumber(8))
//                stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB)
//            }
//        }
//        // Save altered PDF
//        val stamper = PdfStamper(reader, FileOutputStream(dest))
//        stamper.close()
//        reader.close()
//    }
//
//    companion object {
//        /** The resulting PDF file.  */ //public static String RESULT = "results/part4/chapter16/resized_image.pdf";
//        /** The multiplication factor for the image.  */
//        var FACTOR = 0.5f
//
//        /**
//         * Main method.
//         *
//         * @param    args    no arguments needed
//         * @throws DocumentException
//         * @throws IOException
//         */
//        @Throws(IOException::class, DocumentException::class)
//        @JvmStatic
//        fun main(args: Array<String>) {
//            //createPdf(RESULT);
//            ResizeImage().manipulatePdf(
//                "C:/_dev_env_/TEMP/compressPDF/TRPT_135002_1470_20131212_121423.PDF",
//                "C:/_dev_env_/TEMP/compressPDF/compressTest.pdf"
//            )
//        }
//    }
}