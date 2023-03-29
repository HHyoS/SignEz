package com.signez.signageproblemshooting.service

import android.graphics.Rect


object PrePostProcessor {
    val NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)
    val NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)

    // model input image size
    const val mInputWidth = 640
    const val mInputHeight = 640

    // model output is of size 25200*(num_of_class+5)
    const val mOutputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    const val mOutputColumn = 6 // left, top, right, bottom, score and class probability
    const val mThreshold = 0.10f // score above which a detection is generated
    const val mNmsLimit = 15

    val signageClass: Array<String> = arrayOf("signage")
    val errorModuleClass: Array<String> = arrayOf("error-module")

    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    /**
     * Removes bounding boxes that overlap too much with other boxes that have
     * a higher score.
     *
     * @param boxes an array of bounding boxes and their scores
     * @param limit the maximum number of boxes that will be selected
     * @param threshold used to decide whether boxes overlap too much
     */
    private fun nonMaxSuppression(boxes: ArrayList<DetectResult>, limit: Int, threshold: Float): ArrayList<DetectResult> {
        // Do an argsort on the confidence scores, from high to low.
        boxes.sortWith(Comparator { o1, o2 -> o1.score.compareTo(o2.score) })

        val selected = ArrayList<DetectResult>()
        val active = BooleanArray(boxes.size) { true }
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

                var j = i + 1
                while (j < boxes.size) {
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
                    j++
                }
            }
            i++
        }
        return selected
    }

    private fun IOU(a: Rect, b: Rect): Float {
        val areaA = (a.right - a.left) * (a.bottom - a.top)
        if (areaA <= 0.0) return 0.0f

        val areaB = (b.right - b.left) * (b.bottom - b.top)
        if (areaB <= 0.0) return 0.0f

        val intersectionMinX = a.left.coerceAtLeast(b.left)
        val intersectionMinY = a.top.coerceAtLeast(b.top)
        val intersectionMaxX = a.right.coerceAtMost(b.right)
        val intersectionMaxY = a.bottom.coerceAtMost(b.bottom)
        val intersectionArea = (maxOf(intersectionMaxY - intersectionMinY, 0) *
                maxOf(intersectionMaxX - intersectionMinX, 0)).toFloat()
        return intersectionArea / (areaA + areaB - intersectionArea)
    }

    fun outputsToNMSPredictions(
        outputs: FloatArray,
        imgScaleX: Float,
        imgScaleY: Float,
        ivScaleX: Float,
        ivScaleY: Float,
        startX: Float,
        startY: Float
    ): ArrayList<DetectResult> {
        val results = ArrayList<DetectResult>()
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

                val rect = Rect(
                    (startX + ivScaleX * left).toInt(),
                    (startY + top * ivScaleY).toInt(),
                    (startX + ivScaleX * right).toInt(),
                    (startY + ivScaleY * bottom).toInt()
                )
                val result = DetectResult(cls, outputs[i * mOutputColumn + 4], rect)
                results.add(result)
            }
        }
        return nonMaxSuppression(results, mNmsLimit, mThreshold)
    }

}