package com.canasikk.attachjavamail;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
// Instagram : https://www.instagram.com/canasikk/
// GitHub : https://github.com/canasikk
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


// Instagram : https://www.instagram.com/canasikk/
// GitHub : https://github.com/canasikk

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 43;
    private static final int IMAGE_PICK = 1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 331;
    String mail, pw, yol;
    EditText isim, iletisim;
    Button yolla, sec;

    javax.mail.Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isim = (EditText) findViewById(R.id.txtSoruAdi);
        iletisim = (EditText) findViewById(R.id.txtSoruIletisim);
        yolla = (Button) findViewById(R.id.btnSoruGOnder);
        sec = (Button) findViewById(R.id.btnResimSec);
        if (!checkAndRequestPermissions()) {
            return;
        }

        if (yol == null) {
            yolla.setEnabled(false);
        }

        mail = "SEND_YOUR_MAIL@gmail.com";
        pw = "MAIL_PASSWORD";

        sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkAndRequestPermissions()) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Bir Fotoğraf Seçin"), IMAGE_PICK);
            }
        });

        yolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isim.getText().toString() != null && isim.getText().toString() != "") {
                    if (iletisim.getText().toString() != null && iletisim.getText().toString() != "") {
                        if (yol != null) {
                            if (!checkAndRequestPermissions()) {
                                return;
                            }
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Dikkat !");
                            builder.setMessage("Mesaj gönderilsin mi ?");
                            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);
                                    Properties properties = new Properties();
                                    properties.put("mail.smtp.host", "smtp.googlemail.com");
                                    properties.put("mail.smtp.socketFactory.port", "465");
                                    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                                    properties.put("mail.smtp.auth", "true");
                                    properties.put("mail.smtp.port", "465");
                                    try {
                                        session = javax.mail.Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                                            @Override
                                            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                                                return new javax.mail.PasswordAuthentication(mail, pw);
                                            }
                                        });
                                        if (session != null) {
                                            MimeMessage message = new MimeMessage(session);
                                            message.setFrom(new InternetAddress(mail));
                                            message.setSubject("Mesaj başlık");
                                            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("TAKE_MAIL@gmail.com"));
                                            BodyPart messageBodyPart = new MimeBodyPart();
                                            messageBodyPart.setText(isim.getText().toString() + " #|# " + iletisim.getText().toString());
                                            Multipart multipart = new MimeMultipart();
                                            multipart.addBodyPart(messageBodyPart);
                                            messageBodyPart = new MimeBodyPart();
                                            FileDataSource source = new FileDataSource(yol);
                                            messageBodyPart.setDataHandler(new DataHandler(source));
                                            messageBodyPart.setFileName(yol);
                                            multipart.addBodyPart(messageBodyPart);
                                            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
                                            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
                                            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
                                            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
                                            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
                                            mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
                                            message.setContent(multipart);
                                            Transport.send(message);
                                            Toast.makeText(MainActivity.this, "Mesaj Başarıyla Gönderildi!", Toast.LENGTH_SHORT).show();
                                            finish();

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Resim Seçin", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "İletişim Boş Olamaz", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "İsim Boş Olamaz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Resim seç
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        }
        switch (requestCode) {
            case IMAGE_PICK:
                this.imageFromGallery(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void imageFromGallery(int resultCode, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        //this.resim.setImageBitmap(BitmapFactory.decodeFile(filePath));
        yol = filePath;
        if (yol != null) {
            yolla.setEnabled(true);
        }
        cursor.close();

    }
    //Resim seç

    // İzinler
    private boolean checkAndRequestPermissions() {
        int permissionINTERNET = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        int permissionACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionRECEIVE_BOOT_COMPLETED = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionREAD_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionINTERNET != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (permissionACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (permissionINTERNET != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (permissionWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionRECEIVE_BOOT_COMPLETED != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }
        if (permissionREAD_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    // İzinler
}
