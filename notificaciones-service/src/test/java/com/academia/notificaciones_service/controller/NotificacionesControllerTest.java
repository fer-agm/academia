package com.academia.notificaciones_service.controller;

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
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.academia.notificaciones_service.dto.NotificacionesDTO;
import com.academia.notificaciones_service.service.NotificacionesService;

/**
 * Pure Mockito unit tests for {@link NotificacionesController}.
 * No Spring context, no @WebMvcTest, no MockMvc, no database.
 *
 * <p>A {@link ServletRequestAttributes} request context is installed in
 * {@link #setUp()} because the controller builds HATEOAS links via
 * {@code linkTo(methodOn(...))} and calls {@code dto.add(link)}.
 */
@ExtendWith(MockitoExtension.class)
class NotificacionesControllerTest {

    @Mock
    private NotificacionesService notificacionesService;

    @InjectMocks
    private NotificacionesController controller;

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

    // NotificacionDTO(idNotificacion, idEstudiante, idCertificado)
    private NotificacionesDTO buildDto(Long id) {
        return new NotificacionesDTO(id, "id", 10L);
    }

    @Test
    void getAll_returnsOkWithDtoList() {
        // Given
        NotificacionesDTO dto1 = buildDto(1L);
        NotificacionesDTO dto2 = buildDto(2L);
        when(notificacionesService.getAll()).thenReturn(List.of(dto1, dto2));

        // When
        ResponseEntity<List<NotificacionesDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        // HATEOAS self link added to each dto
        assertTrue(response.getBody().get(0).hasLinks());
        verify(notificacionesService).getAll();
        verifyNoMoreInteractions(notificacionesService);
    }

    @Test
    void getAll_whenEmpty_returnsOkWithEmptyList() {
        // Given
        when(notificacionesService.getAll()).thenReturn(List.of());

        // When
        ResponseEntity<List<NotificacionesDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(notificacionesService).getAll();
    }

    @Test
    void getById_whenFound_returnsOkWithDtoAndLink() {
        // Given
        NotificacionesDTO dto = buildDto(10L);
        when(notificacionesService.getById(10L)).thenReturn(Optional.of(dto));

        // When
        ResponseEntity<NotificacionesDTO> response = controller.getById(10L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getIdNotificacion());
        assertTrue(response.getBody().hasLink("lista-completa"));
        verify(notificacionesService).getById(10L);
    }

    @Test
    void getById_whenNotFound_returns404() {
        // Given
        when(notificacionesService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<NotificacionesDTO> response = controller.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(notificacionesService).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedDto() {
        // Given
        NotificacionesDTO input = new NotificacionesDTO(null, "id", 10L);
        NotificacionesDTO saved = buildDto(5L);
        when(notificacionesService.guardar(input, "Bearer token")).thenReturn(saved);

        // When
        ResponseEntity<NotificacionesDTO> response = controller.crear(input, "Bearer token");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getIdNotificacion());
        verify(notificacionesService).guardar(input, "Bearer token");
        verifyNoMoreInteractions(notificacionesService);
    }

    @Test
    void actualizar_whenFound_returnsOkWithUpdatedDto() {
        // Given
        Long id = 10L;
        NotificacionesDTO input = new NotificacionesDTO(null, "id", 10L);
        NotificacionesDTO saved = buildDto(id);
        when(notificacionesService.getById(id)).thenReturn(Optional.of(buildDto(id)));
        when(notificacionesService.guardar(any(NotificacionesDTO.class), any())).thenReturn(saved);

        // When
        ResponseEntity<NotificacionesDTO> response = controller.actualizar(id, input, "Bearer token");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getIdNotificacion());
        // controller forces the path id onto the incoming dto before saving
        assertEquals(id, input.getIdNotificacion());
        verify(notificacionesService).getById(id);
        verify(notificacionesService).guardar(input, "Bearer token");
    }

    @Test
    void actualizar_whenNotFound_returns404AndDoesNotSave() {
        // Given
        Long id = 99L;
        NotificacionesDTO input = new NotificacionesDTO(null, "id", 10L);
        when(notificacionesService.getById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<NotificacionesDTO> response = controller.actualizar(id, input, "Bearer token");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(notificacionesService).getById(id);
        verify(notificacionesService, never()).guardar(any(), any());
    }

    @Test
    void borrar_whenFound_returnsNoContentAndDeletes() {
        // Given
        Long id = 7L;
        when(notificacionesService.getById(id)).thenReturn(Optional.of(buildDto(id)));

        // When
        ResponseEntity<Void> response = controller.borrar(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(notificacionesService).getById(id);
        verify(notificacionesService, times(1)).borrar(id);
    }

    @Test
    void borrar_whenNotFound_returns404AndDoesNotDelete() {
        // Given
        Long id = 99L;
        when(notificacionesService.getById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = controller.borrar(id);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(notificacionesService).getById(id);
        verify(notificacionesService, never()).borrar(any());
    }
}
