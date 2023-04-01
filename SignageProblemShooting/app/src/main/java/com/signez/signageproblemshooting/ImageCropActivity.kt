package com.signez.signageproblemshooting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.signez.signageproblemshooting.service.PrePostProcessor
import com.signez.signageproblemshooting.ui.analysis.ResultGridDestination
import com.signez.signageproblemshooting.ui.analysis.ResultsHistoryDestination
import com.signez.signageproblemshooting.ui.inputs.openImageCropActivity
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.lang.Math.sqrt

class ImageCropActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    // 점 네 개 추가
    var pointTopLeft = Point(0, 0)
    var pointTopRight = Point(300, 0)
    var pointBottomLeft = Point(0, 300)
    var pointBottomRight = Point(300, 300)

    var mModule: Module? = null
    // m : viewFrame
    // mm : 이미지 실제 크기
    var mWidth : Int = 1
    var mHeight : Int = 2
    var mmWidth : Int = 3
    var mmHeight :Int = 5
    lateinit var bitmap : Bitmap
    var mImgScaleX = 0f
    var mImgScaleY = 0f
    var mIvScaleX = 0f
    var mIvScaleY = 0f
    var mStartX = 0f
    var mStartY = 0f
    var rec = Rect(-999, -999, -999, -999)
    lateinit var bitmapp : Bitmap
    var minHeight : Float = 0f
    var maxHeight : Float  = 0f
    var viewWidth : Float  = 0f
    var viewHeight : Float  = 0f
    var imageWidth : Float  = 0f
    var imageHeight  : Float  = 0f
    var scaleX: Float = 0f
    var scaleY : Float = 0f

    private val REQUEST_TYPE: String = "REQUEST_TYPE"
    private val REQUEST_SIGNAGE_ID: String = "REQUEST_SIGNAGE_ID"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        val getVideoThumbnail: (Uri) -> Bitmap? = { uri ->
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, uri)
            retriever.getFrameAtTime()
        }
        val imageUri = Uri.parse(intent.getStringExtra("uri"))
        val contentResolver = contentResolver
        val mimeType = contentResolver.getType(imageUri)

        mWidth = intent.getIntExtra("mWidth", 1)
        mHeight = intent.getIntExtra("mHeight", 2)
        mmWidth = intent.getIntExtra("mmWidth", 3)
        mmHeight = intent.getIntExtra("mmHeight", 5)

        Log.d("mWidth", "$mWidth")
        Log.d("mHeight", "$mHeight")
        Log.d("mmWidth", "$mmWidth")
        Log.d("mmHeight", "$mmHeight")

        // 여기서 머신러닝 돌리기
        mImgScaleX = mmWidth.toFloat() / PrePostProcessor.mInputWidth
        mImgScaleY = mmHeight.toFloat() / PrePostProcessor.mInputHeight
        mIvScaleX = (if (mmWidth > mmHeight) mWidth
            .toFloat() / mmWidth else mHeight
            .toFloat() / mmHeight)
        mIvScaleY = (if (mmHeight > mmWidth) mHeight
            .toFloat() / mmHeight else mWidth
            .toFloat() / mmWidth)
        mStartX = (mWidth - mIvScaleX * mmWidth) / 2
        mStartY = (mHeight - mIvScaleY * mmHeight) / 2

        if (mModule == null) {
            val temp = ErrorDetectActivity()
            mModule = temp.getModel()
        }
        val thread = object : Thread() {
            override fun run() {
                val options = BitmapFactory.Options()
                options.inMutable = true
                bitmapp = if (mimeType?.startsWith("video") == true) {
                    getVideoThumbnail(imageUri)
                } else {
                    BitmapFactory.decodeStream(
                        contentResolver.openInputStream(imageUri),
                        null,
                        options
                    )
                }!!
                val resizedBitmap = bitmapp.let {
                    Bitmap.createScaledBitmap(
                        it,
                        PrePostProcessor.mInputWidth,
                        PrePostProcessor.mInputHeight,
                        true
                    )
                }
                val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                    resizedBitmap,
                    PrePostProcessor.NO_MEAN_RGB,
                    PrePostProcessor.NO_STD_RGB
                )
                val outputTuple =
                    mModule!!.forward(IValue.from(inputTensor)).toTuple()
                val outputTensor = outputTuple[0].toTensor()
                val outputs = outputTensor.dataAsFloatArray
                val results = PrePostProcessor.outputsToNMSPredictions(
                    outputs,
                    mImgScaleX,
                    mImgScaleY,
                    mIvScaleX,
                    mIvScaleY,
                    mStartX,
                    mStartY
                )
                if (results.size != 0) {
                    rec = results[0].rect
                    Log.d("result[0]", "${results[0].rect}")
                    Log.d("rec", "$rec")

                } else {
                    Log.d("what", "????")
                }
            }
        }
        thread.start()
        thread.join()


        imageView = findViewById(R.id.imageView)


        imageView.setImageBitmap(bitmapp)
        val viewTreeObserver = imageView.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // ViewTreeObserver 리스너가 불리면서 ImageView와 Drawable 객체 모두의 크기를 구할 수 있습니다.
                viewWidth = imageView.width.toFloat()
                viewHeight = imageView.height.toFloat()
                imageWidth = bitmapp.width.toFloat()
                imageHeight = bitmapp.height.toFloat()
                val scale = viewWidth / imageWidth

                imageWidth *= scale
                imageHeight *= scale
                Log.d("llog","viewWidth $viewWidth viewHeight = $viewHeight imageWidth $imageWidth imageHeight $imageHeight")
                if (rec.left == -999) {
                    val widthHalf = (imageWidth / 2).toInt()
                    val widthQuarter = widthHalf / 2
                    val heightHalf = (imageHeight / 2).toInt()
                    val heightQuarter = heightHalf / 2
                    pointTopLeft = Point(widthHalf - widthQuarter, heightHalf - heightQuarter)
                    pointTopRight = Point(widthHalf + widthQuarter, heightHalf - heightQuarter)
                    pointBottomLeft = Point(widthHalf - widthQuarter, heightHalf + heightQuarter)
                    pointBottomRight = Point(widthHalf + widthQuarter, heightHalf + heightQuarter)
                } else {
                    pointTopLeft = Point(rec.left, rec.top)
                    pointTopRight = Point(rec.right, rec.top)
                    pointBottomLeft = Point(rec.left, rec.bottom)
                    pointBottomRight = Point(rec.right, rec.bottom)
                }

                // 리스너 제거
                imageView.viewTreeObserver.removeOnPreDrawListener(this)

                return true
            }
        })


        Log.d("ImageScaleType", imageView.scaleType.toString())
