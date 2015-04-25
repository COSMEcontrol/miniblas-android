package com.arcadio.common;

/**
 * Created by Alberto Azuara García on 26/11/14.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class NumericVariable  extends ItemVariable implements Parcelable{

    /**  */
    private double valor = Double.NaN;
    private double lastValue = Double.NaN; // permite saber si el nuevo valor es distinto del que contenía anteriormente

    /**  */
    private double initial_value = Double.NaN;
    /**  */
    private double minimal_value = Double.NaN;
    /**  */
    private double maximal_value = Double.NaN;

    /**  */
    private double coefficient1 = Double.NaN;
    /**  */
    private double coefficient2 = Double.NaN;
    /**  */
    private double coefficient3 = Double.NaN;


    /**
     *
     *
     */
    public NumericVariable(String _name, double _value){
        super(_name);
        this.lastValue = this.valor;
        this.valor = _value;
    }

    public NumericVariable(Parcel in) {
        readFromParcel(in);
    }

    /**
     *
     *
     */
    public double getValue () {
        return valor;
    }

    /**
     *
     *
     */
    public void setValor (double v) {
        this.lastValue = this.valor;
        valor = v;
        this.isValueNull = false;
        this.timestamp = System.currentTimeMillis();
        if (this.lastValue != this.valor){
            this.timestampModified = this.timestamp;
            this.isValueModified = true;
        }else{
            this.isValueModified = false;
        }
    }

    /**
     *
     *
     */
    public double getInitial_value(){
        return initial_value;
    }

    /**
     *
     *
     */
    public double getMinimal_value(){
        return minimal_value;
    }

    /**
     *
     *
     */
    public double getMaximal_value(){
        return maximal_value;
    }

    /**
     *
     *
     */
    public double getCoefficient1(){
        return coefficient1;
    }

    /**
     *
     *
     */
    public double getCoefficient2(){
        return coefficient2;
    }

    /**
     *
     *
     */
    public double getCoefficient3(){
        return coefficient3;
    }





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(valor);
        dest.writeDouble(lastValue);
        dest.writeDouble(initial_value);
        dest.writeDouble(minimal_value);
        dest.writeDouble(maximal_value);
        dest.writeDouble(coefficient1);
        dest.writeDouble(coefficient2);
        dest.writeDouble(coefficient3);
    }
    @Override
    public NumericVariable readFromParcel(Parcel in) {
        super.readFromParcel(in);
        this.valor=in.readDouble();
        this.lastValue =in.readDouble();
        this.initial_value = in.readDouble();
        this.minimal_value = in.readDouble();
        this.maximal_value = in.readDouble();
        this.coefficient1 = in.readDouble();
        this.coefficient2 = in.readDouble();
        this.coefficient3 = in.readDouble();
        return this;
    }

    public static final Creator<NumericVariable> CREATOR
            = new Creator<NumericVariable>() {
        public NumericVariable createFromParcel(Parcel in) {
            return new NumericVariable(in);
        }

        public NumericVariable[] newArray(int size) {
            return new NumericVariable[size];
        }
    };
}