package com.arcadio.common;

/**
 * Created by Alberto Azuara García on 26/11/14.
 */
import android.os.Parcel;


public class TextVariable extends ItemVariable{


    private String value = null;
    private String lastValue = null; //permite saber si el nuevo valor es distinto del que contenía anteriormente

    /**
     *
     *
     */
    public TextVariable(String _name, String _value){
        super(_name);
        this.lastValue = this.value;
        this.value = _value;
    }


    public TextVariable(Parcel in) {
        readFromParcel(in);
    }

    /**
     *
     *
     */
    public String getValue(){
        return value;
    }

    /**
     *
     *
     */
    public void setValue(String _value){
        this.lastValue = this.value;
        value = _value;
        this.isValueNull = false;
        this.timestamp = System.currentTimeMillis();
        if ((this.value != null) &&(this.lastValue !=null)){
            if ( ! this.value.equals(this.lastValue)){
                this.timestampModified = this.timestamp;
                this.isValueModified = true;
            }else{
                this.isValueModified = false;
            }
        }else{

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(value);
        dest.writeString(lastValue);
    }
    @Override
    public TextVariable readFromParcel(Parcel in) {
        super.readFromParcel(in);
        this.value = in.readString();
        this.lastValue = in.readString();
        return this;
    }
    public static final Creator<TextVariable> CREATOR
            = new Creator<TextVariable>() {
        public TextVariable createFromParcel(Parcel in) {
            return new TextVariable(in);
        }

        public TextVariable[] newArray(int size) {
            return new TextVariable[size];
        }
    };
}