package controlador;

import entidad.Cliente;
import negocio.ClienteBO;

public class ClienteControlador {

    private final ClienteBO clienteBO;

    public ClienteControlador() {
        this.clienteBO = new ClienteBO();
    }

    public Cliente registrarCliente(Cliente cliente) throws Exception {
        return clienteBO.registrarCliente(cliente);
    }

    public Cliente login(String usuario, String contrasena) throws Exception {
        return clienteBO.autenticar(usuario, contrasena);
    }
}