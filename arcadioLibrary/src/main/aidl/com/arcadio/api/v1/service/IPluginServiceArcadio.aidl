package com.arcadio.api.v1.service;
import com.arcadio.api.v1.service.ISessionStartedListener;
import com.arcadio.api.v1.service.ICosmeListener;
import java.util.List;
import com.arcadio.common.NamesList;
import com.arcadio.common.VariablesList;
import com.arcadio.common.ItemVariable;
import java.util.Map;
import com.arcadio.api.v1.service.ParceableAccessLevels;
import com.arcadio.api.v1.service.ParceableCosmeStates;
interface IPluginServiceArcadio {

     void addNameToBag(int sessionId, String sessionKey,String _bagName, String _name);
     void addNamesToBag(int sessionId, String sessionKey,String _bagName, inout List<String> _names);
     void blockingRead(int sessionId, String sessionKey,String _name, int _timeout);
     void blockingWrite(int sessionId, String sessionKey,String _name, double _value, int _timeout);
     void connect1(int id, ISessionStartedListener sessionListener, ICosmeListener _iCosmeListener);
     void connect2(ISessionStartedListener sessionListener, ICosmeListener _iCosmeListener, String _password, String _host, int _port);
     void createBag(int sessionId, String sessionKey,String _bagName);
     void deleteBag(int sessionId, String sessionKey,String _bagName);
     void disconnect(int sessionId, String sessionKey);
     int getBagPeriod(int sessionId, String sessionKey,String _bagName);
     List<String> getBags(int sessionId, String sessionKey);
     long getPingLatencyMs(int sessionId, String sessionKey);
     ItemVariable getVariable(int sessionId, String sessionKey,String _name);
     List<ItemVariable> getVariables(int sessionId, String sessionKey,inout List<String> _names);
     String getVersion(int sessionId, String sessionKey);
     boolean isConnected(int sessionId, String sessionKey);
     void removeNameFromBag(int sessionId, String sessionKey,String _bagName, String _name);
     void setBagPeriod(int sessionId, String sessionKey,String _bagName, int _ms);
     void setPingPeriod(int sessionId, String sessionKey,int _ms);
     void singleRead(int sessionId, String sessionKey,inout VariablesList _vars);
     void waitForLastTelegram(int sessionId, String sessionKey,int _msTimeout);
     void writeVariable1(int sessionId, String sessionKey,String _name, double _value);
     void writeVariable2(int sessionId, String sessionKey,String _name, String _value);
     void writeVariables3(int sessionId, String sessionKey,inout VariablesList _names);
     //CosmeconnectorPlus
     NamesList getNamesList(int sessionId, String sessionKey);
     void requestNamesList(int sessionId, String sessionKey);




}