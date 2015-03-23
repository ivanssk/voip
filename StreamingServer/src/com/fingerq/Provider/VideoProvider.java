package com.fingerq.Provider;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.ImageFormat;
import junit.framework.Assert;

final public class VideoProvider extends AVProvider {
	private Camera _camera;
	private int _previewWidth;
	private int _previewHeight;
	private SurfaceHolder _surfaceHolder;

	public VideoProvider(int previewWidth, int previewHeight, SurfaceHolder surfaceHolder) {
		_previewWidth = previewWidth;
		_previewHeight = previewHeight;
		_surfaceHolder = surfaceHolder;
	}

	/*
	 * This method will initate camera object and start its preview.
	 */
	public void start() {
		_camera = Camera.open();

		if (null == _camera)
			Assert.fail("camera instance cannot be null");

		try {
			_camera.setPreviewDisplay(_surfaceHolder);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		Camera.Parameters camera_parameters = _camera.getParameters();
		camera_parameters.setPreviewSize(_previewWidth, _previewHeight);
		camera_parameters.setPreviewFormat(ImageFormat.NV21);

		_camera.setParameters(camera_parameters);

		_camera.setPreviewCallback(new Camera.PreviewCallback() {
			public void onPreviewFrame(byte[] data, Camera camera) {
				VideoProvider.this.notify(data, data.length);
			}
		});

		_camera.startPreview();
	}

	/*
	 * This method will stop preview and release camera object.
	 */

	public void stop() {
		_camera.setPreviewCallback(null);
		_camera.stopPreview();
		_camera.release();
		_camera = null;
	}
}
