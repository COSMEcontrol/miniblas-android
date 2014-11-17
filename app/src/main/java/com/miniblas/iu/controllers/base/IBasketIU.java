package com.miniblas.iu.controllers.base;


import com.miniblas.model.MiniBlasCesta;

public interface IBasketIU {
    public void actualizarLista();
    public void setSubtitle(final String title);
    //public SeleccionableRenderAdapter<MiniBlasCesta> getAdapter();
    public void addElementAdapter(final MiniBlasCesta basket);
    public void setConnectIcon();
    public void setDisconnectIcon();
    public void msgSaveEditBasket();
	public void msgSaveNewBasket();
	public void msgCancelNewBasket();
	public void msgCancelEditBascket();
	public void msgErrorAccessBD();
	public void msgErrorSaveProfiles();
	public void setChekedAutoConect();
	public void setNotChekedAutoConect();
	public void showIconLoading();
	public void dismissIconLoading();
	public void recuperarEstado();

}
