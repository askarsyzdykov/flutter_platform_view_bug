import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef void CameraViewCreatedCallback(CameraViewController controller);

class CameraView extends StatefulWidget {
  const CameraView({
    Key key,
    this.onCameraViewCreated,
  }) : super(key: key);

  final CameraViewCreatedCallback onCameraViewCreated;

  @override
  State<StatefulWidget> createState() => _CameraViewState();
}

class _CameraViewState extends State<CameraView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'plugins.kz.btsd/camera_view',
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text('$defaultTargetPlatform is not yet supported by the camera_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onCameraViewCreated == null) {
      return;
    }
    widget.onCameraViewCreated(CameraViewController._(id));
  }
}

class CameraViewController {
  CameraViewController._(int id) : _channel = MethodChannel('plugins.kz.btsd/camera_view_$id');

  final MethodChannel _channel;

  Future<void> startCamera(Facing facing) async {
    print('start camera');
    return _channel.invokeMethod('start_camera', {'facing': '${getStringValue(facing)}'});
  }

  Future<Uint8List> takePhoto() async {
    print('take photo');
    return _channel.invokeMethod('take_photo');
  }

  static String getStringValue(value) {
    return value.toString().split('.')[1];
  }
}

enum Facing {
  FRONT,
  BACK
}
