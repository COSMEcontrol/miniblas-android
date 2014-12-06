package com.miniblas.iu.builder;

import com.miniblas.iu.renderers.CestaRenderer;
import com.miniblas.model.MiniBlasCesta;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collection;

import javax.inject.Inject;

public class CestaRendererBuilder extends RendererBuilder<MiniBlasCesta>{
	@Inject
	public CestaRendererBuilder(Collection<Renderer<MiniBlasCesta>> prototypes){
		super(prototypes);
	}

	@Override
	protected Class getPrototypeClass(MiniBlasCesta cesta){
		Class prototypeClass;

		prototypeClass = CestaRenderer.class;

		return prototypeClass;
	}
}
