package xiaohui.volley;

import android.app.Application;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Xiaohui on 12/29/2015.
 * reference article:
 * http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
 */
public class VolleyHelperApplication extends Application {

    private static VolleyHelperApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * @return VolleyHelper singleton instance
     */
    public static synchronized VolleyHelperApplication getInstance() {
        return mInstance;
    }

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueueWithSelfCertifiedSsl;

    private RequestQueue mRequestQueueWithDefaultSsl;

    private RequestQueue mRequestQueueWithHttp;

    public RequestQueue getRequestQueueWithHttp() {
        if (mRequestQueueWithHttp == null) {
            mRequestQueueWithHttp = Volley.newRequestQueue( getApplicationContext() );
        }
        
        return mRequestQueueWithHttp;
    }

    public RequestQueue getRequestQueueWithDefaultSsl() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueueWithDefaultSsl == null) {

            Network network = new BasicNetwork( new HurlStack() );

            Cache cache = new DiskBasedCache( getCacheDir(), 1024 * 1024 );

            RequestQueue queue = new RequestQueue( cache, network );
            queue.start();

            mRequestQueueWithDefaultSsl = queue;  //Volley.newRequestQueue(getApplicationContext());

            SSLCertificateValidation.trustAllCertificate();
        }

        return mRequestQueueWithDefaultSsl;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueueWithSelfCertifiedSsl() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueueWithSelfCertifiedSsl == null) {
            try {
                SSLSocketFactory sslSocketFactory = VolleySSLSocketFactory.getSSLSocketFactory( getApplicationContext() );

                Network network = new BasicNetwork( new HurlStack( null, sslSocketFactory ) );

                Cache cache = new DiskBasedCache( getCacheDir(), 1024 * 1024 );

                RequestQueue queue = new RequestQueue( cache, network );
                queue.start();

                mRequestQueueWithSelfCertifiedSsl = queue;  //Volley.newRequestQueue(getApplicationContext());

            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
                public boolean verify(String hostName, SSLSession ssls) {
                    return true;
                }
            } );
        }

        return mRequestQueueWithSelfCertifiedSsl;
    }
}
