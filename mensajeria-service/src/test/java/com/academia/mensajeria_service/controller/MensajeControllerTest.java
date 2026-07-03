package com.academia.mensajeria_service.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.academia.mensajeria_service.dto.MensajeDTO;
import com.academia.mensajeria_service.service.MensajeService;

/**
 * Pure Mockito unit tests for {@link MensajeController}.
 * No Spring context, no @WebMvcTest, no MockMvc, no database.
 *
 * <p>A {@link ServletRequestAttributes} request context is installed in
 * {@link #setUp()} because the controller builds HATEOAS links via
 * {@code linkTo(methodOn(...))} and calls {@code dto.add(link)}.
 */
@ExtendWith(MockitoExtension.class)
class MensajeControllerTest {

    @Mock
    private MensajeService mensajeService;

    @InjectMocks
    private MensajeController controller;

    @BeforeEach
    void setUp() {
        // HATEOAS link building requires a bound request context.
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    // MensajeDTO(idMensaje, idEmisor, idReceptor, mensaje)
    private MensajeDTO buildDto(Long id, String idEmisor, String idReceptor, String mensaje) {
        return new MensajeDTO(id, idEmisor, idReceptor, mensaje);
    }

    @Test
    void getAll_returnsOkWithDtoList() {
        // Given
        MensajeDTO dto1 = buildDto(1L, "idEmisor1", "idReceptor1", "Mensaje 1");
        MensajeDTO dto2 = buildDto(2L, "idEmisor2", "idReceptor2", "Mensaje 2");
        when(mensajeService.getAll()).thenReturn(List.of(dto1, dto2));

        // When
        ResponseEntity<List<MensajeDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        // HATEOAS self link added to each dto
        assertTrue(response.getBody().get(0).hasLinks());
        verify(mensajeService).getAll();
        verifyNoMoreInteractions(mensajeService);
    }

    @Test
    void getAll_whenEmpty_returnsOkWithEmptyList() {
        // Given
        when(mensajeService.getAll()).thenReturn(List.of());

        // When
        ResponseEntity<List<MensajeDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(mensajeService).getAll();
    }

    @Test
    void getById_whenFound_returnsOkWithDtoAndLink() {
        // Given
        MensajeDTO dto = buildDto(10L, "idEmisor10", "idReceptor10", "Mensaje 10");
        when(mensajeService.getById(10L)).thenReturn(Optional.of(dto));

        // When
        ResponseEntity<MensajeDTO> response = controller.getById(10L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getIdMensaje());
        assertTrue(response.getBody().hasLink("lista-completa"));
        verify(mensajeService).getById(10L);
    }

    @Test
    void getById_whenNotFound_returns404() {
        // Given
        when(mensajeService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<MensajeDTO> response = controller.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(mensajeService).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedDto() {
        // Given
        MensajeDTO input = new MensajeDTO(null, "idEmisor", "idReceptor", "mensaje");
        MensajeDTO saved = buildDto(5L, "idEmisor", "idReceptor", "mensaje");
        when(mensajeService.generarMensaje(input, "Bearer token")).thenReturn(saved);

        // When
        ResponseEntity<MensajeDTO> response = controller.generarMensaje(input, "Bearer token");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getIdMensaje());
        verify(mensajeService).generarMensaje(input, "Bearer token");
        verifyNoMoreInteractions(mensajeService);
    }


    // @Test
    // void borrar_whenFound_returnsNoContentAndDeletes() {
    //     // Given
    //     Long id = 7L;
    //     Long idEmisor = 100L;
    //     Long idReceptor = 200L;
    //     String mensaje = "Mensaje a borrar";
    //     when(mensajeService.getById(id)).thenReturn(Optional.of(buildDto(id, idEmisor, idReceptor, mensaje)));

    //     // When
    //     ResponseEntity<Void> response = controller.borrar(id);

    //     // Then
    //     assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    //     assertNull(response.getBody());
    //     verify(mensajeService).getById(id);
    //     verify(mensajeService, times(1)).borrar(id);
    // }

    // @Test
    // void borrar_whenNotFound_returns404AndDoesNotDelete() {
    //     // Given
    //     Long id = 99L;
    //     when(mensajeService.getById(id)).thenReturn(Optional.empty());

    //     // When
    //     ResponseEntity<Void> response = controller.borrar(id);

    //     // Then
    //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    //     assertNull(response.getBody());
    //     verify(mensajeService).getById(id);
    //     verify(mensajeService, never()).borrar(any());
    // }
}
