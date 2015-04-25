package com.arcadio.api.v1.service;

import android.os.Parcel;
import android.os.Parcelable;


public class ParceableCosmeStates implements Parcelable{
		
		private CosmeStates state = CosmeStates.DISCONNECTED;
		
		
		public CosmeStates getState(){
			return state;
		}
		public ParceableCosmeStates(Parcel in){
			readFromParcel(in);
		}
		public ParceableCosmeStates(CosmeStates _estado){
			this.state =_estado;
		}
	    
	    public int describeContents() {
			return 0;
		}
	    public void readFromParcel(Parcel in) {
	        String entrada = in.readString();
	        state = CosmeStates.valueOf(entrada);
	    }
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(state.toString());
		}
		public static final Parcelable.Creator<ParceableCosmeStates> CREATOR
	    = new Parcelable.Creator<ParceableCosmeStates>() {
	        public ParceableCosmeStates createFromParcel(Parcel in) {
	            return new ParceableCosmeStates(in);
	        }
	 
	        public ParceableCosmeStates[] newArray(int size) {
	            return new ParceableCosmeStates[size];
	        }
	    };
	}
	

