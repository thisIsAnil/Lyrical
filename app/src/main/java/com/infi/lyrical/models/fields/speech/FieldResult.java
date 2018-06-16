package com.infi.lyrical.models.fields.speech;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by INFIi on 11/26/2017.
 */

public class FieldResult {


    @SerializedName("source_information")
    @Expose
    private SourceInformation sourceInformation;

    @SerializedName("items")
    @Expose
    private List<SpeechData> data=null;

    public FieldResult() {

    }

    public FieldResult(SourceInformation sourceInformation, List<SpeechData> data) {
        this.sourceInformation = sourceInformation;
        this.data = data;
    }

    public SourceInformation getSourceInformation() {
        return sourceInformation;
    }

    public void setSourceInformation(SourceInformation sourceInformation) {
        this.sourceInformation = sourceInformation;
    }



    public List<SpeechData> getData() {
        return data;
    }

    public void setData(List<SpeechData> data) {
        this.data = data;
    }

    //change it late to return all fields
    @Override
    public String toString() {
        String res="result: {"+" items: [";
        if(data!=null){
            for(SpeechData sd:data)res+=sd.getContent()+" ";
        }
        res+=" ]";
        return res;
    }
}
