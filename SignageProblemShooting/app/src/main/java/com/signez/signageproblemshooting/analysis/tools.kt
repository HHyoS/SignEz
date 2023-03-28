package com.signez.signageproblemshooting.analysis

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import java.io.InputStream
import kotlin.math.roundToInt


fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
        val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}

fun imageToArray(contentResolver: ContentResolver, photoUri: MutableState<Uri>) {
    val inputStream: InputStream? = contentResolver.openInputStream(photoUri.value)
    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(this, 300, 400)
    }

    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap!!, 300, 400, false)

    // Get pixels from the bitmap
    val pixels = IntArray(scaledBitmap.width * scaledBitmap.height)
    scaledBitmap.getPixels(pixels, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)

    // Convert the 1D pixel array to a 2D array
    val pixelArray2D = Array(scaledBitmap.height) { y ->
        IntArray(scaledBitmap.width) { x ->
            pixels[y * scaledBitmap.width + x]
        }
    }
    val rows = pixelArray2D.size
    val cols = pixelArray2D[0].size
    Log.d("TAG", "size of $rows and $cols")
    for (i in 0 until rows) {
        val rowStr = StringBuilder()
        for (j in 0 until cols) {
            rowStr.append(String.format("%08X", pixelArray2D[i][j]))
            rowStr.append(" ")
        }
        Log.d("TAG", rowStr.toString())
    }
}

fun getFrames(context: Context, videoUri: Uri, resX:Int = 300, resY: Int = 400, fps:Int=10 ): MutableList<Bitmap> {

    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, videoUri)

    val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
    val frameIntervalUs = 1000000L / fps // 기본 10 fps

    val frames = mutableListOf<Bitmap>()
    var timeUs = 0L

    //https://developer.android.com/reference/android/media/MediaMetadataRetriever
    // microseconds (1/1000000th of a second) 단위로 timeUs 인자받음.
    // duration은 ms (1/1000th of a second)라 단위 맞춰주기
    while (timeUs < durationMs * 1000) {
        val frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        val scaledFrame = frame?.let { Bitmap.createScaledBitmap(it, resX, resY, true) }
        if (scaledFrame != null) {
            frames.add(scaledFrame)
        }
        timeUs += frameIntervalUs // 0초, 0 + 1/fps 초 , ...
    }

    retriever.release()
    return frames
}