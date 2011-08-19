package de.fhs.spirit.toilers

import java.io._
import android.content.Context
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.impl.conn.SingleClientConnManager
import org.apache.http.conn.ssl.SSLSocketFactory
import java.security.KeyStore
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.conn.scheme.{SocketFactory, PlainSocketFactory, Scheme, SchemeRegistry}
import de.fhs.spirit.R

class SpiritHttpClient (var context:Context) extends DefaultHttpClient {

  override protected def createClientConnectionManager() : ClientConnectionManager = {
      val registry = new SchemeRegistry()
      registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory, 80))
      // Register for port 443 our SSLSocketFactory with our keystore
      // to the ConnectionManager
      registry.register(new Scheme("https", newSslSocketFactory(), 443))
      new SingleClientConnManager(getParams, registry)
    }

    private def newSslSocketFactory() : SocketFactory = {
      try {
        // Get an instance of the Bouncy Castle KeyStore format
        var trusted = KeyStore.getInstance("BKS")
        // Get the raw resource, which contains the keystore with
        // your trusted certificates (root and any intermediate certs)
        val in: InputStream = context.getResources.openRawResource(R.raw.mykeystore)
        try {
          // Initialize the keystore with the provided trusted certificates
          // Also provide the password of the keystore
          trusted.load(in, "mysecret".toCharArray)
        } finally {
          in.close();
        }
        // Pass the keystore to the SSLSocketFactory. The factory is responsible
        // for the verification of the server certificate.
        val sf = new SSLSocketFactory(trusted)
        // Hostname verification from certificate
        // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
        sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER)

        sf
      } catch {
        case e: Exception => throw new AssertionError(e);
      }
    }

}