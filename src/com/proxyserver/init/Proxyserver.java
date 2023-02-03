package com.proxyserver.init;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxyserver {
	
	public static void main(String args[]) {
		ServerSocket serversocket = null;
		
		try {
			 serversocket = new ServerSocket(8088); // Inicie el servidor para monitorear el puerto 8080
			System.out.println("Inicio del servidor ...");
			while (true) {
				Socket socket = serversocket.accept(); // Esperando la conexión del cliente
				Process p = new Process(socket);
				Thread t = new Thread(p);
				t.start();

			}
		} catch (Exception e) {
			System.out.println(e);
			
		}
		finally {
			try {
				serversocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}