//        val imageMatrix = imageView.imageMatrix
//        val matrixValues = FloatArray(9)
//        imageMatrix.getValues(matrixValues)

//        scaleX = matrixValues[Matrix.MSCALE_X]
//        scaleY = matrixValues[Matrix.MSCALE_Y]
//        val translateX = matrixValues[Matrix.MTRANS_X]
//        val translateY = matrixValues[Matrix.MTRANS_Y]

        // ImageView에 대한 OnTouchListener 설정
        imageView.setOnTouchListener { v, event ->
//            val viewWidth = v.width
//            val viewHeight = v.height
//            val imageWidth = imageView.drawable.intrinsicWidth
//            val imageHeight = imageView.drawable.intrinsicHeight

            scaleX = imageWidth / viewWidth
            scaleY = imageHeight / viewHeight

//            val scaleX = viewWidth.toFloat() / imageWidth.toFloat()
//            val scaleY = viewHeight.toFloat() / imageHeight.toFloat()
//            val scaledTouchX = event.x * scaleX
//            val scaledTouchY = event.y * scaleY
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치 다운 이벤트 처리
                    // 가장 가까운 점을 찾아 이동
//                    val touchPoint = Point(scaledTouchX.toInt(), scaledTouchY.toInt())
                    val touchPointF = imageViewToImageCoordinatesFitCenter(imageView, event.x, event.y)
                    val touchPoint = Point(touchPointF.x.toInt(), touchPointF.y.toInt())
                    Log.d("touchPoint","${touchPoint}")
                    Log.d("bottomright", "${pointBottomRight}")

                    val closestPoint = findClosestPoint(
                        touchPoint,
                        pointTopLeft,
                        pointTopRight,
                        pointBottomRight,
                        pointBottomLeft
                    )
                    Log.d("closestPoint1", "${closestPoint}")
                    if (touchPoint.distanceTo(closestPoint) < 50) {
                        closestPoint.x = touchPoint.x
                        closestPoint.y = touchPoint.y
                        imageView.invalidate() // ImageView 다시 그리기
                    }
                    Log.d("closestPoint2", "${closestPoint}")
                    onDrawBitmap(imageView, bitmapp)

                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // 터치 이동 이벤트 처리
                    // 이동된 점 다시 그리기

//                    val touchPoint = Point(scaledTouchX.toInt(), scaledTouchY.toInt())
                    val touchPointF = imageViewToImageCoordinatesFitCenter(imageView, event.x, event.y)
                    val touchPoint = Point(touchPointF.x.toInt(), touchPointF.y.toInt())
                    val closestPoint = findClosestPoint(
                        touchPoint,
                        pointTopLeft,
                        pointTopRight,
                        pointBottomRight,
                        pointBottomLeft
                    )


                    // 시작 위치와 가장 가까운 점 사이의 거리를 계산하여 일정 거리 이내인 경우에만 이동
                    if (touchPoint.distanceTo(closestPoint) < 50) {
                        closestPoint.x = touchPoint.x
                        closestPoint.y = touchPoint.y
                        imageView.invalidate() // ImageView 다시 그리기
                    }

                    onDrawBitmap(imageView, bitmapp)

                    true
                }
                else -> false
            }
        }
        val btn: Button = findViewById(R.id.exit_btn)
        btn.setOnClickListener {
            val REQUEST_CODE_ERROR_DETECT_ACTIVITY = 999
            val intent = Intent(this, ErrorDetectActivity::class.java).apply {
                putExtra("PointTopLeftX", pointTopLeft.x)
                putExtra("PointTopLeftY", pointTopLeft.y)
                putExtra("PointTopRightX", pointTopRight.x)
                putExtra("PointTopRightY", pointTopRight.y)
                putExtra("PointBottomLeftX", pointBottomLeft.x)
                putExtra("PointBottomLeftY", pointBottomLeft.y)
                putExtra("PointBottomRightX", pointBottomRight.x)
                putExtra("PointBottomRightY", pointBottomRight.y)
                putExtra("scaleX", scaleX)
                putExtra("scaleY", scaleY)
                putExtra("REQUEST_CODE", intent.getIntExtra("REQUEST_CODE", 0))
                putExtra("uri", intent.data.toString())
                putExtra(REQUEST_TYPE, intent.getIntExtra(REQUEST_TYPE, -1))
                putExtra(REQUEST_SIGNAGE_ID, intent.getLongExtra(REQUEST_SIGNAGE_ID, -1L))
                setData(intent.data)
            }
            setResult(Activity.RESULT_OK)
            startActivity(intent)
        }
        // ImageView 다시 그리기
