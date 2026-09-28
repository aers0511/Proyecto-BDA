package negocio;

import entidad.Cliente;
import persistencia.IClienteDAO;
import persistenciaDAO.ClienteDAO;
import persistenciaUtil.EncriptadorUtil;

public class ClienteBO {

    private final IClienteDAO clienteDAO;

    public ClienteBO() {
        this.clienteDAO = new ClienteDAO();
    }

    public Cliente registrarCliente(Cliente cliente) throws Exception {
        if (clienteDAO.obtenerPorUsuario(cliente.getUsuario()) != null) {
            throw new Exception("El nombre de usuario ya se encuentra registrado.");
        }

        // Encriptar la contraseña antes de persistir
        String hashPassword = EncriptadorUtil.encriptarPassword(cliente.getContrasena());
        cliente.setContrasena(hashPassword);

        return clienteDAO.insertar(cliente);
    }

    public Cliente autenticar(String usuario, String contrasena) throws Exception {
        Cliente cliente = clienteDAO.obtenerPorUsuario(usuario);
        if (cliente == null) {
            throw new Exception("Usuario o contraseña incorrectos.");
        }

        if (!EncriptadorUtil.verificarPassword(contrasena, cliente.getContrasena())) {
            throw new Exception("Usuario o contraseña incorrectos.");
        }

        return cliente;
    }
}