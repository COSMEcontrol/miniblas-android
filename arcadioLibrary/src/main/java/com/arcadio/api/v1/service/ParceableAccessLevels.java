package com.arcadio.api.v1.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.arcadio.common.AccessLevels;


public class ParceableAccessLevels implements Parcelable{
    
    private AccessLevels state = AccessLevels.USER;
    
    
    public AccessLevels getState(){
        return state;
    }
    public ParceableAccessLevels(Parcel in){
        readFromParcel(in);
    }
    public ParceableAccessLevels(AccessLevels _estado){
        this.state =_estado;
    }
    
    public int describeContents() {
        return 0;
    }
    public void readFromParcel(Parcel in) {
        String entrada = in.readString();
        state = AccessLevels.valueOf(entrada);
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(state.toString());
    }
    public static final Creator<ParceableAccessLevels> CREATOR
    = new Creator<ParceableAccessLevels>() {
        public ParceableAccessLevels createFromParcel(Parcel in) {
            return new ParceableAccessLevels(in);
        }
 
        public ParceableAccessLevels[] newArray(int size) {
            return new ParceableAccessLevels[size];
        }
    };
}
	

