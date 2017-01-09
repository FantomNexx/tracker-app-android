package com.fantomsoftware.tracker.ui_acts;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.data.AppData;
import com.fantomsoftware.tracker.ui_frags.Frag_ListUsers;
import com.fantomsoftware.tracker.ui_frags.Frag_TrackMe;
import com.fantomsoftware.tracker.ui_frags.Frag_TrackOthers;
import com.fantomsoftware.tracker.utils.Utils;

public class Act extends AppCompatActivity{
//--------------------------------------------------------------------
private Toolbar        toolbar;
private DrawerLayout   drawer_layout;
private NavigationView navigation_view;


public static final String sp_key_id_fragment_current =
    "sp_key_id_fragment_current";

private int id_fragment_current = -1;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected void onCreate( Bundle savedInstanceState ){
  super.onCreate( savedInstanceState );
  setContentView( R.layout.act );
  AppData.SetCurrentActivity( this );
  InitActivity();
}
//--------------------------------------------------------------------
@Override
protected void onResume(){
  super.onResume();
  AppData.SetCurrentActivity( this );
}
//--------------------------------------------------------------------
@Override
protected void onDestroy(){
  super.onDestroy();
  AppData.Terminate();
}
//--------------------------------------------------------------------
@Override
public void onBackPressed(){
  super.onBackPressed();
  AppData.TerminateActivity();
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void InitActivity(){

  navigation_view = (NavigationView) findViewById( R.id.navigation_view );

  InitToolbar();
  InitNavigationView();
  InitDrawer();
  InitFragment();
}
//--------------------------------------------------------------------
private void InitToolbar(){

  toolbar = (Toolbar) findViewById( R.id.toolbar );

  if( toolbar == null ){
    return;
  }//if

  toolbar.setTitle( R.string.app_name );

  setSupportActionBar( toolbar );

}
//--------------------------------------------------------------------
private void InitNavigationView(){

  NavigationView.OnNavigationItemSelectedListener on_item_selected =
      new NavigationView.OnNavigationItemSelectedListener(){
        //----------------------------------------------------------
        @Override
        public boolean onNavigationItemSelected( MenuItem menu_item ){

          menu_item.setChecked( true );
          drawer_layout.closeDrawers();
          return NavigateToFragment( menu_item.getItemId() );

        }//onNavigationItemSelected
        //----------------------------------------------------------
      };//onNavigationItemSelected


  navigation_view.setNavigationItemSelectedListener( on_item_selected );
}
//--------------------------------------------------------------------
private void InitDrawer(){

  drawer_layout = (DrawerLayout)
      findViewById( R.id.drawer_layout );

  if( drawer_layout == null ){
    return;
  }//if


  ActionBarDrawerToggle action_bar_toggle =
      new ActionBarDrawerToggle(
          this,
          drawer_layout,
          toolbar,
          R.string.drawer_open,
          R.string.drawer_close ){
        //----------------------------------------------------------
        @Override
        public void onDrawerClosed( View drawerView ){
          super.onDrawerClosed( drawerView );
        }//onDrawerClosed

        //----------------------------------------------------------
        @Override
        public void onDrawerOpened( View drawerView ){
          super.onDrawerOpened( drawerView );
        }//onDrawerOpened
        //----------------------------------------------------------

      };//action_bar_toggle

  drawer_layout.setDrawerListener( action_bar_toggle );
    /*

    //todo try this
    DrawerLayout.DrawerListener l_drawer =
        new DrawerLayout.DrawerListener(){
          //----------------------------------------------------------
          @Override
          public void onDrawerSlide( View drawerView, float slideOffset ){

          }//onDrawerSlide
          //----------------------------------------------------------
          @Override
          public void onDrawerOpened( View drawerView ){

          }//onDrawerOpened
          //----------------------------------------------------------
          @Override
          public void onDrawerClosed( View drawerView ){

          }//onDrawerClosed
          //----------------------------------------------------------
          @Override
          public void onDrawerStateChanged( int newState ){

          }//onDrawerStateChanged
        };//l_drawer

    drawer_layout.addDrawerListener( l_drawer );
    */

  //calling sync state is necessay to show icon
  action_bar_toggle.syncState();

}
//--------------------------------------------------------------------
private void InitFragment(){
  id_fragment_current = GetIdFragmentCurrent();
  NavigateToFragment( id_fragment_current );
}
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private int GetIdFragmentCurrent(){

  id_fragment_current = Utils.GetSharedPrefInt(
      sp_key_id_fragment_current );

  switch( id_fragment_current ){
    case R.id.drawer_item_track_me:
    case R.id.drawer_item_track_others:
      break;

    default:
      id_fragment_current = R.id.drawer_item_track_me;
      break;
  }//switch

  SetIdFragmentCurrent( id_fragment_current );

  return id_fragment_current;
}
//--------------------------------------------------------------------
private void SetIdFragmentCurrent( int id_fragment ){

  Utils.SetSharedPref(
      sp_key_id_fragment_current, id_fragment );

}
//--------------------------------------------------------------------
private boolean NavigateToFragment( int id_fragment ){

  Fragment fragment_content;
  String   tag;

  switch( id_fragment ){
    case R.id.drawer_item_track_me:
      toolbar.setTitle( R.string.fragment_track_me );
      tag = Frag_TrackMe.TAG;
      fragment_content = new Frag_TrackMe();
      break;

    case R.id.drawer_item_track_others:
      toolbar.setTitle( R.string.fragment_track_others );
      tag = Frag_TrackOthers.TAG;
      fragment_content = new Frag_TrackOthers();
      break;

    case R.id.drawer_item_users:
      toolbar.setTitle( R.string.fragment_users_title );
      tag = Frag_ListUsers.TAG;
      fragment_content = new Frag_ListUsers();
      break;

    default:
      return false;
  }//switch

  SetIdFragmentCurrent( id_fragment );
  SelectNavigationItem( id_fragment );


  android.support.v4.app.FragmentTransaction fragment_transaction;
  fragment_transaction = getSupportFragmentManager().beginTransaction();
  fragment_transaction.replace( R.id.fragment_content, fragment_content, tag );
  fragment_transaction.commitAllowingStateLoss();
  getSupportFragmentManager().executePendingTransactions();

  return true;

}
//--------------------------------------------------------------------
private void SelectNavigationItem( int id_fragment ){

  MenuItem menu_item;

  Menu menu = navigation_view.getMenu();

  if( menu == null ){
    return;
  }//if

  menu_item = menu.findItem( id_fragment );

  if( menu_item == null ){
    return;
  }//if

  menu_item.setChecked( true );
}
//--------------------------------------------------------------------

}//Act
