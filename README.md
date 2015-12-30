# volleyHttps
##Making a HTTPS request using Android Volley and Self Certified SSL

This is an example for using volley with SSL.

Thank for Arnab Chakraborty's article. It's very helpful.

http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/

#How to use it.
###1. In AndroidManifest.xml add the VolleyHelperApplication
``` xml
<application
android:name="xiaohui.volley.VolleyHelperApplication"
...
</application>
```
#2. Example:
## StringRequest - HTTP/GET
``` groovy
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
```

##StringRequst HTTP/POST
``` groovy
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
```    
##JsonRequest HTTPS/GET
``` groovy
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
```
##JsonRequest HTTP/POST
``` groovy
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
```
##JsonArrayRequest HTTP/GET
``` groovy
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
```
#3. Using Self Certified SSL

1. Replace the certificates client.key.p12 and client.truststore with our own certificates in folder assets
2. Configure the CertificateConfig.java
2. Then use the following method to send the request.
``` groovy
VolleyDataRequester.withSelfCertifiedHttps( this )
                .setUrl( You_url)
                .setJsonResponseListener( new YouJsonRequestListener ())
                .requestJson();
```

#Screeshot:
![Alt text](https://raw.githubusercontent.com/yuxiaohui78/volleyHttps/master/snapshoot/app.png "Home page")

References:

http://developer.android.com/training/volley/index.html

https://github.com/yuxiaohui78/android-volley

http://code.tutsplus.com/tutorials/an-introduction-to-volley--cms-23800

http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
