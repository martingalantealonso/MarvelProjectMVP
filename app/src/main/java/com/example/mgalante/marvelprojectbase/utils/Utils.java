package com.example.mgalante.marvelprojectbase.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.mgalante.marvelprojectbase.utils.Constants.LOGTAG;
import static com.example.mgalante.marvelprojectbase.utils.Constants.REQ_CREATE_FILE;
import static com.example.mgalante.marvelprojectbase.utils.Constants.apiClient;
import static com.example.mgalante.marvelprojectbase.utils.Constants.mMemoryCache;

public class Utils {
    /**
     * Crea un md5
     */
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Crea una carpeta en GoogleDrive
     */
    public static void createFolder(String name) {

        //Crear una carpeta
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(name)
                .build();

        DriveFolder folder = Drive.DriveApi.getRootFolder(apiClient);
        folder.createFolder(apiClient, changeSet).setResultCallback(
                new ResultCallback<DriveFolder.DriveFolderResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFolderResult result) {
                        if (result.getStatus().isSuccess())
                            Log.i(LOGTAG, "Carpeta creada con ID = " + result.getDriveFolder().getDriveId());
                        else
                            Log.e(LOGTAG, "Error al crear carpeta");
                    }
                });

    }

    /**
     * Guarda una imagen en GoogleDrive
     */
    public static void createFileWithActivity(final Activity activity, final String characterName) {

        Drive.DriveApi.newDriveContents(apiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        MetadataChangeSet changeSet =
                                new MetadataChangeSet.Builder()
                                        .setTitle(characterName)
                                        .setMimeType("image/png")
                                        .build();

                        getCacheimage(result.getDriveContents());

                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(changeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(apiClient);

                        try {
                            activity.startIntentSenderForResult(
                                    intentSender, REQ_CREATE_FILE, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(LOGTAG, "Error al iniciar actividad: Create File", e);
                        }
                    }

                });
    }

    /**
     * Llamado en createFileWithActivity, establece la imagen que se guardará
     *
     * @param driveContents
     */
    private static void getCacheimage(DriveContents driveContents) {

        Bitmap bitmap = null;
        OutputStream outputStream = driveContents.getOutputStream();

        try {
            //bitmap = ((GlideBitmapDrawable) mCircleDialogImage.getDrawable().getCurrent()).getBitmap();
            bitmap = getBitmapFromMemCache("CharacterImage");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            //byte[] byteArray = outputStream.toByteArray();
            //bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
        } catch (Throwable e) {

        } finally {

        }
    }

    /**
     * Elimina un archivo de Google Drive
     */
    public static void deleteFile(DriveId fileDriveId) {
        DriveFile file = fileDriveId.asDriveFile();

        //Opción 1: Enviar a la papelera
        file.trash(apiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess())
                    Log.i(LOGTAG, "Fichero eliminado correctamente.");
                else
                    Log.e(LOGTAG, "Error al eliminar el fichero");
            }
        });

        //Opción 2: Eliminar
        //file.delete(apiClient).setResultCallback(...)
    }

    public static final void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static final Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Intent para mandar compartir una imagen
     */
    public static final Intent shareCacheImage(String key) {

        Bitmap icon = getBitmapFromMemCache(key);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_image.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));

    }

}
