package com.miniblas.persistence;


public interface SettingStorage {

		public boolean getPrefAutoConexion();
		
		public int getPrefAutoConexionIdProfile();
		
		public void setPrefAutoConexion(boolean _autoConexion);
		
		public void setPrefAutoConexionIdProfile(int _idProfile);
		
//		public void setPrefDefaultPort(int _port);
//		
//		public void setPrefDefaultIp(String _ip);
		
		public int getPrefDefaultPort();
		
		public String getPrefDefaultPassword();
		
		
		
}
