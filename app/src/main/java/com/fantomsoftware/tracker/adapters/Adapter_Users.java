package com.fantomsoftware.tracker.adapters;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.data.User;
import com.fantomsoftware.tracker.interfaces.OnResultId;


public class Adapter_Users
    extends RecyclerView.Adapter<Adapter_Users.ViewHolder>{

  //------------------------------------------------------------------
  private SparseArray<User> data;

  private OnResultId on_item_click = null;
  private OnResultId on_item_click_long = null;
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public void SetOnListItemClick( OnResultId on_item_click ){
    this.on_item_click = on_item_click;
  }//SetOnListItemClick
  //------------------------------------------------------------------
  public void SetOnListItemClickLong( OnResultId on_item_long_click ){
    this.on_item_click_long = on_item_long_click;
  }//SetOnListItemClickLong
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public Adapter_Users( SparseArray<User> data ){

    this.data = data;

  }//RecycleA_Currencies
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  @Override
  public Adapter_Users.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType ){


    View.OnClickListener cl_item = new View.OnClickListener(){
      @Override
      public void onClick( View view ){
        int position = ( int ) view.getTag();
        User item = data.get( data.keyAt( position ) );

        if( on_item_click != null ){
          on_item_click.OnResult( item.id );
        }//if
      }//onClick
    };//cl_item


    View.OnLongClickListener lcl_item = new View.OnLongClickListener(){
      @Override
      public boolean onLongClick( View view ){
        int position = ( int ) view.getTag();
        User item = data.get( data.keyAt( position ) );

        if( on_item_click_long != null ){
          on_item_click_long.OnResult( item.id );
        }//if
        return false;
      }
    };//lcl_item


    LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
    View view = inflater.inflate( R.layout.list_item_user, null );

    view.setOnClickListener( cl_item );
    view.setOnLongClickListener( lcl_item );


    RelativeLayout rl_view = ( RelativeLayout )
        view.findViewById( R.id.root_view );

    TextView tv_name = ( TextView )
        view.findViewById( R.id.tv_name );

    return new ViewHolder( rl_view, tv_name );

  }//onCreateViewHolder
  //------------------------------------------------------------------
  @Override
  public void onBindViewHolder( ViewHolder holder, int position ){

    User item = data.get( data.keyAt( position ) );

    holder.root_view.setTag( position );
    holder.tv_name.setText( item.name );

  }//onBindViewHolder
  //------------------------------------------------------------------
  @Override
  public int getItemCount(){

    if( data == null ){
      return 0;
    }//if

    return data.size();

  }//getItemCount
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public void Update(){

    notifyDataSetChanged();

  }//Update
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public static class ViewHolder extends RecyclerView.ViewHolder{
    //----------------------------------------------------------------
    public RelativeLayout root_view;
    public TextView tv_name;
    //----------------------------------------------------------------
    public ViewHolder( RelativeLayout root_view, TextView tv_name ){

      super( root_view );

      this.root_view = root_view;
      this.tv_name = tv_name;
    }//ViewHolder
  }//ViewHolder
  //------------------------------------------------------------------

}//Adapter_Users
