package com.signez.signageproblemshooting.service


import android.graphics.Rect
import java.io.Serializable
import java.util.*

class Result(var classIndex: Int, var score: Float, var rect: Rect) : Serializable
object PrePostProcessor {
    // for yolov5 model, no need to apply MEAN and STD
    @JvmField
    var NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)

    @JvmField
    var NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)

    // model input image size
    @JvmField
    var mInputWidth = 640

    @JvmField
    var mInputHeight = 640

    // model output is of size 25200*(num_of_class+5)
    private const val mOutputRow =
        25200 // as decided by the YOLOv5 model for input image of size 640*640
    private const val mOutputColumn = 6 // left, top, right, bottom, score and 80 class probability
    private const val mThreshold = 0.7f // score above which a detection is generated
    private const val mNmsLimit = 15
    lateinit var mClasses: Array<String>
    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    /**
     * Removes bounding boxes that overlap too much with other boxes that have
     * a higher score.
     * - Parameters:
     * - boxes: an array of bounding boxes and their scores
     * - limit: the maximum number of boxes that will be selected
     * - threshold: used to decide whether boxes overlap too much
     */
    fun nonMaxSuppression(
        boxes: ArrayList<Result>,
        limit: Int,
        threshold: Float
    ): ArrayList<Result> {

        // Do an argsort on the confidence scores, from high to low.
        Collections.sort(
            boxes
        ) { o1, o2 -> o1.score.compareTo(o2.score) }
        val selected = ArrayList<Result>()
        val active = BooleanArray(boxes.size)
        Arrays.fill(active, true)
        var numActive = active.size

        // The algorithm is simple: Start with the box that has the highest score.
        // Remove any remaining boxes that overlap it more than the given threshold
        // amount. If there are any boxes left (i.e. these did not overlap with any
        // previous boxes), then repeat this procedure, until no more boxes remain
        // or the limit has been reached.
        var done = false
        var i = 0
        while (i < boxes.size && !done) {
            if (active[i]) {
                val boxA = boxes[i]
                selected.add(boxA)
                if (selected.size >= limit) break
                for (j in i + 1 until boxes.size) {
                    if (active[j]) {
                        val boxB = boxes[j]
                        if (IOU(boxA.rect, boxB.rect) > threshold) {
                            active[j] = false
                            numActive -= 1
                            if (numActive <= 0) {
                                done = true
                                break
                            }
                        }
                    }
                }
            }
            i++
        }
        return selected
    }

    /**
     * Computes intersection-over-union overlap between two bounding boxes.
     */
    private fun IOU(a: Rect, b: Rect): Float {
        val areaA = ((a.right - a.left) * (a.bottom - a.top)).toFloat()
        if (areaA <= 0.0) return 0.0f
        val areaB = ((b.right - b.left) * (b.bottom - b.top)).toFloat()
        if (areaB <= 0.0) return 0.0f
        val intersectionMinX = a.left.coerceAtLeast(b.left).toFloat()
        val intersectionMinY = a.top.coerceAtLeast(b.top).toFloat()
        val intersectionMaxX = a.right.coerceAtMost(b.right).toFloat()
        val intersectionMaxY = a.bottom.coerceAtMost(b.bottom).toFloat()
        val intersectionArea = (intersectionMaxY - intersectionMinY).coerceAtLeast(0f) *
                (intersectionMaxX - intersectionMinX).coerceAtLeast(0f)
        return intersectionArea / (areaA + areaB - intersectionArea)
    }

    fun getValue(a: Int): Int {
        return if (a < 0)
            0
        else
            a
    }

    @JvmStatic
    fun outputsToNMSPredictions(
        outputs: FloatArray,
        imgScaleX: Float,
        imgScaleY: Float,
        ivScaleX: Float,
        ivScaleY: Float,
        startX: Float,
        startY: Float
    ): ArrayList<Result> {
        val results = ArrayList<Result>()
        for (i in 0 until mOutputRow) {
            if (outputs[i * mOutputColumn + 4] > mThreshold) {
                val x = outputs[i * mOutputColumn]
                val y = outputs[i * mOutputColumn + 1]
                val w = outputs[i * mOutputColumn + 2]
                val h = outputs[i * mOutputColumn + 3]
                val left = imgScaleX * (x - w / 2)
                val top = imgScaleY * (y - h / 2)
                val right = imgScaleX * (x + w / 2)
                val bottom = imgScaleY * (y + h / 2)
                var max = outputs[i * mOutputColumn + 5]
                var cls = 0
                for (j in 0 until mOutputColumn - 5) {
                    if (outputs[i * mOutputColumn + 5 + j] > max) {
                        max = outputs[i * mOutputColumn + 5 + j]
                        cls = j
                    }
                }
                val centerX = (left + right) / 2
                val centerY = (top + bottom) / 2
                val width = right - left
                val height = bottom - top
                val newWidth = width * 1.0f
                val newHeight = height * 1.0f
                val newLeft = centerX - newWidth / 2
                val newTop = centerY - newHeight / 2
                val newRight = centerX + newWidth / 2
                val newBottom = centerY + newHeight / 2
                val rect = Rect(
                    getValue((startX + ivScaleX * newLeft).toInt()),
                    getValue((startY + ivScaleY * newTop).toInt()),
                    getValue((startX + ivScaleX * newRight).toInt()),
                    getValue((startY + ivScaleY * newBottom).toInt())
                )
                val result = Result(cls, outputs[i * mOutputColumn + 4], rect)
                results.add(result)
            }
        }
        return nonMaxSuppression(results, mNmsLimit, mThreshold)
    }
}