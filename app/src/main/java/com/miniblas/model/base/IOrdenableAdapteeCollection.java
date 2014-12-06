package com.miniblas.model.base;

/**
 * Created by alberto on 19/11/14.
 */

import com.pedrogomez.renderers.AdapteeCollection;


/**
 * Interface created to represent the adaptee collection used in RendererAdapter. RendererAdapter
 * will be created with a RendererBuilder and an AdapteeCollection that store all the content to
 * show in a list view.
 *
 * @author Alberto Azuara García.
 */
public interface IOrdenableAdapteeCollection <T> extends AdapteeCollection<T>{
	public void add(int index, T element);

	public int indexOf(T variable);
}


