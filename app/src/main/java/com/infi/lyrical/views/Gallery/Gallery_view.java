package com.infi.lyrical.views.Gallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.infi.lyrical.R;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.util.VideoRequestHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 5/7/2017.
 */

public class Gallery_view extends FrameLayout{

    public interface Delegate{
        void onBackPressed();
        void onFilesSelected(List<String> files);
        void selectFromGallery(boolean select1);
    }
    interface ImageLoaderListener{
        void onLoaded();
    }

    Delegate delegate;

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    private RecyclerView mRvPhotos;
    private ProgressDialog mProgressDialog;

    private volatile List<String> mPhotoUris=new ArrayList<>();
    private List<Long> durations=new ArrayList<>();
    private PhotosAdapter mPhotosAdapter;

    private GridLayoutManager  childGrid;

    private boolean status[];
    private List<String> selected=new ArrayList<>();
    private boolean isSelect1=true;
    private TextView title;
    private Context context;
    private List<Long> mediaIds=new ArrayList<>();
    private int hit=0;
    public void setSelect1(boolean select1)
    {
        isSelect1 = select1;
        title.setText("Select Video's");
    }

    public Gallery_view(Context context){
        super(context);
        this.context=context;
        if(checkPermissionForExternalStorage(context))
        init(context);
        else {
            Toast.makeText(context,"Grant EXTERNAL_STORAGE_PERMISSION to proceed",Toast.LENGTH_SHORT).show();
            this.setVisibility(GONE);
        }
    }
    boolean checkPermissionForExternalStorage(Context context){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    void init(final Context context){

        try {
            LayoutParams pParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            pParams.topMargin = AndroidUtils.dp(60);

            mRvPhotos = new RecyclerView(context);
            Gallery_view.this.addView(mRvPhotos, pParams);

            View actionBar = LayoutInflater.from(context).inflate(R.layout.gallery_action_bar, null, false);
            LayoutParams aParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtils.dp(60));
            aParams.gravity = Gravity.TOP;
            Gallery_view.this.addView(actionBar, aParams);

            title = (TextView) actionBar.findViewById(R.id.action_bar_title_optimse);
            actionBar.findViewById(R.id.gallery_back).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delegate != null) delegate.onBackPressed();
                }
            });

            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);


            childGrid = new GridLayoutManager(getContext(), 3);


            loadFiles();
        }catch (Exception e){
            FileLog.write("From gallery View init" +e.getMessage());
        }

    }

    private boolean isPlaying=false;
    GalleryPlayer galleryPlayer;
    void attachPlayer(final String path){
        galleryPlayer=new GalleryPlayer(getContext());
        galleryPlayer.setHideListener(new GalleryPlayer.onHideListener() {
            @Override
            public void onHide() {
                try {
                    FileLog.write("Recieved request to hide from gallery player");
                    detachPlayer();
                }catch (Throwable t){
                    FileLog.write(t.getMessage());
                }
            }
        });
        try {

            isPlaying = true;
            addView(galleryPlayer);
            Animation animation=AnimationUtils.loadAnimation(context,R.anim.fade_in);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    try {
                        galleryPlayer.play(path);
                    }catch (Exception e){
                        FileLog.e("galleryy player",e);
                    };
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            galleryPlayer.startAnimation(animation);
        }catch (Exception e){
            FileLog.write(e.getMessage());
        }
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
    public boolean getPlaying(){return isPlaying;}

    public void stopPlaying(){
        detachPlayer();
    }
    void detachPlayer(){
        try {
            if (galleryPlayer != null) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        try {
                            isPlaying = false;
                            removeView(galleryPlayer);
                            galleryPlayer = null;
                            System.gc();

                        } catch (Exception e) {
                            FileLog.e("gallery player", e);
                        }
                        ;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                galleryPlayer.stop();
                galleryPlayer.startAnimation(animation);

            }
        }catch (Exception e){
            FileLog.e("detach player",e);
        }
    }
    void loadFiles() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String[] projection = new String[]{
                            MediaStore.Video.Media.DATA,MediaStore.Video.Media._ID,MediaStore.Video.Media.DURATION,MediaStore.Video.Media.MIME_TYPE
                    };

                    Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                    long st=System.currentTimeMillis();
                    Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Video.VideoColumns.DATE_TAKEN+" DESC");
                    mPhotoUris=new ArrayList<String>();
                    if (cursor != null) {

                        while (cursor.moveToNext()) {
                                if(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)).equals("video/mp4")) {
                                    mPhotoUris.add(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                                    mediaIds.add(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
                                    durations.add(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
                                }
                        }
                        cursor.close();
                    }
                    /*Collections.sort(mPhotoUris, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return (int)(new File(o1).lastModified()-new File(o2).lastModified());
                        }
                    });*/
                    FileLog.write("Took "+(System.currentTimeMillis()-st)+"ms to fetch images");
                }catch (Exception e){
                    FileLog.write("From gallery view asyn task "+e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                mProgressDialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    //imageLoader=new Loader(mPhotoUris);
                    mProgressDialog.dismiss();
                    status = new boolean[mPhotoUris.size()];
                    mRvPhotos.setLayoutManager(childGrid);
                    mPhotosAdapter = new PhotosAdapter();
                    mRvPhotos.setAdapter(mPhotosAdapter);
                }catch (Exception e){
                    FileLog.write("From post execute"+e.getMessage());
                }

            }
        }.execute();

    }
        public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {
            Picasso picasso;
            VideoRequestHandler handler;
            @Override
            public void onViewRecycled(PhotoViewHolder holder) {
               // holder.loader.cancel(true);
                super.onViewRecycled(holder);
            }

            View root;
            int current=0;

        public PhotosAdapter() {
            handler=new VideoRequestHandler();
            picasso=new Picasso.Builder(context)
                    .addRequestHandler(handler).build();
        }

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            root=LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_photo_grid, parent, false);
            return new PhotoViewHolder(root);
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder hld, int position) {
            try {
                if (hld == null) hld = new PhotoViewHolder(root);

                final PhotoViewHolder holder = hld;
                final int p = position;
                current = holder.getAdapterPosition();
                holder.position = holder.getAdapterPosition();
                picasso.load(handler.SCHEME_VIDEO+":"+mPhotoUris.get(position)).into(holder.ivPhoto);
                if (selected.contains(mPhotoUris.get(p))&&!isSelect1) {
                    holder.checkBox.setVisibility(VISIBLE);
                    holder.checkBox.setChecked(true);
                    holder.ivPhoto.setAlpha(0.2f);
                } else {
                    holder.checkBox.setChecked(false);
                    holder.ivPhoto.setAlpha(1.0f);
                }
                final String path=mPhotoUris.get(p);
                holder.ivPhoto.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            File f=new File(mPhotoUris.get(p));
                            if(!f.exists()){
                                Toast.makeText(context,"No Such File exist",Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(isSelect1){
                                selected.add(mPhotoUris.get(p));
                                holder.ivPhoto.setAlpha(0.2f);
                                holder.checkBox.setVisibility(VISIBLE);
                                holder.checkBox.setChecked(true);
                                if (selected.size() > 0) {
                                    if (delegate != null) delegate.onFilesSelected(selected);
                                } else
                                    Toast.makeText(context, "No video selected", Toast.LENGTH_SHORT).show();

                            }else {
                                if (!status[p]) {
                                    holder.ivPhoto.setAlpha(0.2f);
                                    holder.checkBox.setVisibility(VISIBLE);
                                    holder.checkBox.setChecked(true);
                                    selected.add(mPhotoUris.get(p));
                                } else {
                                    selected.remove(mPhotoUris.get(p));
                                    holder.ivPhoto.setAlpha(1.0f);
                                    holder.checkBox.setChecked(false);
                                }
                                status[p] = !status[p];
                            }
                            }
                        catch (Exception e) {
                            FileLog.write("From gallery child adapter" + e.getMessage());
                        }
                    }
                });
                holder.ivPhoto.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(Build.VERSION.SDK_INT>=18) {
                            holder.ivPhoto.setAlpha(0.2f);

                            attachPlayer(path);
                            AndroidUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.ivPhoto.setAlpha(1.f);
                                }
                            }, 1000);
                            return true;
                        }else return false;
                    }
                });
                holder.duration.setText(AndroidUtils.convertTime(durations.get(position)));
            }catch (Throwable t){
                FileLog.write(t.getMessage());
            }
        }
         void remove(int pos){
            mPhotoUris.remove(pos);
            notifyItemChanged(pos);
            notifyItemRangeChanged(pos,mPhotoUris.size());
        }
        @Override
        public int getItemCount() {
            return mPhotoUris.size();
        }

        public  class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPhoto;
            CheckBox checkBox;
            TextView duration;
            int position;
            //ImageAsynLoader loader;
            public PhotoViewHolder(View itemView) {
                super(itemView);
                ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
                checkBox = (CheckBox)itemView.findViewById(R.id.check_iv);
                duration = (TextView) itemView.findViewById(R.id.video_duration);
            }
        }
    }


}
