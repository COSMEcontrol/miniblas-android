package com.miniblas.iu.builder;

import com.miniblas.iu.renderers.BagRenderer;
import com.miniblas.model.MiniBlasBag;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collection;

import javax.inject.Inject;

public class BagRendererBuilder extends RendererBuilder<MiniBlasBag>{
	@Inject
	public BagRendererBuilder(Collection<Renderer<MiniBlasBag>> prototypes){
		super(prototypes);
	}

	@Override
	protected Class getPrototypeClass(MiniBlasBag cesta){
		Class prototypeClass;

		prototypeClass = BagRenderer.class;

		return prototypeClass;
	}
}
