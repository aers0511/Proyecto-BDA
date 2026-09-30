package com.tutiket.util;

import com.tutiket.domain.Administrador;
import com.tutiket.domain.Promotora;
import com.tutiket.dto.ClienteRegistroDTO;
import com.tutiket.exception.BusinessException;
import com.tutiket.repository.impl.JdbcAdministradorRepository;
import com.tutiket.repository.impl.JdbcClienteRepository;
import com.tutiket.repository.impl.JdbcCuentaClienteRepository;
import com.tutiket.repository.impl.JdbcCuentaPromotoraRepository;
import com.tutiket.repository.impl.JdbcPromotoraRepository;
import com.tutiket.repository.impl.JdbcReporteRepository;
import com.tutiket.service.AdminService;
import com.tutiket.service.ClienteService;
import com.tutiket.service.PromotoraService;

public class DataInitializer {

    public static void cargarDatosIniciales() {
        // Inicialización de servicios con sus respectivos repositorios
        AdminService adminService = new AdminService(
                new JdbcReporteRepository(),
                new JdbcAdministradorRepository()
        );

        PromotoraService promotoraService = new PromotoraService(
                new JdbcPromotoraRepository(), 
                new JdbcCuentaPromotoraRepository()
        );
        
        ClienteService clienteService = new ClienteService(
                new JdbcClienteRepository(), 
                new JdbcCuentaClienteRepository()
        );

        // 1. Crear Administrador General mediante AdminService
        try {
            Administrador admin = new Administrador();
            admin.setNombre("Administrador General");
            admin.setCorreo("admin@tutiket.com");
            admin.setUsuario("admin");
            admin.setContrasena("123");
            adminService.registrarAdmin(admin);
        } catch (BusinessException ignored) {
            // El usuario o correo ya existe en la BD
        } catch (Exception e) {
            System.err.println("Error al inicializar Admin: " + e.getMessage());
        }

        // 2. Registrar Promotoras usando PromotoraService (crea automáticamente sus 2 cuentas bancarias)
        try {
            Promotora p1 = new Promotora();
            p1.setNombreEmpresa("Promotora Uno");
            p1.setRfc("PRO010101AAA");
            p1.setCorreo("promotora1@tutiket.com");
            p1.setUsuario("promotora1");
            p1.setContrasena("123");
            promotoraService.registrarPromotora(p1);
        } catch (BusinessException ignored) {
            // El usuario o correo ya existe en la BD
        } catch (Exception e) {
            System.err.println("Error al inicializar Promotora 1: " + e.getMessage());
        }

        try {
            Promotora p2 = new Promotora();
            p2.setNombreEmpresa("Promotora Dos");
            p2.setRfc("PRO020202BBB");
            p2.setCorreo("promotora2@tutiket.com");
            p2.setUsuario("promotora2");
            p2.setContrasena("123");
            promotoraService.registrarPromotora(p2);
        } catch (BusinessException ignored) {
            // El usuario o correo ya existe en la BD
        } catch (Exception e) {
            System.err.println("Error al inicializar Promotora 2: " + e.getMessage());
        }

        // 3. Registrar Cliente Demo usando ClienteService (crea automáticamente sus 3 cuentas bancarias)
        try {
            ClienteRegistroDTO dto = new ClienteRegistroDTO();
            dto.setNombres("Cliente");
            dto.setApellidoPaterno("Demo");
            dto.setCorreo("cliente@tutiket.com");
            dto.setUsuario("cliente");
            dto.setContrasena("123");
            clienteService.registrarCliente(dto);
        } catch (BusinessException ignored) {
            // El usuario o correo ya existe en la BD
        } catch (Exception e) {
            System.err.println("Error al inicializar Cliente Demo: " + e.getMessage());
        }
    }
}