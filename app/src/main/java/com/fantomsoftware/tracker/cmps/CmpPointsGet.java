package com.fantomsoftware.tracker.cmps;


import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fantomsoftware.tracker.consts.ConstsRequest;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.interfaces.OnResultJSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CmpPointsGet{
//--------------------------------------------------------------------
private OnResultJSON on_result;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetOnResult( OnResultJSON on_result ){
  this.on_result = on_result;
}//SetOnResult
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void Request(){

  // Instantiate the RequestQueue.
  RequestQueue queue = Volley.newRequestQueue( AppData.context );
  String       url   = "http://fantomsoftware.com/fantom_tracker/tracker-server.php";


  Response.Listener<JSONObject> l_response = new Response.Listener<JSONObject>(){
    //----------------------------------------------------------------
    @Override
    public void onResponse( JSONObject response ){

      try{
        OnResult( response.getJSONObject( "data" ) );
      }catch( JSONException e ){
        Log.d( AppData.log_key, "CmpPointsGet bad JSON object: " + e.getMessage() );
      }//catch

    }//onResponse
    //----------------------------------------------------------------
  };//l_response


  Response.ErrorListener l_error = new Response.ErrorListener(){
    //----------------------------------------------------------------
    @Override
    public void onErrorResponse( VolleyError error ){
      OnResult( null );
    }//onErrorResponse
    //----------------------------------------------------------------
  };//l_error


  Map<String, String> request_data;
  request_data = new HashMap<>();
  request_data.put( "request", ConstsRequest.GET_POINTS );
  request_data.put( "user_id", AppData.user_current_id );
  request_data.put( "user_password", AppData.user_current_password );


  CustomRequest request = new CustomRequest(
      Request.Method.POST, url, request_data, l_response, l_error );

  queue.add( request );

}//RequestSync
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnResult( JSONObject data_json ){

  if( on_result != null ){
    on_result.OnResult( data_json );
  }//if

}//OnResult
//--------------------------------------------------------------------

}//CmpPointsGet
