package com.example.piotn.pmob_td;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.heroku.sdk.EnvKeyStore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class KeyStoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_store);

        final EditText url = (EditText) findViewById(R.id.editText);
        //final EditText certsLabel = (EditText) findViewById(R.id.editText2);
        final TextView certsLabel = (TextView) findViewById(R.id.textView2);
        Button b = (Button) findViewById(R.id.button5);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL url = null;
                try {
                    url = new URL("https://www.google.com/");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection con = null;
                try {
                    con = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    con.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Certificate userCert[] = con.getServerCertificates();
                    KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(null);
                    for(int i =0;i<userCert.length;i++) {
                        trustStore.setCertificateEntry("cc", userCert[i]);
                    }

                    StringBuilder builder = null;
                    Enumeration<String> aliases = trustStore.aliases();
                    while(aliases.hasMoreElements()){
                        X509Certificate cert = (X509Certificate) trustStore.getCertificate(aliases.nextElement());
                        builder = new StringBuilder();
                        builder.append(cert.toString());

                        //Log.d("cc", cert.toString());

                    }

                    certsLabel.setText(builder.toString());

                } catch (SSLPeerUnverifiedException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
