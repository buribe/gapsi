package com.gapsi.ecommerce.controlador;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gapsi.ecommerce.dto.ProveedorDto;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ProveedorControlador extends SelectorComposer<Window> {
	private static final long serialVersionUID = 1L;
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String strUrl = "http://localhost:8001/proveedor";
	
	private ProveedorDto proveedorDto;
	private List<ProveedorDto> lstProveedorDto;
	
	@Wire
    private Listbox proveedorListbox;
	@Wire
	private Textbox tbNombre;
	@Wire
	private Textbox tbRazonSocial;
	@Wire
	private Textbox tbDireccion;
	@Wire
	private Label lbMensaje;
	@Wire
	private Button btnAgregar;
	@Wire
	private Button btnEliminar;
	
	public void doAfterCompose(Window win) throws Exception {
		super.doAfterCompose(win); 	
		log.debug("doAfterCompose()");
		
		try {
			log.debug("Invocando Servicio Rest para obtener lista de Proveedores...");
			
			//Obtiene lista de proveedores
			peticionHttpGet(strUrl);

		} catch(Exception exc) {
			exc.printStackTrace();
		}		
	}
	
	@Listen("onClick=#btnNuevo")
	public void nuevo() {
		log.debug("nuevo()");
		
		tbNombre.setValue("");
        tbRazonSocial.setValue("");
    	tbDireccion.setValue("");
    	
		tbNombre.setReadonly(false);
    	tbRazonSocial.setReadonly(false);
    	tbDireccion.setReadonly(false);
    	
		lbMensaje.setValue("");
		btnAgregar.setDisabled(false);
    	btnEliminar.setDisabled(true);
	}

	@Listen("onSelect = listbox")
    public void onSelecListBox() {
		log.debug("onSelecListBox");

		//Obtiene el proveedor seleccionado de la lista
        int index = proveedorListbox.getSelectedIndex();        
        proveedorDto = lstProveedorDto.get(index);
        log.debug("selected: " + proveedorDto.toString());
        
        tbNombre.setValue(proveedorDto.getNombre());
        tbRazonSocial.setValue(proveedorDto.getRazonSocial());
    	tbDireccion.setValue(proveedorDto.getDireccion());
    	
    	tbNombre.setReadonly(true);
    	tbRazonSocial.setReadonly(true);
    	tbDireccion.setReadonly(true);
    	
    	lbMensaje.setValue("");
    	btnAgregar.setDisabled(true);
    	btnEliminar.setDisabled(false);
    }
	
	@Listen("onClick=#btnAgregar")
	public void agregar() {
		log.debug("agregar()");
				
		try {
			log.debug("Invocando Servicio Rest para Guardar...");
			
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("content-type","application/json");
			String input = "{\"nombre\":\"" + tbNombre.getValue() + "\","
					+ "\"razonSocial\":\"" + tbRazonSocial.getValue() + "\","
					+ "\"direccion\":\"" + tbDireccion.getValue() + "\"}";
			log.debug("input: " + input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			
			if(conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				lbMensaje.setValue("¡Nombre de Proveedor ya existe!");
				throw new RuntimeException("Error Code 302: "+conn.getResponseCode());
			} else if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed: Http Error Code: "+conn.getResponseCode());
			}
			//InputStream in;		
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;
			ProveedorDto proveedorDto;
			log.debug("Response from API:");
			while((output=br.readLine()) != null ) {
				log.debug(output);
				proveedorDto = convertirJSonAClase(output);
			}
			conn.disconnect();			
			
			lbMensaje.setValue("¡Proveedor guardado exitosamentet!");
			
			//Obtiene lista de proveedores
			peticionHttpGet(strUrl);

		} catch(Exception exc) {
			exc.printStackTrace();
		}		
	}
	
	@Listen("onClick=#btnEliminar")
	public void eliminar() {
		log.debug("eliminar()");
		
		try {
			//Elimina proveedor
			peticionHttpDelete(strUrl+"/"+proveedorDto.getId());
			
			lbMensaje.setValue("¡Proveedor eliminado exitosamentet!");
			
			//Obtiene lista de proveedores
			peticionHttpGet(strUrl);
			
		} catch(Exception exc) {
			exc.printStackTrace();
		}	
	}	

	private void peticionHttpGet(String urlParaVisitar) throws Exception {
		lstProveedorDto = new ArrayList<>(); 
		URL url = new URL(urlParaVisitar);
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
		conexion.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
		String output;
		while ((output = rd.readLine()) != null) {
			log.debug(output);
			convertirJSonAListaClase(output);
		}
		rd.close();
		proveedorListbox.setModel(new ListModelList<ProveedorDto>(lstProveedorDto));
	}
	
	private void peticionHttpDelete(String urlParaVisitar) throws Exception {
		log.debug(urlParaVisitar);		
		URL url = new URL(urlParaVisitar);
		HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
		conexion.setRequestMethod("DELETE");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
		String output;
		while ((output = rd.readLine()) != null) {
			log.debug(output);
		}
		rd.close();
	}
	
	private ProveedorDto convertirJSonAClase(String json_str) {
        ObjectMapper mapper = new ObjectMapper();
        ProveedorDto proveedorDto = new ProveedorDto();
        try {
            JsonNode node = mapper.readTree(json_str);
            proveedorDto.setNombre(node.get("nombre").asText());
			proveedorDto.setRazonSocial(node.get("razonSocial").asText());
			proveedorDto.setDireccion(node.get("direccion").asText());
			proveedorDto.setId(node.get("id").asInt());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return proveedorDto;
    }
	
	private void convertirJSonAListaClase(String jsonString) {
        try{
            Gson gson = new Gson();                
            lstProveedorDto = gson.fromJson(jsonString, new TypeToken<List<ProveedorDto>>(){}.getType());     
            log.debug("lstProveedorDto: " + lstProveedorDto.size()); 

            if( lstProveedorDto!= null ){
                for(ProveedorDto object : lstProveedorDto){
                	log.debug("Uusario Info: " + object.toString());    
                }
            }
        }catch(JsonSyntaxException e){
            System.err.println("JsonSyntaxException: " + e.getMessage());
        }    
    } 

	public ProveedorDto getProveedorDto() {
		return proveedorDto;
	}

	public void setProveedorDto(ProveedorDto proveedorDto) {
		this.proveedorDto = proveedorDto;
	}		
}
