package com.miniblas.iu.builder;

import com.miniblas.iu.renderers.SeekVariableRenderer;
import com.miniblas.iu.renderers.SwitchVariableRenderer;
import com.miniblas.iu.renderers.VisualizadorVariableRenderer;
import com.miniblas.model.variableWidgets.VariableSeekWidget;
import com.miniblas.model.variableWidgets.VariableSwitchWidget;
import com.miniblas.model.variableWidgets.VariableValueWidget;
import com.miniblas.model.variableWidgets.base.BaseVariableWidget;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collection;

import javax.inject.Inject;
/**
 *
 * @author A. Azuara
 */
public class VariableRendererBuilder extends RendererBuilder<BaseVariableWidget>{
	@Inject
	public VariableRendererBuilder(Collection<Renderer<BaseVariableWidget>> prototypes){
		super(prototypes);
	}

	@Override
	protected Class getPrototypeClass(BaseVariableWidget variable){
		Class prototypeClass=null;
		//prototypeClass = VariableRenderer.class;

		if(variable instanceof VariableSeekWidget){
			prototypeClass = SeekVariableRenderer.class;
		}else if(variable instanceof VariableSwitchWidget){
			prototypeClass = SwitchVariableRenderer.class;
		}else if(variable instanceof VariableValueWidget){
			prototypeClass = VisualizadorVariableRenderer.class;
		}

		return prototypeClass;
	}
}
