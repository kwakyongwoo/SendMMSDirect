package com.dyddyd.sendmms.main.send

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.Telephony
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.data.ExifOrientationStream
import com.dyddyd.sendmms.R
import com.dyddyd.sendmms.databinding.FragmentSendBinding
import com.dyddyd.sendmms.repository.Utils
import com.dyddyd.sendmms.repository.data.History
import com.dyddyd.sendmms.repository.data.HistoryViewModel
import com.dyddyd.sendmms.repository.sharedpreference.MMSPreferenceManager
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.Settings
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*


class SendFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_GET_IMAGE_FROM_GALLERY = 2

    private lateinit var viewModel: HistoryViewModel

    private lateinit var binding: FragmentSendBinding

    private var photoFile: File? = null
    private var uri: Uri? = null
    private var fileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send, container, false)
        binding.pref = MMSPreferenceManager.Companion

        Utils.setStatusBarColor(activity!!, R.color.white)
        Utils.changeStatusBarTextColorLight(activity!!)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)

        val sdf = SimpleDateFormat("yyyy년 M월 d일 E요일")
        binding.date.text = sdf.format(Date())

        clickableTrue()

        binding.apply {
            cardCamera.setOnClickListener {
                clickableFalse()
                showCameraIntent()
            }
            cardGallery.setOnClickListener {
                clickableFalse()
                showGalleryIntent()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        clickableTrue()
    }


    private var contentUri: Uri? = null
    private fun showCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(activity!!.packageManager)?.also {
                photoFile = try {
                    // 원본 이미지 생성
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    uri = FileProvider.getUriForFile(activity!!, "com.dyddyd.sendmmsdirect", it)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun showGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_GET_IMAGE_FROM_GALLERY)
    }

    // 두번 클릭 방지
    private fun clickableFalse() {
        binding.apply {
            cardCamera.isClickable = false
            cardGallery.isClickable = false
        }
    }

    private fun clickableTrue() {
        binding.apply {
            cardCamera.isClickable = true
            cardGallery.isClickable = true
        }
    }

    private var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        return File(activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp.png")
            .apply {
                currentPhotoPath = absolutePath
            }
    }

    private fun galleryAddPic() {
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val fileName = "dyddyd-${timeStamp}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 0)

            val contentUri = activity!!.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            contentUri?.let {
                val pdf = activity!!.contentResolver.openFileDescriptor(contentUri, "w", null)
                pdf?.also {
                    val fos = FileOutputStream(pdf.fileDescriptor)
                    fos.write(Files.readAllBytes(Paths.get(currentPhotoPath)))
                    fos.close()
                }
                fileUri = it
                Log.d("fileUri", fileUri.toString())
            }
        } else {
            val contentUri = activity!!.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            contentUri?.let {
                val pdf = activity!!.contentResolver.openFileDescriptor(contentUri, "w", null)
                pdf?.also {
                    val fos = FileOutputStream(pdf.fileDescriptor)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        fos.write(Files.readAllBytes(Paths.get(currentPhotoPath)))
                    }
                    fos.close()
                }
                fileUri = it
                Log.d("fileUri", fileUri.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val setting = Settings()
            setting.useSystemSending = true
            val sendTransaction = Transaction(context, setting)
            val message = Message(
                MMSPreferenceManager.content,
                MMSPreferenceManager.phoneNum
            )
            val id: Long = android.os.Process.getThreadPriority(android.os.Process.myTid()).toLong()

            val option = BitmapFactory.Options()
            option.inSampleSize = 2

            when (requestCode) {
                // 사진 촬영
                REQUEST_IMAGE_CAPTURE -> {
                    galleryAddPic()
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath, option)
                    val rotateBitmap = rotateBitmap(bitmap, 90)

                    message.addImage(rotateBitmap)

                }

                REQUEST_GET_IMAGE_FROM_GALLERY -> {
                    fileUri = data?.data
                    if (data?.data == null) Toast.makeText(
                        context,
                        "선택한 이미지가 존재하지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    else {
                        val bitmap = getBitmapFromUri(data.data!!)

                        val compressBitmap = Bitmap.createScaledBitmap(
                            bitmap,
                            (bitmap.width * 0.5).toInt(),
                            (bitmap.height * 0.5).toInt(),
                            false
                        )
                        val rotateBitmap = rotateBitmap(compressBitmap, 90)
                        message.addImage(rotateBitmap)
                    }
                }
            }

            sendTransaction.sendNewMessage(message, id)

            val sdp = SimpleDateFormat("yyyy년 MM월 dd일 kk시 mm분 ss초")

            viewModel.insert(
                History(
                    0,
                    MMSPreferenceManager.name,
                    MMSPreferenceManager.phoneNum,
                    MMSPreferenceManager.content,
                    fileUri.toString(),
                    sdp.format(Date())
                )
            )
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = activity!!.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }

    private fun rotateBitmap(source: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}