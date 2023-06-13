package com.gapsi.ecommerce.dto;

public class LoginDto {
	private String bienvenida;
	private String version;

	public LoginDto() {
		super();
	}

	public String getBienvenida() {
		return bienvenida;
	}

	public void setBienvenida(String bienvenida) {
		this.bienvenida = bienvenida;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "LoginDto [bienvenida=" + bienvenida + ", version=" + version + "]";
	}

}
