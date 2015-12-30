package xiaohui.volleyhttps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import xiaohui.volley.VolleyDataRequester;

/*
Free http, https server.
* http://httpbin.org/
* */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String HTTP_HOST = "http://httpbin.org";
    public static final String HTTPS_HOST = "https://httpbin.org";
    public static final String IP = "/ip";
    public static final String POST = "/post";

    public static final String HTTPS_JSONARRAY = "https://api.github.com/users/mralexgray/repos"; //Free array test.

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        findViewById( R.id.btnHttpsGetJsonRequest ).setOnClickListener( this );
        findViewById( R.id.btnHttpsPostJsonRequest ).setOnClickListener( this );
        findViewById( R.id.btnHttpPostRequest ).setOnClickListener( this );
        findViewById( R.id.btnHttpGetRequest ).setOnClickListener( this );
        findViewById( R.id.btnHttpsGetJsonArrayRequest ).setOnClickListener( this );

        tvResult = (TextView)findViewById( R.id.tvResult );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnHttpGetRequest:
                stringRequestGetHttpExample();
                break;

            case R.id.btnHttpPostRequest:
                stringRequestPostHttpExample();
                break;

            case R.id.btnHttpsGetJsonRequest:
                jsonRequestGetHttpsExample();
                break;

            case R.id.btnHttpsPostJsonRequest:
                jsonRequestPostHttpsExample ();
                break;

            case R.id.btnHttpsGetJsonArrayRequest:
                jsonArrayRequestGetHttpsExample ();
                break;
        }
    }

    private void stringRequestGetHttpExample(){

        VolleyDataRequester.withHttp( this )
                .setUrl( HTTP_HOST + IP)
                .setMethod( VolleyDataRequester.Method.GET )
                .setStringResponseListener( new VolleyDataRequester.StringResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText( MainActivity.this, "HTTP/POST,StringRequest successfully.", Toast.LENGTH_SHORT ).show();
                        tvResult.setText( response );
                    }
                } )
                .requestString();
    }

    private void stringRequestPostHttpExample(){

        HashMap<String, String> body = new HashMap <String, String>() ;
        body.put( "name", "xiaohui" );
        body.put( "gender", "male" );

        VolleyDataRequester.withHttp( this )
                .setUrl( HTTP_HOST +  POST)
                .setBody( body )
                .setMethod( VolleyDataRequester.Method.POST )
                .setStringResponseListener( new VolleyDataRequester.StringResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText( MainActivity.this, "HTTP/POST,StringRequest successfully.", Toast.LENGTH_SHORT ).show();
                        tvResult.setText( response );
                    }
                } )
                .requestString();
    }

    private void jsonRequestGetHttpsExample(){
        VolleyDataRequester.withDefaultHttps( this )
                .setUrl(HTTPS_HOST + IP)
                .setJsonResponseListener( new VolleyDataRequester.JsonResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String s = response.getString( "origin" );
                            tvResult.setText( s );
                            Toast.makeText( MainActivity.this, "HTTPS/GET, JsonRequest successfully.", Toast.LENGTH_SHORT ).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } )
                .setResponseErrorListener( new VolleyDataRequester.ResponseErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvResult.setText( error.getMessage() );
                    }
                } )
                .requestJson();
    }

    private void jsonRequestPostHttpsExample(){
        JSONObject json = new JSONObject(  );
        try{
            json.put( "name", "xiaohui" );
            json.put( "gender", "male" );
        }catch (Exception e){
            e.printStackTrace();
        }

        VolleyDataRequester.withDefaultHttps( this )
                .setUrl(HTTPS_HOST + POST)
                .setBody( json )
                .setJsonResponseListener( new VolleyDataRequester.JsonResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String data = response.getString( "data" );
                            tvResult.setText( data);

                            Toast.makeText( MainActivity.this, "HTTPS/POST, JsonRequest successfully.", Toast.LENGTH_SHORT ).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } )
                .setResponseErrorListener( new VolleyDataRequester.ResponseErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvResult.setText( error.getMessage() );
                    }
                } )
                .requestJson();
    }

    private void jsonArrayRequestGetHttpsExample(){
        VolleyDataRequester.withDefaultHttps( this )
                .setUrl(HTTPS_JSONARRAY)
                .setJsonArrayResponseListener( new VolleyDataRequester.JsonArrayResponseListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String output = "Array length=" + response.length();

                        for (int i = 0; i < response.length(); i++){
                            try {
                                JSONObject json = (JSONObject) response.get( i );
                                String name = json.getString( "name" );
                                output += "\n" + i + " - " + name;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        tvResult.setText( output );
                    }
                } )
                .setResponseErrorListener( new VolleyDataRequester.ResponseErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvResult.setText( error.getMessage() );
                    }
                } )
                .requestJsonArray();
    }

}
