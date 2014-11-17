package com.miniblas.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.miniblas.iu.fragments.ProfilesElementsFragment;
import com.miniblas.iu.utils.SeleccionableRendererAdapter;
import com.miniblas.model.MiniBlasCestaCollection;
import com.miniblas.model.MiniBlasPerfilCollection;
import com.pedrogomez.renderers.Renderer;

import android.content.Context;
import android.view.LayoutInflater;

import com.miniblas.app.AplicacionPrincipal;

import com.miniblas.iu.builder.CestaRendererBuilder;
import com.miniblas.iu.builder.NewVariableRendererBuilder;
import com.miniblas.iu.builder.PerfilRendererBuilder;
import com.miniblas.iu.builder.VariableRendererBuilder;
import com.miniblas.iu.renderers.CestaRenderer;
import com.miniblas.iu.renderers.NewVariableRenderer;
import com.miniblas.iu.renderers.PerfilRenderer;
import com.miniblas.iu.renderers.VariableRenderer;
import com.miniblas.model.MiniBlasCesta;
import com.miniblas.model.MiniBlasItemVariable;
import com.miniblas.model.MiniBlasPerfil;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
		ProfilesElementsFragment.class,
		AplicacionPrincipal.class,
}, library=true)
public class RenderAdaptersModule {


    private Context context;
	
	public RenderAdaptersModule(Context context) {
        this.context = context;
    }

    @Provides
    SeleccionableRendererAdapter<MiniBlasPerfil> providePerfilRendererAdapter(LayoutInflater layoutInflater, PerfilRendererBuilder rendererBuilder) {
        MiniBlasPerfilCollection perfilCollection = new MiniBlasPerfilCollection(new ArrayList<MiniBlasPerfil>());
    	final SeleccionableRendererAdapter<MiniBlasPerfil> adapter = new SeleccionableRendererAdapter<MiniBlasPerfil>(layoutInflater, rendererBuilder, perfilCollection);
        return adapter; 
    }

    @Provides SeleccionableRendererAdapter<MiniBlasCesta> provideCestasRendererAdapter(LayoutInflater layoutInflater, CestaRendererBuilder rendererBuilder) {
        MiniBlasCestaCollection cestaCollection = new MiniBlasCestaCollection(new ArrayList<MiniBlasCesta>());
        final SeleccionableRendererAdapter<MiniBlasCesta> adapter = new SeleccionableRendererAdapter<MiniBlasCesta>(layoutInflater, rendererBuilder, cestaCollection);
        return adapter; 
    }
    /*
    @Provides @Named("provideVariablesRendererAdapter") VariablesAdapter provideVariablesRendererAdapter(LayoutInflater layoutInflater, VariableRendererBuilder rendererBuilder) {
        final List<MiniBlasItemVariable> variablesCollection = new ArrayList<MiniBlasItemVariable>();
        final VariablesAdapter adapter = new VariablesAdapter(layoutInflater, rendererBuilder, variablesCollection);     
        return adapter; 
    }
    */
    /*
    @Provides SeleccionableRendererAdapter<MiniBlasItemVariable> provideNuevaVariableRendererAdapter(LayoutInflater layoutInflater, NewVariableRendererBuilder rendererBuilder) {
        final List<MiniBlasItemVariable> variablesCollection = new ArrayList<MiniBlasItemVariable>();
        final SeleccionableRendererAdapter<MiniBlasItemVariable> adapter = new SeleccionableRendererAdapter<MiniBlasItemVariable>(layoutInflater, rendererBuilder, variablesCollection);
        return adapter; 
    }

*/
    @Provides
    LayoutInflater provideLayoutInflater() {
        return LayoutInflater.from(context);
    }
    @Provides
    PerfilRendererBuilder providePerfilRendererBuilder() {
        return new PerfilRendererBuilder(getPrototypesPerfil());
    }
    @Provides
    CestaRendererBuilder provideCestaRendererBuilder() {
        return new CestaRendererBuilder(getPrototypesCesta());
    }
    @Provides
    VariableRendererBuilder provideVariableRendererBuilder() {
        return new VariableRendererBuilder(getPrototypesVariable());
    }
    @Provides
    NewVariableRendererBuilder provideNewVariableRendererBuilder() {
        return new NewVariableRendererBuilder(getPrototypesNewVariable());
    }
    
    /**
     * Create a list of prototypes to configure RendererBuilder.
     * The list of Renderer<Video> that contains all the possible renderers that our RendererBuilder is going to use.
     *
     * @return Renderer<Video> prototypes for RendererBuilder.
     */
    private List<Renderer<MiniBlasPerfil>> getPrototypesPerfil() {
        List<Renderer<MiniBlasPerfil>> prototypes = new LinkedList<Renderer<MiniBlasPerfil>>();
        PerfilRenderer perfilrenderer = new PerfilRenderer(context);
        prototypes.add(perfilrenderer);
        return prototypes;
    }
    private List<Renderer<MiniBlasCesta>> getPrototypesCesta() {
        List<Renderer<MiniBlasCesta>> prototypes = new LinkedList<Renderer<MiniBlasCesta>>();
        CestaRenderer cestaRenderer = new CestaRenderer(context);
        prototypes.add(cestaRenderer);
        return prototypes;
    }
    private List<Renderer<MiniBlasItemVariable>> getPrototypesVariable() {
        List<Renderer<MiniBlasItemVariable>> prototypes = new LinkedList<Renderer<MiniBlasItemVariable>>();
        VariableRenderer variableRenderer = new VariableRenderer(context);
        prototypes.add(variableRenderer);
        return prototypes;
    }
    private List<Renderer<MiniBlasItemVariable>> getPrototypesNewVariable() {
        List<Renderer<MiniBlasItemVariable>> prototypes = new LinkedList<Renderer<MiniBlasItemVariable>>();
        NewVariableRenderer variableRenderer = new NewVariableRenderer(context);
        prototypes.add(variableRenderer);
        return prototypes;
    }


}
