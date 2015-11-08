package com.miniblas.iu.controllers.base;

import android.util.SparseBooleanArray;

import java.io.IOException;
import java.io.Serializable;
/**
 *
 * @author A. Azuara
 */
public class SerializableSparseBooleanArrayContainer implements Serializable{

	private static final long serialVersionUID = 393662066105575556L;
	private SparseBooleanArray mSparseArray = new SparseBooleanArray();

	public SerializableSparseBooleanArrayContainer(SparseBooleanArray mDataArray){
		this.mSparseArray = mDataArray;
	}

	public SparseBooleanArray getSparseArray(){
		return mSparseArray;
	}

	public void setSparseArray(SparseBooleanArray sparseArray){
		this.mSparseArray = sparseArray;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException{
		out.writeLong(serialVersionUID);
		int sparseArraySize = 0;
		if(mSparseArray != null){
			sparseArraySize = mSparseArray.size();
		}
		out.write(sparseArraySize);
		if(mSparseArray != null){
			for(int i = 0; i < sparseArraySize; i++){
				int key = mSparseArray.keyAt(i);
				out.writeInt(key);
				boolean value = mSparseArray.get(key);
				out.writeBoolean(value);
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
		long readSerialVersion = in.readLong();
		if(readSerialVersion != serialVersionUID){
			throw new IOException("serial version mismatch");
		}
		int sparseArraySize = in.read();
		mSparseArray = new SparseBooleanArray(sparseArraySize);
		for(int i = 0; i < sparseArraySize; i++){
			int key = in.readInt();
			boolean value = in.readBoolean();
			mSparseArray.put(key, value);
		}
	}

}
	
