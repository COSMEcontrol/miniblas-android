package com.arcadio.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Esta clase contiene la información asociada a cada clase que el cliente haya solicitado crear a Arcadio.
 * Cuando haya que reestablecer la comunicación, podrá recrearse la cesta existente sin que el cliente
 * deba hacer absolutamente nada.
 * @author Alberto Azuara
 */
public class Bag {
    public static final String BASKET_NAME_SEPARATOR = "-";

    private static Random r = new Random();

    private String basketName;   // prefijo de la cesta (sin el sufijo aleatorio
    private String realBasketName;    // compuesto por el prefijo más el sufijo aleatorio
    private Collection<String> nameList = new ArrayList();

    private int event_time;
    private int inhibit_time;

    public Bag(String _name, int event_time, int inhibit_time)
    {
        this.basketName = _name;

        //this.realBasketName  = _nombreAleatorio;
        this.updateRandomName();

        this.event_time = event_time;
        this.inhibit_time = inhibit_time;
    }

    /**
     * @return the prefijo
     */
    public String getBasketName()
    {
        return basketName;
    }

    public String getRealBasketName()
    {
        return realBasketName;
    }

    /**
     * @return the nameList
     */
    public Collection<String> getNameList()
    {
        return nameList;
    }

    public void setNameList(Collection<String> _listaNombres)
    {
        this.nameList = _listaNombres;
    }

    public void addName(String _name)
    {
        nameList.add(_name);
    }

    public boolean existsName(String _name)
    {
        return nameList.contains(_name);
    }

    public boolean deleteName(String _name)
    {
        return nameList.remove(_name);
    }

    /**
     * @return the event_time
     */
    public int getEventTime()
    {
        return event_time;
    }

    /**
     * @param _eventTime the event_time to set
     */
    public void setEventTime(int _eventTime)
    {
        this.event_time = _eventTime;
    }

    public int getInhibitTime()
    {
        return inhibit_time;
    }

    public void setInhibitTime(int inhibit_time)
    {
        this.inhibit_time = inhibit_time;
    }

    public void updateRandomName()
    {
        this.realBasketName = basketName + BASKET_NAME_SEPARATOR
                + Long.toString(Math.abs(r.nextInt()), 36);
    }
}