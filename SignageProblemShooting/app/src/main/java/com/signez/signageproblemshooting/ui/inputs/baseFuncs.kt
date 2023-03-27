package com.signez.signageproblemshooting.ui.inputs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CODE_VIDEO_CAPTURE = 1
const val REQUEST_CODE_IMAGE_CAPTURE = 2


@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
fun createImageFile(viewModel: MediaViewModel): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_$timeStamp.jpg"

    // Get the public Pictures directory
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val appDir = File(storageDir, "SignEz")

    // Create the app directory if it doesn't exist
    if (!appDir.exists()) {
        appDir.mkdirs()
    }

    // Create the image file in the app directory
    val imageFile = File(appDir, imageFileName)
    viewModel.mCurrentPhotoPath.value = imageFile.absolutePath
//    mCurrentPhotoPath =
    return imageFile
}

@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
fun createVideoFile(viewModel: VideoViewModel): File {
    // Create a video file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val videoFileName = "MP4_$timeStamp.mp4"

    // Get the public Movies directory
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
    val appDir = File(storageDir, "SignEz")

    // Create the app directory if it doesn't exist
    if (!appDir.exists()) {
        appDir.mkdirs()
    }

    // Create the video file in the app directory
    //    mCurrentVideoPath.value = videoFile.absolutePath
    val videoFile = File(appDir, videoFileName)
    viewModel.mCurrentVideoPath.value = videoFile.absolutePath

    return videoFile
}

fun dispatchTakePictureIntent(activity: Activity, viewModel: MediaViewModel,type:Int) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(activity.packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile(viewModel)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            // Continue only if the File was successfully created
            // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
            if (photoFile != null) { // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                val providerURI = FileProvider.getUriForFile(activity, "com.signez.signageproblemshooting.provider", photoFile)
                // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                viewModel.type = type
                if (type == REQUEST_CODE_IMAGE_CAPTURE) {
                    activity.startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE)
                }
                else {
                    activity.startActivityForResult(takePictureIntent, type)
                }
            }
        }
    }
}

fun dispatchTakeVideoIntent(activity: Activity, viewModel: VideoViewModel) {
    Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
        // Ensure that there's a camera activity to handle the intent
        takeVideoIntent.resolveActivity(activity.packageManager)?.also {
            // Create the File where the video should go
            val videoFile: File? = try {
                createVideoFile(viewModel)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                val videoURI = FileProvider.getUriForFile(activity, "com.signez.signageproblemshooting.provider", videoFile)
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600) // Limit video duration to 30 seconds
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0) // 화질 가능한 낮게
                activity.startActivityForResult(takeVideoIntent, REQUEST_CODE_VIDEO_CAPTURE)
            }
        }
    }
}

fun galleryAddPic(context: Context, viewModel: MediaViewModel) {
    // Get the absolute path of the image file
    val imagePath = viewModel.mCurrentPhotoPath.value ?: return
    // Insert the image into the MediaStore
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "My Image")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATA, imagePath)
    }
//        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//    imageUri.value = Uri.parse(imagePath)
    viewModel.imageUri.value = Uri.parse(imagePath)
    Toast.makeText(context, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
}

fun galleryAddVideo(context: Context, viewModel: VideoViewModel) {
    // Get the absolute path of the video file
    val videoPath = viewModel.mCurrentVideoPath.value ?: return

    // Insert the video into the MediaStore
    val values = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, "My Video")
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.DATA, videoPath)
    }
    viewModel.videoUri.value = Uri.parse(videoPath)
    Toast.makeText(context, "Video saved to gallery.", Toast.LENGTH_SHORT).show()
}

fun getRealPathFromURI(uri: Uri,activity: Activity): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = activity.contentResolver.query(uri, projection, null, null, null)
    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor?.moveToFirst()
    val path = columnIndex?.let { cursor.getString(it) }
    cursor?.close()
    return path
}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}

fun playVideoFromUri(context: Context, uri: Uri) {
    val mediaPlayer = MediaPlayer().apply {
        setDataSource(context, uri)
        prepare()
        start()
    }
}