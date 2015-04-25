package com.miniblas.iu.builder;

import com.miniblas.iu.renderers.NewVariableRenderer;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collection;

import javax.inject.Inject;

public class NewVariableRendererBuilder extends RendererBuilder<BaseVariableWidget>{
	@Inject
	public NewVariableRendererBuilder(Collection<Renderer<BaseVariableWidget>> prototypes){
		super(prototypes);
	}

	@Override
	protected Class getPrototypeClass(BaseVariableWidget variable){
		Class prototypeClass;

		prototypeClass = NewVariableRenderer.class;

		return prototypeClass;
	}
}
