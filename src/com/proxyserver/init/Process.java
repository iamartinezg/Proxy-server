package com.proxyserver.init;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Process implements Runnable {
	
	public String log_access_filename = "access.log";
	private BufferedWriter accessLogFile;
	
	Socket socket;

	public Process(Socket s1) {
		socket = s1;

		try {
			accessLogFile = new BufferedWriter(new FileWriter(log_access_filename, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// Obtener entrada de Socket
			PrintStream out = new PrintStream(socket.getOutputStream()); // flujo de salida
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Obtener flujo de entrada
			Boolean keepAlive = socket.getKeepAlive();
			int port = socket.getPort();
			
			
			String info = in.readLine();
			// Analiza la cadena de solicitud y obtén la URL
			System.out.println("Mensaje obtenido: " + info);
			System.out.println("Proxy-Connection: Keep-Alive " + keepAlive);
			System.out.println("Port: " + port);
			
			
			writeLog("Mensaje obtenido: "+info, true);
			writeLog("Proxy-Connection: Keep-Alive " + keepAlive, true);
			writeLog("Port: " + port, true);
			
			
			
			
			String gotourl = validarSitiosVirtuales(info);
			System.out.println("Host: " + gotourl);
			System.out.println("Conectando a " + gotourl);
			
			
			writeLog("MensajeFinal : GET " + gotourl, true);
			
			URL con = new URL(gotourl);
			InputStream gotoin = con.openStream(); // Leer recursos de red
			int n = gotoin.available(); // devuelve el tamaño total de la página
			//out.println("HTTP/1.0 200 OK");
			//out.println("MIME_version:1.0");
			//out.println("Content_Type:text/html");
			//out.println("Content_Length:" + n);
			//out.println(" ");
			byte buf[] = new byte[1024];

			while ((n = gotoin.read(buf)) >= 0) {
				out.write(buf, 0, n);
			}
			out.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Exception:" + e);
		}
	}
	
String validarSitiosVirtuales(String mensajeIn) {
		
		
		// carga en una lista de string la info del archivo 
		List<String> virtualSities = new ArrayList<String>();
		  try {
		      File myObj = new File("D:\\U\\Redes\\ServerProxy\\servidoresvirtuales");
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        //System.out.println(data);
		        virtualSities.add(data);
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
		//Verifica si el mensaje que llega es get o post
		//
		if(mensajeIn.contains("GET")||mensajeIn.contains("POST")) {
			String[] data = mensajeIn.split(" ");
			int pos=  data[1].indexOf(".com");
			int length = data[1].length();
			String restURL="";
			if(length>pos+4) {
				restURL = data[1].substring(pos+4,data[1].length());
			}
			else {
				restURL = data[1];
			}
			//recorre la lista con la info del archivo para verificar si el host se encuentra en el archivo
			for (String itemSitioViritua : virtualSities) {
				String[] items = itemSitioViritua.split(" ");
				if(data[1].contains(items[0])){//si se encuentra se cambia el host
					System.out.println("DATA por procesar"+ restURL );
					//return data[1]; //"http://"+data[1]; //+"/"+items[2];
					return "http://"+items[1]	+restURL;
				}
			}
			
			return data[1];
		}
		else {
			String[] data = mensajeIn.split(" ");
			
			return "https://"+data[1];//.substring(0, data[1].length()-4);
		}
		
		//return "";
		
	}
	//Escribe en el archivo de log la informacion 
	public void writeLog(String s, boolean new_line) {
		try {
			s = new Date().toString() + " " + s;
			accessLogFile.write(s, 0, s.length());
			if (new_line)
				accessLogFile.newLine();
				accessLogFile.flush();
			//if (debug)
				//System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}