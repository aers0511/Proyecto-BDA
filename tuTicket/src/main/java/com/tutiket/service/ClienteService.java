package com.tutiket.service;

import com.tutiket.config.DatabaseConfig;
import com.tutiket.domain.Cliente;
import com.tutiket.domain.CuentaCliente;
import com.tutiket.dto.ClienteRegistroDTO;
import com.tutiket.exception.BusinessException;
import com.tutiket.repository.ClienteRepository;
import com.tutiket.repository.CuentaClienteRepository;
import com.tutiket.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Random;

public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CuentaClienteRepository cuentaRepository;

    public ClienteService(ClienteRepository clienteRepository, CuentaClienteRepository cuentaRepository) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
    }

    public Cliente registrarCliente(ClienteRegistroDTO dto) throws Exception {
        if (dto.getUsuario() == null || dto.getUsuario().isBlank() || 
            dto.getCorreo() == null || dto.getCorreo().isBlank() ||
            dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            throw new BusinessException("El usuario, el correo y la contraseña son obligatorios.");
        }

        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();

            if (clienteRepository.existeUsuarioOCorreo(conn, dto.getUsuario(), dto.getCorreo())) {
                throw new BusinessException("El nombre de usuario o correo ya se encuentra registrado.");
            }

            Cliente cliente = new Cliente();
            String nombreCompleto = dto.getNombres() + " " + dto.getApellidoPaterno() + " " + 
                    (dto.getApellidoMaterno() != null ? dto.getApellidoMaterno() : "");
            
            cliente.setNombre(nombreCompleto.trim());
            cliente.setCorreo(dto.getCorreo());
            cliente.setUsuario(dto.getUsuario());
            cliente.setContrasena(PasswordUtil.hashPassword(dto.getContrasena()));
            cliente.setActivo(true);

            Cliente clienteGuardado = clienteRepository.guardar(conn, cliente);

            // Requisito: Generación de 3 cuentas automáticas fondeadas con $1,000 MXN en total ($400 + $350 + $250)
            String[] bancos = {"BBVA", "Banamex", "Banorte", "Santander", "HSBC"};
            Random random = new Random();
            BigDecimal[] montos = {
                new BigDecimal("400.00"), 
                new BigDecimal("350.00"), 
                new BigDecimal("250.00")
            };

            for (int i = 0; i < 3; i++) {
                String numeroCuenta;
                
                // Garantiza que el número de tarjeta sea único en la BD antes de insertar
                do {
                    numeroCuenta = "4152" + String.format("%012d", Math.abs(random.nextLong()) % 100000000000L);
                } while (cuentaRepository.existeNumeroCuenta(conn, numeroCuenta));

                CuentaCliente cuenta = new CuentaCliente();
                cuenta.setIdCliente(clienteGuardado.getId());
                cuenta.setNumeroCuenta(numeroCuenta);
                cuenta.setBanco(bancos[random.nextInt(bancos.length)]);
                cuenta.setSaldo(montos[i]);
                cuenta.setActivo(true);
                cuentaRepository.guardar(conn, cuenta);
            }

            DatabaseConfig.commitTransaction();
            return clienteGuardado;

        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }

    public List<Cliente> obtenerTodosLosClientes() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return clienteRepository.listarTodos(conn);
        }
    }

    public boolean darDeBajaCliente(Long idCliente) throws Exception {
        if (idCliente == null || idCliente <= 0) {
            throw new BusinessException("El ID del cliente no es válido.");
        }

        DatabaseConfig.beginTransaction();
        try {
            Connection conn = DatabaseConfig.getConnection();
            
            boolean dadoDeBaja = clienteRepository.darDeBaja(conn, idCliente);
            if (!dadoDeBaja) {
                throw new BusinessException("No se encontró el cliente o ya se encuentra dado de baja.");
            }

            DatabaseConfig.commitTransaction();
            return true;
        } catch (Exception e) {
            DatabaseConfig.rollbackTransaction();
            throw e;
        }
    }
}