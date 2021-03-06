package com.fantomsoftware.tracker.cmps;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;


public class CmpCircleAvatar extends ImageView{
  //------------------------------------------------------------------
  static{
    SCALE_TYPE = ScaleType.CENTER_CROP;
    BITMAP_CONFIG = Config.ARGB_8888;
  }

  private static final ScaleType SCALE_TYPE;
  private static final Config BITMAP_CONFIG;

  private static final int COLORDRAWABLE_DIMENSION = 2;
  private static final int DEFAULT_BORDER_WIDTH = 0;
  private static final int DEFAULT_BORDER_COLOR = -16777216;
  private static final boolean DEFAULT_BORDER_OVERLAY = false;

  private final RectF mDrawableRect;
  private final RectF mBorderRect;
  private final Matrix mShaderMatrix;
  private final Paint mBitmapPaint;
  private final Paint mBorderPaint;
  private int mBorderColor;
  private int mBorderWidth;
  private Bitmap mBitmap;
  private BitmapShader mBitmapShader;
  private ColorFilter mColorFilter;
  private int mBitmapWidth;
  private int mBitmapHeight;
  private float mDrawableRadius;
  private float mBorderRadius;
  private boolean mReady;
  private boolean mSetupPending;
  private boolean mBorderOverlay;
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public CmpCircleAvatar( Context context ){
    super( context );
    this.mDrawableRect = new RectF();
    this.mBorderRect = new RectF();
    this.mShaderMatrix = new Matrix();
    this.mBitmapPaint = new Paint();
    this.mBorderPaint = new Paint();
    this.mBorderColor = -16777216;
    this.mBorderWidth = 0;
    this.init();
  }//CmpCircleAvatar
  //------------------------------------------------------------------
  public CmpCircleAvatar( Context context, AttributeSet attrs ){
    this( context, attrs, 0 );
  }//CmpCircleAvatar
  //------------------------------------------------------------------
  public CmpCircleAvatar( Context context, AttributeSet attrs, int defStyle ){
    super( context, attrs, defStyle );
    this.mDrawableRect = new RectF();
    this.mBorderRect = new RectF();
    this.mShaderMatrix = new Matrix();
    this.mBitmapPaint = new Paint();
    this.mBorderPaint = new Paint();
    this.mBorderColor = -16777216;
    this.mBorderWidth = 0;
        /*
        TypedArray a = context.obtainStyledAttributes(attrs, styleable.CircleImageView, defStyle, 0);
        this.mBorderWidth = a.getDimensionPixelSize(styleable.CircleImageView_border_width, 0);
        this.mBorderColor = a.getColor(styleable.CircleImageView_border_color, -16777216);
        this.mBorderOverlay = a.getBoolean(styleable.CircleImageView_border_overlay, false);
        a.recycle();
        */

    this.mBorderWidth = 8;
    this.mBorderColor = Color.parseColor( "#ffffff" );
    this.mBorderOverlay = true;

    this.init();
  }//CmpCircleAvatar
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  private void init(){
    super.setScaleType( SCALE_TYPE );
    this.mReady = true;
    if( this.mSetupPending ){
      this.setup();
      this.mSetupPending = false;
    }

  }//init
  //------------------------------------------------------------------
  private void setup(){
    if( !this.mReady ){
      this.mSetupPending = true;
    }else if( this.mBitmap != null ){
      this.mBitmapShader = new BitmapShader( this.mBitmap, TileMode.CLAMP, TileMode.CLAMP );
      this.mBitmapPaint.setAntiAlias( true );
      this.mBitmapPaint.setShader( this.mBitmapShader );
      this.mBorderPaint.setStyle( Style.STROKE );
      this.mBorderPaint.setAntiAlias( true );
      this.mBorderPaint.setColor( this.mBorderColor );
      this.mBorderPaint.setStrokeWidth( ( float ) this.mBorderWidth );
      this.mBitmapHeight = this.mBitmap.getHeight();
      this.mBitmapWidth = this.mBitmap.getWidth();
      this.mBorderRect.set( 0.0F, 0.0F, ( float ) this.getWidth(), ( float ) this.getHeight() );
      this.mBorderRadius = Math.min( (this.mBorderRect.height() - ( float ) this.mBorderWidth) / 2.0F, (this.mBorderRect.width() - ( float ) this.mBorderWidth) / 2.0F );
      this.mDrawableRect.set( this.mBorderRect );
      if( !this.mBorderOverlay ){
        this.mDrawableRect.inset( ( float ) this.mBorderWidth, ( float ) this.mBorderWidth );
      }

      this.mDrawableRadius = Math.min( this.mDrawableRect.height() / 2.0F, this.mDrawableRect.width() / 2.0F );
      this.updateShaderMatrix();
      this.invalidate();
    }
  }//setup
  //------------------------------------------------------------------
  private void updateShaderMatrix(){
    float dx = 0.0F;
    float dy = 0.0F;
    this.mShaderMatrix.set( ( Matrix ) null );
    float scale;
    if( ( float ) this.mBitmapWidth * this.mDrawableRect.height() > this.mDrawableRect.width() * ( float ) this.mBitmapHeight ){
      scale = this.mDrawableRect.height() / ( float ) this.mBitmapHeight;
      dx = (this.mDrawableRect.width() - ( float ) this.mBitmapWidth * scale) * 0.5F;
    }else{
      scale = this.mDrawableRect.width() / ( float ) this.mBitmapWidth;
      dy = (this.mDrawableRect.height() - ( float ) this.mBitmapHeight * scale) * 0.5F;
    }

    this.mShaderMatrix.setScale( scale, scale );
    this.mShaderMatrix.postTranslate( ( float ) (( int ) (dx + 0.5F)) + this.mDrawableRect.left, ( float ) (( int ) (dy + 0.5F)) + this.mDrawableRect.top );
    this.mBitmapShader.setLocalMatrix( this.mShaderMatrix );
  }//updateShaderMatrix
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public boolean isBorderOverlay(){
    return this.mBorderOverlay;
  }//isBorderOverlay
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  public ScaleType getScaleType(){
    return SCALE_TYPE;
  }//getScaleType
  //------------------------------------------------------------------
  public int getBorderColor(){
    return this.mBorderColor;
  }//getBorderColor
  //------------------------------------------------------------------
  public int getBorderWidth(){
    return this.mBorderWidth;
  }//getBorderWidth
  //------------------------------------------------------------------
  private Bitmap getBitmapFromDrawable( Drawable drawable ){
    if( drawable == null ){
      return null;
    }else if( drawable instanceof BitmapDrawable ){
      return (( BitmapDrawable ) drawable).getBitmap();
    }else{
      try{
        Bitmap e;
        if( drawable instanceof ColorDrawable ){
          e = Bitmap.createBitmap( 2, 2, BITMAP_CONFIG );
        }else{
          e = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG );
        }

        Canvas canvas = new Canvas( e );
        drawable.setBounds( 0, 0, canvas.getWidth(), canvas.getHeight() );
        drawable.draw( canvas );
        return e;
      }catch( OutOfMemoryError var4 ){
        return null;
      }
    }
  }//getBitmapFromDrawable
  //------------------------------------------------------------------

  //------------------------------------------------------------------
  public void setScaleType( ScaleType scaleType ){
    if( scaleType != SCALE_TYPE ){
      throw new IllegalArgumentException( String.format( "ScaleType %s not supported.", new Object[]{scaleType} ) );
    }
  }//setScaleType
  //------------------------------------------------------------------
  public void setAdjustViewBounds( boolean adjustViewBounds ){
    if( adjustViewBounds ){
      throw new IllegalArgumentException( "adjustViewBounds not supported." );
    }
  }//setAdjustViewBounds
  //------------------------------------------------------------------
  public void setBorderColor( int borderColor ){
    if( borderColor != this.mBorderColor ){
      this.mBorderColor = borderColor;
      this.mBorderPaint.setColor( this.mBorderColor );
      this.invalidate();
    }
  }//setBorderColor
  //------------------------------------------------------------------
  public void setBorderColorResource( int borderColorRes ){
    this.setBorderColor( this.getContext().getResources().getColor( borderColorRes ) );
  }//setBorderColorResource
  //------------------------------------------------------------------
  public void setBorderWidth( int borderWidth ){
    if( borderWidth != this.mBorderWidth ){
      this.mBorderWidth = borderWidth;
      this.setup();
    }
  }//setBorderWidth
  //------------------------------------------------------------------
  public void setBorderOverlay( boolean borderOverlay ){
    if( borderOverlay != this.mBorderOverlay ){
      this.mBorderOverlay = borderOverlay;
      this.setup();
    }
  }//setBorderOverlay
  //------------------------------------------------------------------

  //------------------------------------------------------------------
  public void setImageBitmap( Bitmap bm ){
    super.setImageBitmap( bm );
    this.mBitmap = bm;
    this.setup();
  }//setImageBitmap
  //------------------------------------------------------------------
  public void setImageDrawable( Drawable drawable ){
    super.setImageDrawable( drawable );
    this.mBitmap = this.getBitmapFromDrawable( drawable );
    this.setup();
  }//setImageDrawable
  //------------------------------------------------------------------
  public void setImageResource( int resId ){
    super.setImageResource( resId );
    this.mBitmap = this.getBitmapFromDrawable( this.getDrawable() );
    this.setup();
  }//setImageResource
  //------------------------------------------------------------------
  public void setImageURI( Uri uri ){
    super.setImageURI( uri );
    this.mBitmap = this.getBitmapFromDrawable( this.getDrawable() );
    this.setup();
  }//setImageURI
  //------------------------------------------------------------------
  public void setColorFilter( ColorFilter cf ){
    if( cf != this.mColorFilter ){
      this.mColorFilter = cf;
      this.mBitmapPaint.setColorFilter( this.mColorFilter );
      this.invalidate();
    }
  }//setColorFilter
  //------------------------------------------------------------------


  //------------------------------------------------------------------
  protected void onDraw( Canvas canvas ){
    if( this.getDrawable() != null ){
      canvas.drawCircle( ( float ) (this.getWidth() / 2), ( float ) (this.getHeight() / 2), this.mDrawableRadius, this.mBitmapPaint );
      if( this.mBorderWidth != 0 ){
        canvas.drawCircle( ( float ) (this.getWidth() / 2), ( float ) (this.getHeight() / 2), this.mBorderRadius, this.mBorderPaint );
      }
    }
  }//onDraw
  //------------------------------------------------------------------
  protected void onSizeChanged( int w, int h, int oldw, int oldh ){
    super.onSizeChanged( w, h, oldw, oldh );
    this.setup();
  }//onSizeChanged
  //------------------------------------------------------------------

}//CmpCircleAvatar