package com.piwew.jaki.laporan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityCameraBinding
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.utils.createCustomTempFile
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var currentImageUri: Uri? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        setupAccessibilityDelegates()
        setupClickListeners()
        // requestLocationPermissionIfNeeded()
    }

    private fun setupAccessibilityDelegates() {
        binding.ivActionBack.setAsAccessibilityRoleButton()
        binding.switchCamera.setAsAccessibilityRoleButton()
        binding.btnGallery.setAsAccessibilityRoleButton()
        binding.btnCaptureImage.setAsAccessibilityRoleButton()
        binding.tvTitlePage.setAsAccessibilityHeading()

        ViewCompat.addAccessibilityAction(
            binding.root,
            getString(R.string.action_capture)
        ) { _, _ ->
            takePhotoAndGetMyLastLocation()
            true
        }

        ViewCompat.addAccessibilityAction(
            binding.root,
            getString(R.string.action_gallery)
        ) { _, _ ->
            startGallery()
            true
        }
    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }
        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
        binding.btnCaptureImage.setOnClickListener { takePhotoAndGetMyLastLocation() }
        binding.btnGallery.setOnClickListener { startGallery() }
    }

    private fun requestLocationPermissionIfNeeded() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location: Location? ->
                currentLocation = location
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                showToast(getString(R.string.failed_show_camera))
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))

        updateSwitchCameraContentDescription()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)

                    val laporanData = LaporanData(
                        imageUri = savedUri.toString(),
                        latitude = currentLocation?.latitude,
                        longitude = currentLocation?.longitude
                    )

                    val intent = Intent(this@CameraActivity, AturLokasiLaporanActivity::class.java)
                    intent.putExtra(EXTRA_LAPORAN, laporanData)

                    startActivity(intent)
                    finish()
                }

                override fun onError(exc: ImageCaptureException) {
                    showToast(getString(R.string.failed_take_image))
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    /* Gallery */
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            val intent = Intent(this, AturLokasiLaporanActivity::class.java)

            val laporanData = LaporanData(
                imageUri = uri.toString()
            )

            intent.putExtra(EXTRA_LAPORAN, laporanData)
            startActivity(intent)
            finish()
        } else {
            showToast(getString(R.string.no_image_selected))
            Log.d("Photo Picker", "No media selected")
        }
    }

    /* Location */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    takePhotoAndGetMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    takePhotoAndGetMyLastLocation()
                }

                else -> {
                    showToast(getString(R.string.location_permission_denied))
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePhotoAndGetMyLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    takePhoto()
                } else {
                    Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun updateSwitchCameraContentDescription() {
        val description = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            getString(R.string.switch_to_front_camera)
        } else {
            getString(R.string.switch_to_back_camera)
        }
        binding.switchCamera.contentDescription = description
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    public override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_LAPORAN = "DATA"
    }

}