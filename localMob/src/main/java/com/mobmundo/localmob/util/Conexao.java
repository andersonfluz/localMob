package com.mobmundo.localmob.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class Conexao {

	Context context;
	public Conexao(Context ctx){
		context = ctx;
	}	
	
	public boolean verificaConexao() {  
	    boolean conectado;  
	    ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if (conectivtyManager.getActiveNetworkInfo() != null  
	            && conectivtyManager.getActiveNetworkInfo().isAvailable()  
	            && conectivtyManager.getActiveNetworkInfo().isConnected()) {  
	        conectado = true;  
	    } else {
	        conectado = false;  
	    }  
	    return conectado;  
	}
	
	public static boolean wifiIsConnected(Context context){
	    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
	    return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting(); 
	}
}
