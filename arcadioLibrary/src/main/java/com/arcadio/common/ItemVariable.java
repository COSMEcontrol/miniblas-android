package com.arcadio.common;

/**
 * Created by Alberto Azuara García on 26/11/14.
 */
import android.os.Parcel;
import android.os.Parcelable;


public class ItemVariable implements Parcelable {

    /**  */
    protected String name = "";
    /**  */
    protected String type = "";
    /**  */

    protected boolean modificable = true;

    protected String unit;  // ms, cm, etc...

    protected boolean isValueNull = true;

    protected long timestamp = -1;  // contiene el instante en que se ha actualizado su valor (puede ser el mismo que había)
    protected long timestampModified = -1; // contiene el instante en que se modificó el valor (
    protected boolean isValueModified = true; // true si el nuevo valor es distinto del anterior
    // false si el nuevo y anterior valores son iguales
    /**
     *
     *
     */
    public ItemVariable(){
        this.setModificable(true);
        this.isValueModified = false;

    }

    /**
     *
     *
     */
    public ItemVariable(String _name){
        this.name = _name;
        this.setType("No Recibido");
        this.setModificable(true);
        this.isValueModified = false;
    }

    public ItemVariable(Parcel in) {
        readFromParcel(in);
    }
/*

    public ItemVariable(String que_nombre,
                        double que_valor,
                        String que_valor_txt,
                        double que_valor_inicial,
                        double que_valor_minimo,
                        double que_valor_maximo,
                        String que_unidad,
                        double que_coeficiente1,
                        double que_coeficiente2,
                        double que_coeficiente3){

        this.nombre = que_nombre;
        this.valorAnterior = this.valor;
        this.valor = que_valor;
        this.valorAnterior_txt = this.valor_txt;
        this.valor_txt=que_valor_txt;
        this.valor_inicial = que_valor_inicial;
        this.valor_minimo = que_valor_minimo;
        this.valor_maximo = que_valor_maximo;
        this.unit = que_unidad;
        this.coeficiente1 = que_coeficiente1;
        this.coeficiente2 = que_coeficiente2;
        this.coeficiente3 = que_coeficiente3;

        isValueNull = false;



        this.setTipo("No Recibido");
        this.setModificable(true);
        this.timestamp = System.currentTimeMillis();
        this.timestampModified = this.timestamp;
        this.isValueModified = true;
    }
*/
    /**
     *
     *
     */
    public String getName(){
        return name;
    }

    /**
     *
     *
     */
    @Override
    public String toString(){
        return name;
    }


    public boolean isNull(){
        return this.isValueNull;
    }

    public String getType() {
        return type;
    }

    public void setType(String _type) {
        this.type = _type;
    }

    public boolean isModificable() {
        return modificable;
    }

    public void setModificable(boolean modificable) {
        this.modificable = modificable;
    }


    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public long getTimestampModified(){
        return this.timestampModified;
    }

    public boolean isValorModificado(){
        return this.isValueModified;
    }





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(unit);
        dest.writeLong(timestamp);
        dest.writeLong(timestampModified);

        boolean[] booleanArray = new boolean[3];
        booleanArray[0] = modificable;
        booleanArray[1] = isValueNull;
        booleanArray[2] = isValueModified;
        dest.writeBooleanArray(booleanArray);
    }
    public ItemVariable readFromParcel(Parcel in) {
        this.name=in.readString();
        this.type=in.readString();
        this.unit = in.readString();
        this.timestamp = in.readLong();
        this.timestampModified = in.readLong();
        boolean[] booleanArray = new boolean[3];
        in.readBooleanArray(booleanArray);
        this.modificable=booleanArray[0];
        this.isValueNull =booleanArray[1];
        this.isValueModified=booleanArray[2];
        return this;
    }
    public static final Parcelable.Creator<ItemVariable> CREATOR
            = new Parcelable.Creator<ItemVariable>() {
        public ItemVariable createFromParcel(Parcel in) {
            return new ItemVariable(in);
        }

        public ItemVariable[] newArray(int size) {
            return new ItemVariable[size];
        }
    };
}