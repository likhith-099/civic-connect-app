package com.civicconnect.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Java utility class for compressing and scaling bitmaps before upload.
 * Reduces network payload and storage bandwidth for civic issue photo submissions.
 */
public final class ImageCompressor {

    private ImageCompressor() {
        // Prevent instantiation
    }

    /**
     * Compresses a Bitmap into JPEG byte array given a maximum quality percentage.
     */
    public static byte[] compressToByteArray(Bitmap bitmap, int quality) {
        if (bitmap == null) {
            return new byte[0];
        }
        int clampedQuality = Math.max(10, Math.min(100, quality));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, clampedQuality, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Resizes a Bitmap if its dimensions exceed maxDimension while preserving aspect ratio.
     */
    public static Bitmap resizeIfNeeded(Bitmap bitmap, int maxDimension) {
        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap;
        }

        float aspectRatio = (float) width / (float) height;
        int newWidth;
        int newHeight;

        if (width > height) {
            newWidth = maxDimension;
            newHeight = Math.round(maxDimension / aspectRatio);
        } else {
            newHeight = maxDimension;
            newWidth = Math.round(maxDimension * aspectRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
