package com.fantomsoftware.tracker.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.fantomsoftware.tracker.R;
import com.fantomsoftware.tracker.data.AppData;

public class BehaviorFAB extends FloatingActionButton.Behavior{

  private int toolbarHeight;

  //---------------------------------------------------------------------------
  public BehaviorFAB( Context context, AttributeSet attrs ){

    super();

    //this.toolbarHeight = Utils.getToolbarHeight( context );
    this.toolbarHeight = ( int ) AppData.resources.getDimension(
        R.dimen.toolbar_height );

  }//ScrollingFABBehavior
  //---------------------------------------------------------------------------
  @Override
  public boolean layoutDependsOn(
      CoordinatorLayout parent,
      FloatingActionButton fab,
      View dependency ){

    return super.layoutDependsOn( parent, fab, dependency ) ||
        (dependency instanceof AppBarLayout);

  }//layoutDependsOn
  //---------------------------------------------------------------------------
  @Override
  public boolean onDependentViewChanged(
      CoordinatorLayout parent,
      FloatingActionButton fab,
      View dependency ){

    boolean returnValue = super.onDependentViewChanged(
        parent, fab, dependency );

    if( dependency instanceof AppBarLayout ){
      CoordinatorLayout.LayoutParams lp =
          ( CoordinatorLayout.LayoutParams ) fab.getLayoutParams();

      int fabBottomMargin = lp.bottomMargin;
      int distanceToScroll = fab.getHeight() + fabBottomMargin;
      float ratio = dependency.getY() / ( float ) toolbarHeight;
      fab.setTranslationY( -distanceToScroll * ratio );
    }//if

    return returnValue;

  }//onDependentViewChanged
  //---------------------------------------------------------------------------
}//ScrollingFABBehavior