//        imageView.viewTreeObserver.addOnDrawListener {
//            onDraw(imageView, imageUri)
//        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun onDraw(imageView: ImageView, uri : Uri) {
        // 사각형 그리기
        val paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE

        val path = Path()
        path.moveTo(pointTopLeft.x.toFloat(), pointTopLeft.y.toFloat())
        path.lineTo(pointTopRight.x.toFloat(), pointTopRight.y.toFloat())
        path.lineTo(pointBottomRight.x.toFloat(), pointBottomRight.y.toFloat())
        path.lineTo(pointBottomLeft.x.toFloat(), pointBottomLeft.y.toFloat())
        path.close()


        imageView.setImageBitmap(null) // 이전 이미지 제거
        bitmap = uriToBitmap(this, uri)
        val imageViewWidth = imageView.width
        val scaleFactor = imageViewWidth.toFloat() / bitmap.width.toFloat()
        //val mutableBitmap = Bitmap.createScaledBitmap(bitmap, imageViewWidth, (bitmap.height * scaleFactor).toInt(), false)
        val mutableBitmap = Bitmap.createScaledBitmap(
            bitmap,
            imageViewWidth,
            (bitmap.height * scaleFactor).toInt(),
            true
        ).copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap!!)
        canvas.drawPath(path, paint)

        // 점 그리기
        paint.color = Color.BLUE
        paint.strokeWidth = 12f
        paint.style = Paint.Style.FILL_AND_STROKE

        canvas.drawPoint(pointTopLeft.x.toFloat(), pointTopLeft.y.toFloat(), paint)
        canvas.drawPoint(pointTopRight.x.toFloat(), pointTopRight.y.toFloat(), paint)
        canvas.drawPoint(pointBottomRight.x.toFloat(), pointBottomRight.y.toFloat(), paint)
        canvas.drawPoint(pointBottomLeft.x.toFloat(), pointBottomLeft.y.toFloat(), paint)

        // ImageView에 비트맵 이미지 적용
        imageView.setImageBitmap(mutableBitmap)
    }
    private fun onDrawBitmap(imageView: ImageView, bitmap: Bitmap) {
        // 사각형 그리기
        val paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        val path = Path()
        path.moveTo(pointTopLeft.x.toFloat(), pointTopLeft.y.toFloat())
        path.lineTo(pointTopRight.x.toFloat(), pointTopRight.y.toFloat())
        path.lineTo(pointBottomRight.x.toFloat(), pointBottomRight.y.toFloat())
        path.lineTo(pointBottomLeft.x.toFloat(), pointBottomLeft.y.toFloat())
        path.close()


        imageView.setImageBitmap(null) // 이전 이미지 제거
        val imageViewWidth = imageView.width
        val scaleFactor = imageViewWidth.toFloat() / bitmapp.width.toFloat()
        //val mutableBitmap = Bitmap.createScaledBitmap(bitmap, imageViewWidth, (bitmap.height * scaleFactor).toInt(), false)
        val mutableBitmap = Bitmap.createScaledBitmap(
            bitmapp,
            imageViewWidth,
            (bitmapp.height * scaleFactor).toInt(),
            true
        ).copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap!!)
        canvas.drawPath(path, paint)

        // 점 그리기
        paint.color = Color.BLUE
        paint.strokeWidth = 12f
        paint.style = Paint.Style.FILL_AND_STROKE

        canvas.drawPoint(pointTopLeft.x.toFloat(), pointTopLeft.y.toFloat(), paint)
        canvas.drawPoint(pointTopRight.x.toFloat(), pointTopRight.y.toFloat(), paint)
        canvas.drawPoint(pointBottomRight.x.toFloat(), pointBottomRight.y.toFloat(), paint)
        canvas.drawPoint(pointBottomLeft.x.toFloat(), pointBottomLeft.y.toFloat(), paint)

        // ImageView에 비트맵 이미지 적용
        imageView.setImageBitmap(mutableBitmap)
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }
    private fun findClosestPoint(point: Point, vararg points: Point): Point {
        var closestPoint = points[0]
        var minDistance = closestPoint.distanceTo(point)

        for (i in 1 until points.size) {
            val distance = points[i].distanceTo(point)
            if (distance < minDistance) {
                closestPoint = points[i]
                minDistance = distance
            }
        }
        return closestPoint
    }
    fun Point.distanceTo(other: Point): Float {
        val dx = (x - other.x).toDouble()
        val dy = (y - other.y).toDouble()

        return sqrt((dx * dx + dy * dy)).toFloat()
    }

    fun imageViewToImageCoordinatesFitCenter(imageView: ImageView, x: Float, y: Float): PointF {
        val drawable: Drawable = imageView.drawable ?: return PointF(x, y)
        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        val imageViewWidth = imageView.width
        val imageViewHeight = imageView.height

        // 이미지와 이미지 뷰의 가로세로비를 계산합니다.
        val imageRatio = imageWidth.toFloat() / imageHeight.toFloat()
        val imageViewRatio = imageViewWidth.toFloat() / imageViewHeight.toFloat()

        // 이미지의 스케일과 평행 이동 값을 계산합니다.
        val scaleX: Float
        val scaleY: Float
        val translateX: Float
        val translateY: Float

        if (imageRatio > imageViewRatio) {
            // 이미지가 이미지 뷰보다 더 넓은 경우
            scaleX = imageViewWidth.toFloat() / imageWidth.toFloat()
            scaleY = scaleX
            translateX = 0f
            translateY = (imageViewHeight - (imageHeight * scaleY)) / 2f
        } else {
            // 이미지가 이미지 뷰보다 더 높은 경우
            scaleY = imageViewHeight.toFloat() / imageHeight.toFloat()
            scaleX = scaleY
            translateX = (imageViewWidth - (imageWidth * scaleX)) / 2f
            translateY = 0f
        }

        // 이미지 뷰에서 실제 이미지의 크기와 위치를 고려하여 좌표를 변환합니다.
        val imageX = (x - translateX) / scaleX
        val imageY = (y - translateY) / scaleY

        Log.d("ScaledPoint", "${imageX}, ${imageY}")

        return PointF(imageX, imageY)
    }
}