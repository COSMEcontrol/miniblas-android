package com.miniblas.iu.builder;

import com.miniblas.iu.renderers.NewVariableRenderer;
import com.miniblas.model.MiniBlasItemVariable;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collection;

import javax.inject.Inject;

public class NewVariableRendererBuilder extends RendererBuilder<MiniBlasItemVariable>{
	@Inject
	public NewVariableRendererBuilder(Collection<Renderer<MiniBlasItemVariable>> prototypes){
		super(prototypes);
	}

	@Override
	protected Class getPrototypeClass(MiniBlasItemVariable variable){
		Class prototypeClass;

		prototypeClass = NewVariableRenderer.class;

		return prototypeClass;
	}
}
