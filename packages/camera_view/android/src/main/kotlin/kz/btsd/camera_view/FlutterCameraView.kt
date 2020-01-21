package kz.btsd.camera_view

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.priyankvasa.android.cameraviewex.AspectRatio
import com.priyankvasa.android.cameraviewex.CameraView
import com.priyankvasa.android.cameraviewex.Modes.AutoFocus
import com.priyankvasa.android.cameraviewex.Modes.JpegQuality
import com.priyankvasa.android.cameraviewex.Size
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.platform.PlatformView

private const val FACING_KEY = "facing"
private const val TAG = "FlutterCameraView"

class FlutterCameraView internal constructor(
    context: Context,
    messenger: BinaryMessenger?,
    id: Int
) : PlatformView,
    MethodCallHandler,
    LifecycleOwner,
    ActivityAware {

    private val cameraView: CameraView = CameraView(context)
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        cameraView.layoutParams = params
        cameraView.jpegQuality = JpegQuality.HIGH
        cameraView.touchToFocus = true
        cameraView.autoFocus = AutoFocus.AF_AUTO
        val methodChannel = MethodChannel(messenger, "plugins.kz.btsd/camera_view_$id")
        methodChannel.setMethodCallHandler(this)
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun getView(): View {
        return cameraView
    }

    override fun onFlutterViewAttached(flutterView: View) {
        Log.d(TAG, "onFlutterViewAttached")
    }

    override fun onFlutterViewDetached() {
        Log.d(TAG, "onFlutterViewDetached")
    }

    override fun dispose() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        cameraView.destroy()
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "start_camera" -> startCamera(methodCall, result)
            "take_photo" -> takePhoto(result)
            else -> result.notImplemented()
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private fun startCamera(methodCall: MethodCall, result: MethodChannel.Result) {
        Log.d(TAG, "startCamera")
        val facing = if (methodCall.hasArgument(FACING_KEY)) {
            Facing.valueOf(methodCall.argument<String>(FACING_KEY)!!)
        } else {
            Facing.BACK
        }
        cameraView.facing = facing.value
        cameraView.addCameraOpenedListener {
            Log.d(TAG, "addCameraOpenedListener")
            val metrics = cameraView.resources.displayMetrics
            cameraView.aspectRatio = AspectRatio.of(16, 9)
        }
        cameraView.start()
        result.success(null)
    }

    private fun takePhoto(result: MethodChannel.Result) {
        cameraView.capture()
        cameraView.addPictureTakenListener {
            result.success(it.data)
        }
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        Log.d(TAG, "onAttachedToActivity")
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        cameraView.start()
    }

    override fun onDetachedFromActivity() {
        Log.d(TAG, "onDetachedFromActivity")
        cameraView.stop()
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }
}

private enum class Facing(val value: Int) {
    BACK(0),
    FRONT(1)
}