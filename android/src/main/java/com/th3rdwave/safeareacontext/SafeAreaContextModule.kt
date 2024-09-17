package com.th3rdwave.safeareacontext

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.PixelUtil

@ReactModule(name = SafeAreaContextModule.NAME)
class SafeAreaContextModule(reactContext: ReactApplicationContext?) :
    NativeSafeAreaContextSpec(reactContext) {
  override fun getName(): String {
    return NAME
  }

  /**
   * getConstants is invoked only at initialization,
   * but DecorView may be not available then
   */
  @ReactMethod
  @DoNotStrip
  override fun getWindowMetrics(promise: Promise) {
    Log.d("SafeAreaContext", "getWindowMetrics")

    val decorView = reactApplicationContext.currentActivity?.window?.decorView as ViewGroup?
    if (decorView == null) {
      promise.reject("Couldn't measure", "Couldn't find DecorView")
      return
    }

    val contentView = decorView.findViewById<View>(android.R.id.content)
    if (contentView == null) {
      promise.reject("Couldn't measure", "Couldn't find contentView")
      return
    }

    val insets = getSafeAreaInsets(decorView)
    val frame = getFrame(decorView, contentView)

    if (insets == null || frame == null) {
      promise.reject("Couldn't measure", "Couldn't measure insets and/or frame")
      return
    }

    Log.d("SafeAreaContext", "getWindowMetrics | insets: $insets, frame: $frame")

    val insetsMap = Arguments.createMap().also { out ->
      out.putDouble("top", PixelUtil.toDIPFromPixel(insets.top).toDouble())
      out.putDouble("right", PixelUtil.toDIPFromPixel(insets.right).toDouble())
      out.putDouble("bottom", PixelUtil.toDIPFromPixel(insets.bottom).toDouble())
      out.putDouble("left", PixelUtil.toDIPFromPixel(insets.left).toDouble())
    }

    val frameMap = Arguments.createMap().also { out ->
      out.putDouble("x", PixelUtil.toDIPFromPixel(frame.x).toDouble())
      out.putDouble("y", PixelUtil.toDIPFromPixel(frame.y).toDouble())
      out.putDouble("width", PixelUtil.toDIPFromPixel(frame.width).toDouble())
      out.putDouble("height", PixelUtil.toDIPFromPixel(frame.height).toDouble())
    }

    val readableMap = Arguments.createMap().also { out ->
      out.putMap("insets", insetsMap)
      out.putMap("frame", frameMap)
    }

    promise.resolve(readableMap)
  }

  public override fun getTypedExportedConstants(): Map<String, Any> {
    return MapBuilder.of<String, Any>("initialWindowMetrics", getInitialWindowMetrics() as Any)
  }

  private fun getInitialWindowMetrics(): Map<String, Any>? {
    Log.d("SafeAreaContext", "getInitialWindowMetrics")
    val decorView = reactApplicationContext.currentActivity?.window?.decorView as ViewGroup?
    Log.d("SafeAreaContext", "getInitialWindowMetrics decor view is ${if (decorView == null)"not" else ""} found")
    val contentView = decorView?.findViewById<View>(android.R.id.content) ?: return null
    val insets = getSafeAreaInsets(decorView)
    val frame = getFrame(decorView, contentView)
    Log.d("SafeAreaContext", "getInitialWindowMetrics | insets: $insets, frame: $frame")
    return if (insets == null || frame == null) {
      null
    } else mapOf("insets" to edgeInsetsToJavaMap(insets), "frame" to rectToJavaMap(frame))
  }

  companion object {
    const val NAME = "RNCSafeAreaContext"
  }
}
