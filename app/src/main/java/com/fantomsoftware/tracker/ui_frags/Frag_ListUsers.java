package com.fantomsoftware.tracker.ui_frags;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.adapters.Adapter_Users;
import com.fantomsoftware.tracker.consts.ConstsAct;
import com.fantomsoftware.tracker.data.User;


public class Frag_ListUsers extends Fragment{
  //------------------------------------------------------------------
  public static final String TAG = "Frag_ListUsers";

  private View root_view;
  private Adapter_Users adapter;
  private SparseArray<User> data;
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup cntr, Bundle bundle ){
    root_view = inflater.inflate( R.layout.frag_list_users, cntr, false );

    Init();

    return root_view;

  }//onCreateView
  //------------------------------------------------------------------
  @Override
  public void onActivityResult(
      int requestCode, int resultCode, Intent intent ){

    if( resultCode != Activity.RESULT_OK ){
      return;
    }//if

    switch( requestCode ){
      case ConstsAct.REQUEST_NEW:
        OnItemAdded( intent );
        break;

      case ConstsAct.REQUEST_EDIT:
        OnItemEdited( intent );
        break;
    }//switch

  }//onActivityResult
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  private void Init(){

    InitList();
    InitFloatingActionButton();

  }//Init
  //------------------------------------------------------------------
  private void InitList(){

    UpdateData();

  }//InitList
  //------------------------------------------------------------------
  private void InitFloatingActionButton(){
    /*
    View.OnClickListener cl_fab = new View.OnClickListener(){
      @Override
      public void onClick( View v ){
        Intent intent = new Intent( AppData.context, Act_User.class );
        intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_NEW );
        startActivityForResult( intent, ConstsAct.REQUEST_NEW );
      }//onClick
    };

    FloatingActionButton fab = ( FloatingActionButton )
        root_view.findViewById( R.id.fab_main );

    fab.setOnClickListener( cl_fab );
    */
  }//InitFloatingActionButton
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  private void UpdateData(){
/*
    OnResultId on_item_click = new OnResultId(){
      @Override
      public void OnResult( int id ){
        OnListItemClick( id );
      }//OnResult
    };//on_item_click


    OnResultId on_item_long_click = new OnResultId(){
      @Override
      public void OnResult( int id ){
        OnListItemClickLong( id );
      }//OnResult
    };//on_item_click


    AppData.app_instance.user_db.UpdateLocal();
    data = AppData.app_instance.user_db.users;

    adapter = new RecycleA_Users( data );
    adapter.SetOnListItemClick( on_item_click );
    adapter.SetOnListItemClickLong( on_item_long_click );


    RecyclerView.LayoutManager layout_manager =
        new LinearLayoutManager( AppData.context );

    RecyclerView rv_list =
        ( RecyclerView ) root_view.findViewById( R.id.rv_list );

    rv_list.setLayoutManager( layout_manager );
    rv_list.setAdapter( adapter );
    */
  }//UpdateData
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  private void OnListItemClick( int id ){
    /*
    Intent intent = new Intent( AppData.context, Act_User.class );
    intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_EDIT );
    intent.putExtra( ConstsAct.KEY_ID, id );
    startActivityForResult( intent, ConstsAct.REQUEST_EDIT );
    */
  }//OnListItemClick
  //------------------------------------------------------------------
  private void OnListItemClickLong( int id ){
/*
    if( id < 0 ){
      return;
    }

    final int item_id = id;

    DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener(){
      @Override
      public void onClick( DialogInterface dialog, int which ){
        switch( which ){
          case R.id.bs_delete:
            if( AppData.app_instance.user_db.Remove( item_id ) != -1 ){
              data.remove( item_id );
              adapter.Update();
              Utils.SnackL( root_view, getString( R.string.bottom_sheet_delete_snack_success ) );
            }else{
              Utils.SnackL( root_view, getString( R.string.bottom_sheet_delete_snack_fail ) );
            }//else
            break;
        }//switch
      }//onClick
    };

    BottomSheet sheet = new BottomSheet.Builder( getActivity(), R.style.BottomSheet )
        .title( getString( R.string.bottom_sheet_title ) )
        .sheet( R.menu.bottom_sheet_delete_option )
        .listener( cl )
        .build();
    sheet.show();
    */


/*
      DialogInterface.OnClickListener l_click =
          new DialogInterface.OnClickListener(){

            @Override
            public void onClick( DialogInterface dialog, int which ){

              switch( which ){
                case R.id.bs_delete:
                  //do somethings
                  break;
              }//switch

            }//onClick
          };//l_click


      BottomSheet.Builder sheet_builder =
          new BottomSheet.Builder( this, R.style.BottomSheetMy );

      sheet_builder.title( "Bottom Sheet Title" );
      sheet_builder.sheet( R.menu.bottom_sheet_delete_option );
      sheet_builder.listener( l_click );

      BottomSheet bottom_sheet_options = sheet_builder.build();
      bottom_sheet_options.show();
*/
  }//OnListItemClickLong
  //------------------------------------------------------------------
  private void OnItemAdded( Intent intent ){
    /*
    Bundle extras = intent.getExtras();
    if( !extras.containsKey( ConstsAct.KEY_ID ) ){
      return;
    }//if

    int id = extras.getInt( ConstsAct.KEY_ID );
    User item = AppData.app_instance.user_db.Get( id );

    Utils.AppendItem( data, item );

    adapter.Update();
    */
  }//OnItemAdded
  //------------------------------------------------------------------
  private void OnItemEdited( Intent intent ){
    /*
    Bundle extras = intent.getExtras();
    if( !extras.containsKey( ConstsAct.KEY_ID ) ){
      return;
    }//if

    int id = extras.getInt( ConstsAct.KEY_ID );
    User item = AppData.app_instance.user_db.Get( id );

    boolean is_found = false;
    for( int i = 0; i < data.size(); i++ ){
      if( data.valueAt( i ).id == item.id ){
        data.setValueAt( i, item );
        is_found = true;
        break;
      }//if
    }//for

    if( !is_found ){
      data.put( data.size(), item );
    }//if not found

    adapter.Update();
    */
  }//OnItemEdited
  //------------------------------------------------------------------


}//Frag_ListUsers

