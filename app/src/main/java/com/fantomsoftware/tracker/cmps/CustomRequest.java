package com.fantomsoftware.tracker.cmps;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class CustomRequest extends Request<JSONObject>{
//--------------------------------------------------------------------
private Response.Listener<JSONObject> listener;
private Map<String, String>           params;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public CustomRequest(
    String url,
    Map<String, String> params,
    Response.Listener<JSONObject> responseListener,
    Response.ErrorListener errorListener ){

  super( Method.GET, url, errorListener );
  this.listener = responseListener;
  this.params = params;

}//CustomRequest
//--------------------------------------------------------------------

public CustomRequest(
    int method,
    String url,
    Map<String, String> params,
    Response.Listener<JSONObject> responseListener,
    Response.ErrorListener errorListener ){

  super( method, url, errorListener );
  this.listener = responseListener;
  this.params = params;
}//CustomRequest
//--------------------------------------------------------------------


//--------------------------------------------------------------------
protected Map<String, String> getParams()
    throws com.android.volley.AuthFailureError{
  return params;
}//getParams
//--------------------------------------------------------------------
@Override
protected Response<JSONObject> parseNetworkResponse(
    NetworkResponse response ){

  try{

    String jsonString = new String(
        response.data,
        HttpHeaderParser.parseCharset( response.headers ) );

    return Response.success(
        new JSONObject( jsonString ),
        HttpHeaderParser.parseCacheHeaders( response ) );

  }catch( UnsupportedEncodingException e ){
    return Response.error( new ParseError( e ) );

  }catch( JSONException je ){
    return Response.error( new ParseError( je ) );
  }//try

}//parseNetworkResponse
//--------------------------------------------------------------------
@Override
protected void deliverResponse( JSONObject response ){

  listener.onResponse( response );

}//deliverResponse
//--------------------------------------------------------------------

}//CustomRequest
