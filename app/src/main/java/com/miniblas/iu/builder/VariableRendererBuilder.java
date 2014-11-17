package com.miniblas.iu.builder;

import java.util.Collection;

import javax.inject.Inject;

import com.miniblas.iu.renderers.CestaRenderer;
import com.miniblas.iu.renderers.VariableRenderer;
import com.miniblas.model.MiniBlasItemVariable;

import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;
public class VariableRendererBuilder extends RendererBuilder<MiniBlasItemVariable>{
	@Inject
	public VariableRendererBuilder(Collection<Renderer<MiniBlasItemVariable>> prototypes) {
		super(prototypes);
	}

	@Override
	protected Class getPrototypeClass(MiniBlasItemVariable variable) {
		 Class prototypeClass;
	        
	            prototypeClass = VariableRenderer.class;
	        
	        return prototypeClass;
	}
}
