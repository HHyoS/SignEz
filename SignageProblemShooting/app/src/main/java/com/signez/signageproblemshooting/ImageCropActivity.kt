package com.signez.signageproblemshooting

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.signez.signageproblemshooting.service.PrePostProcessor
import org.pytorch.IValue
import org.pytorch.torchvision.TensorImageUtils
import java.lang.Math.sqrt

class ImageCropActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    // 점 네 개 추가
    val pointTopLeft = Point(0, 0)
    val pointTopRight = Point(300, 0)
    val pointBottomLeft = Point(0, 300)
    val pointBottomRight = Point(300, 300)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        var imageUri = Uri.parse(intent.getStringExtra("uri"))

        imageView = findViewById(R.id.imageView)

        imageView.setImageURI(imageUri)
        // ImageView에 대한 OnTouchListener 설정
        imageView.setOnTouchListener { v, event ->
            val viewWidth = v.width
            val viewHeight = v.height
            val imageWidth = imageView.drawable.intrinsicWidth
            val imageHeight = imageView.drawable.intrinsicHeight
            val scaleX = imageWidth.toFloat() / viewWidth.toFloat()
            val scaleY = imageHeight.toFloat() / viewHeight.toFloat()
            val scaledTouchX = event.x * scaleX
            val scaledTouchY = event.y * scaleY
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치 다운 이벤트 처리
                    // 가장 가까운 점을 찾아 이동
                    val touchPoint = Point(scaledTouchX.toInt(), scaledTouchY.toInt())
                    Log.d("touchPoint","${touchPoint}")
                    val closestPoint = findClosestPoint(
                        touchPoint,
                        pointTopLeft,
                        pointTopRight,
                        pointBottomRight,
                        pointBottomLeft
                    )
                    if (touchPoint.distanceTo(closestPoint) < 50) {
                        closestPoint.x = touchPoint.x
                        closestPoint.y = touchPoint.y
                        imageView.invalidate() // ImageView 다시 그리기
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // 터치 이동 이벤트 처리
                    // 이동된 점 다시 그리기

                    val touchPoint = Point(scaledTouchX.toInt(), scaledTouchY.toInt())
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
                    true
                }
                else -> false
            }
        }
        val btn: Button = findViewById(R.id.exit_btn)
        btn.setOnClickListener {
            val intent = Intent(this, ErrorDetectActivity::class.java).apply {
                putExtra("tlx", pointTopLeft.x)
                putExtra("tly", pointTopLeft.y)
                putExtra("trx", pointTopRight.x)
                putExtra("try", pointTopRight.y)
                putExtra("blx", pointBottomLeft.x)
                putExtra("bly", pointBottomLeft.y)
                putExtra("pointBottomRight", pointBottomRight)
            }
            startActivity(intent)
            finish()
        }
        // ImageView 다시 그리기
        imageView.viewTreeObserver.addOnDrawListener {
            onDraw(imageView,imageUri)
        }
    }

    private fun onDraw(imageView: ImageView,uri : Uri) {
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
        val bitmap = uriToBitmap(this,uri)
        val imageViewWidth = imageView.width
        val scaleFactor = imageViewWidth.toFloat() / bitmap.width.toFloat()
        val mutableBitmap = Bitmap.createScaledBitmap(bitmap, imageViewWidth, (bitmap.height * scaleFactor).toInt(), true)
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
        val dx = (x - other.x).toFloat()
        val dy = (y - other.y).toFloat()
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
}