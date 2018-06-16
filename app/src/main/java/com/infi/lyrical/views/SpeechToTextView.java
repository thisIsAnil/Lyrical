package com.infi.lyrical.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.infi.lyrical.R;
import com.infi.lyrical.helper.FileLog;
import com.infi.lyrical.helper.SentimentListener;
import com.infi.lyrical.models.SentimentModel;
import com.infi.lyrical.models.SpeechResponse;
import com.infi.lyrical.task.DownloadTask;
import com.infi.lyrical.task.TaskCategory;
import com.infi.lyrical.util.AndroidUtils;
import com.infi.lyrical.util.LanguageModel;
import com.infi.lyrical.util.TempFolderMaker;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INFIi on 12/2/2017.
 */

public class SpeechToTextView extends FrameLayout {
    @BindView(R.id.converted_text)
    TextView convertedText;
    @BindView(R.id.text_title_bar)
    TextView titlebar;
    private View root;
    private ProgressDialog progressDialog;
    public SpeechToTextView(Context context){
        super(context);
        root= LayoutInflater.from(context).inflate(R.layout.speech_to_text_layout,null,false);
        addView(root);
        ButterKnife.bind(this);
        progressDialog=new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(AndroidUtils.getString(R.string.analysing));
        progressDialog.setMessage(AndroidUtils.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void setTitle(String title){
        titlebar.setText(title);
    }
    public void onSpeechRecognizationComplete(DownloadTask downloadTask) throws IOException{
        SpeechResponse speechResponse=FileLog.getSpeechResponseForFile(downloadTask.getUrl());
        String content=AndroidUtils.getContentFromSpeechResponse(speechResponse);
        if(downloadTask.getCategory().equals(TaskCategory.CATEGORY_SPEAKPAD)){
            File nf=new File(TempFolderMaker.getTexts(),System.currentTimeMillis()+".txt");
            FileLog.writeToFile(content,nf.getAbsolutePath());
            setConvertedText(content);
        }
        File f=new File(TempFolderMaker.getSentiments(),System.currentTimeMillis()+".txt");
        FileLog.writeToFile(content,f.getAbsolutePath());
        AndroidUtils.requestSentimentForFile(f.getPath(), LanguageModel.LANGUAGE_US, new SentimentListener() {
            @Override
            public void onRecievedSentiment(SentimentModel model) {
                setConvertedText(model.getAggregate().getSentiment().toString());
            }

            @Override
            public void onFailed() {
                setConvertedText(AndroidUtils.getString(R.string.failed_to_analyse_sentiment));
            }
        });

    }
    public void setConvertedText(String text){
        progressDialog.dismiss();
        convertedText.setText(text);
    }

}
