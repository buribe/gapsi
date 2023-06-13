package com.gapsi.ecommerce.controlador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gapsi.ecommerce.dto.LoginDto;

public class LoginControlador extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String strUrl = "http://localhost:8001/proveedor/login";
	
	//private LoginDto loginDto;
	
	@Wire
	private Label lbBienvenida;
	@Wire
	private Label lbVersion;
	
	public void doAfterCompose(Window win) throws Exception {
		super.doAfterCompose(win);		
		log.debug("doAfterCompose()");
		
		try {
			log.debug("Invocando Servicio Rest para obtener autenticación de Usuario...");
			
			//Obtiene autenticación de usuario
			LoginDto loginDto = peticionHttpGet(strUrl);

			if(loginDto != null) {
				lbBienvenida.setValue(loginDto.getBienvenida());
				lbVersion.setValue(loginDto.getVersion());
			}			
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}

	private LoginDto peticionHttpGet(String urlParaVisitar) throws Exception {
		LoginDto loginDto = null;
		URL url = new URL(urlParaVisitar);
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
		conexion.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
		String output;
		while ((output = rd.readLine()) != null) {
			log.debug(output);
			loginDto = convertirJSonAClase(output);
		}
		rd.close();
		
		return loginDto;
	}
	
	private LoginDto convertirJSonAClase(String json_str) {
        ObjectMapper mapper = new ObjectMapper();
        LoginDto loginDto = new LoginDto();
        try {
            JsonNode node = mapper.readTree(json_str);      
            loginDto.setBienvenida(node.get("bienvenida").asText());
            loginDto.setVersion(node.get("version").asText());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return loginDto;
    }
	
	@Listen("onClick=#aceptar")
	public void aceptar() {
		log.debug("aceptar()");
		
		Executions.sendRedirect("/proveedor.zul");
		
	}
}
