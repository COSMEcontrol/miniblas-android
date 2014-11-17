package com.miniblas.iu.builder;

import java.util.Collection;

import javax.inject.Inject;

import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import com.miniblas.iu.renderers.CestaRenderer;
import com.miniblas.model.MiniBlasCesta;

public class CestaRendererBuilder extends RendererBuilder<MiniBlasCesta>{
		@Inject
		public CestaRendererBuilder(Collection<Renderer<MiniBlasCesta>> prototypes) {
			super(prototypes);
		}

		@Override
		protected Class getPrototypeClass(MiniBlasCesta cesta) {
			 Class prototypeClass;
		        
		            prototypeClass = CestaRenderer.class;
		        
		        return prototypeClass;
		}
}
