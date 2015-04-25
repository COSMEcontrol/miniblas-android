package com.miniblas.iu.builder;

import com.miniblas.iu.renderers.ProfileRenderer;
import com.miniblas.model.MiniBlasProfile;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collection;

import javax.inject.Inject;

public class ProfileRendererBuilder extends RendererBuilder<MiniBlasProfile>{


	@Inject
	public ProfileRendererBuilder(Collection<Renderer<MiniBlasProfile>> prototypes){
		super(prototypes);
	}

	@Override
	protected Class getPrototypeClass(MiniBlasProfile perfil){
		Class prototypeClass;

		prototypeClass = ProfileRenderer.class;

		return prototypeClass;
	}
}
