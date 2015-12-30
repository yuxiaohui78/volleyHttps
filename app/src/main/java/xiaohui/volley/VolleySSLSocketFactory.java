package xiaohui.volley;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

/*
 Create the trust keystore. You can give any password you want.
 keytool -import -v -alias server -file server.crt -keystore client.truststore -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider
  
 */

public final class VolleySSLSocketFactory {

	public static final String TAG = "VolleySSLSocketFactory";

	private static Context mContext = null;

	public static SSLSocketFactory getSSLSocketFactory (Context ctx) throws NoSuchAlgorithmException, KeyManagementException {
		return getSSLSocketFactory(ctx, null);
	}

	public static SSLSocketFactory getSSLSocketFactory (Context ctx, CertificateEncryption encryption)
			throws NoSuchAlgorithmException, KeyManagementException {
		
		mContext = ctx;

		SSLContext context = SSLContext.getInstance("TLS");

		try{
			//create key and trust managers
			KeyManager[] keyManagers = createKeyManagers( CertificateConfig.keyStoreFileName, CertificateConfig.keyStorePassword, CertificateConfig.alias, encryption);
			TrustManager[] trustManagers = createTrustManagers( CertificateConfig.trustStoreFileName, CertificateConfig.trustStorePassword, encryption);
			context.init(keyManagers, trustManagers, null);
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		SSLSocketFactory socketFactory = context.getSocketFactory();
		return socketFactory;
	}

	private static KeyManager[] createKeyManagers(String keyStoreFileName, String keyStorePassword, String alias, CertificateEncryption encryption)
			throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {

		//load certificate from asset file.
		InputStream inputStream = mContext.getResources().getAssets().open(keyStoreFileName);

		if (encryption != null) {

			inputStream = encryption.decode( inputStream );
		}

		KeyStore keyStore = KeyStore.getInstance( CertificateConfig.KEY_STORE_TYPE_P12);
		keyStore.load(inputStream, keyStorePassword.toCharArray());
		
		printKeystoreInfo(keyStore);//for debug

		KeyManager[] managers;
		if (alias != null) {
			managers =
					new KeyManager[] {
					new VolleySSLSocketFactory().new AliasKeyManager(keyStore, alias, keyStorePassword)};
		} else {
			KeyManagerFactory keyManagerFactory =
					KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, keyStorePassword == null ? null : keyStorePassword.toCharArray());
			managers = keyManagerFactory.getKeyManagers();
		}
		return managers;
	}

	private static TrustManager[] createTrustManagers(String trustStoreFileName, String trustStorePassword, CertificateEncryption encryption)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		InputStream inputStream = mContext.getResources().getAssets().open(trustStoreFileName);

		if (encryption != null) {
			inputStream = encryption.decode( inputStream );
		}

		KeyStore trustStore = KeyStore.getInstance( CertificateConfig.KEY_STORE_TYPE_BKS);
		trustStore.load(inputStream, trustStorePassword.toCharArray());

		printKeystoreInfo(trustStore);//for debug
		
		TrustManagerFactory trustManagerFactory =
				TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(trustStore);
		return trustManagerFactory.getTrustManagers();
	}

	private static void printKeystoreInfo(KeyStore keystore) throws KeyStoreException {
		Log.d(TAG, "Provider : " + keystore.getProvider().getName() );
		Log.d(TAG, "Type : " + keystore.getType());
		Log.d(TAG, "Size : " + keystore.size());

		Enumeration en = keystore.aliases();
		while (en.hasMoreElements()) {
			Log.d(TAG, "Alias: " + en.nextElement());
		}
	}

	private class AliasKeyManager implements X509KeyManager {
		private KeyStore _ks;
		private String _alias;
		private String _password;

		public AliasKeyManager(KeyStore ks, String alias, String password) {
			_ks = ks;
			_alias = alias;
			_password = password;
		}

		public String chooseClientAlias(String[] str, Principal[] principal, Socket socket) {
			return _alias;
		}

		public String chooseServerAlias(String str, Principal[] principal, Socket socket) {
			return _alias;
		}

		public X509Certificate[] getCertificateChain(String alias) {
			try {
				java.security.cert.Certificate[] certificates = this._ks.getCertificateChain(alias);
				if(certificates == null){throw new FileNotFoundException("no certificate found for alias:" + alias);}
				X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
				System.arraycopy(certificates, 0, x509Certificates, 0, certificates.length);
				return x509Certificates;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public String[] getClientAliases(String str, Principal[] principal) {
			return new String[] { _alias };
		}

		public PrivateKey getPrivateKey(String alias) {
			try {
				return (PrivateKey) _ks.getKey(alias, _password == null ? null : _password.toCharArray());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public String[] getServerAliases(String str, Principal[] principal) {
			return new String[] { _alias };
		}
	}
}
