package kz.btsd.camera_view

import io.flutter.plugin.common.PluginRegistry.Registrar

class CameraViewPlugin {
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            registrar
                .platformViewRegistry()
                .registerViewFactory(
                    "plugins.kz.btsd/camera_view",
                    CameraViewFactory(registrar.messenger())
                )
        }
    }
}
