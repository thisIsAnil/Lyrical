package com.infi.lyrical.adapters;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infi.lyrical.R;
import com.infi.lyrical.adapters.holders.TaskHolder;
import com.infi.lyrical.task.DownloadTask;
import com.infi.lyrical.task.TaskCategory;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.task.TaskStatus;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by INFIi on 12/1/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

    private List<DownloadTask> tasks;
    private View root;
    private HashMap<String,Res> categpryToRes;
    private @ColorRes int inProgress=R.color.colorPrimaryDark;
    private @ColorRes int completed=R.color.colorPrimaryDark;
    private @ColorRes int failed=android.R.color.holo_red_dark;
    public TaskAdapter(List<DownloadTask> tasks){
        this.tasks=tasks;
        setCategpryToRes();
    }
    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(root==null)root= LayoutInflater.from(AndroidUtils.context).inflate(R.layout.holder_task,parent,false);
        return new TaskHolder(root);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {

        if(holder==null)holder=new TaskHolder(root);
        DownloadTask task=tasks.get(position);
        String title=task.getFile()?task.getUrl():new File(task.getUrl()).getName();
        holder.title.setText(title);
        Res res=categpryToRes.get(task.getCategory());
        holder.category.setImageResource(res.iconRes);
        holder.category.setBackgroundResource(res.bgRes);
        holder.status.setText(task.getStatus());
        if(task.getStatus().equals(TaskStatus.STATUS_FAILED)||task.getStatus().equals(TaskStatus.STATUS_SAVING_ERROR)){
            holder.status.setTextColor(AndroidUtils.getColor(failed));
        }else holder.status.setTextColor(AndroidUtils.getColor(inProgress));
    }

    @Override
    public int getItemCount() {
        return tasks==null?0:tasks.size();
    }

    public void updateItem(DownloadTask downloadTask){
        for(int i=0;i<tasks.size();i++){
            if(tasks.get(i).getJobId().equals(downloadTask.getJobId())){
                tasks.remove(i);
                tasks.add(i,downloadTask);
                notifyItemChanged(i);
            }
        }
    }

    public void addItem(DownloadTask downloadTask){
        if(downloadTask!=null){
            tasks.add(0,downloadTask);
            notifyItemInserted(0);
        }
    }
    private void setCategpryToRes(){
        categpryToRes=new HashMap<>();
        categpryToRes.put(TaskCategory.CATEGORY_VIDEO,new Res(R.drawable.ic_video,R.drawable.gradient_video));
        categpryToRes.put(TaskCategory.CATEGORY_MUSIC,new Res(R.drawable.ic_music,R.drawable.gradient_music));
        categpryToRes.put(TaskCategory.CATEGORY_SPEAKPAD,new Res(R.drawable.ic_notepad,R.drawable.gradient_notepad));
        categpryToRes.put(TaskCategory.CATEGORY_WAI,new Res(R.drawable.ic_who_am_i,R.drawable.gradient_wai));
    }
    private class Res{
        private @DrawableRes int iconRes;
        private @DrawableRes int bgRes;

        Res(int iconRes, int bgRes) {
            this.iconRes = iconRes;
            this.bgRes = bgRes;
        }
    }
}
