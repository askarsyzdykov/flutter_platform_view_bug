import 'package:camera_view/camera_view.dart';
import 'package:flutter/material.dart';

void main() => runApp(MaterialApp(home: CameraViewExample()));

class CameraViewExample extends StatefulWidget {
  @override
  _CameraViewExampleState createState() => _CameraViewExampleState();
}

class _CameraViewExampleState extends State<CameraViewExample> {
  CameraViewController _cameraController;

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        CameraView(
          onCameraViewCreated: _onCameraViewCreated,
        ),
        Center(
          child: RaisedButton(
            child: Text('Take photo'),
            onPressed: () => _takePhoto(),
          ),
        ),
      ],
    );
  }

  void _onCameraViewCreated(CameraViewController controller) {
    this._cameraController = controller;
    controller.startCamera(Facing.FRONT);
  }

  void _takePhoto() async {
    final data = await _cameraController.takePhoto();
    print(data);
  }
}
