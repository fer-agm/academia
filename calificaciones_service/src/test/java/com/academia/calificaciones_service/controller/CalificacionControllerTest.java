package com.academia.calificaciones_service.controller;

import com.academia.calificaciones_service.dto.CalificacionDTO;
import com.academia.calificaciones_service.service.CalificacionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Pure Mockito unit tests for {@link CalificacionController}.
 * No Spring context, no @WebMvcTest, no MockMvc, no database.
 *
 * <p>A {@link ServletRequestAttributes} request context is installed in
 * {@link #setUp()} because the controller builds HATEOAS links via
 * {@code linkTo(methodOn(...))} and calls {@code dto.add(link)}.
 */
@ExtendWith(MockitoExtension.class)
class CalificacionControllerTest {

    @Mock
    private CalificacionService calificacionService;

    @InjectMocks
    private CalificacionController controller;

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

    private CalificacionDTO buildDto(Long id) {
        return new CalificacionDTO(id, "EST-001", LocalDate.of(2026, 1, 15), 6.5);
    }

    @Test
    void getAll_returnsOkWithDtoList() {
        // Given
        CalificacionDTO dto1 = buildDto(1L);
        CalificacionDTO dto2 = buildDto(2L);
        when(calificacionService.getAll()).thenReturn(List.of(dto1, dto2));

        // When
        ResponseEntity<List<CalificacionDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        // HATEOAS self link added to each dto
        assertTrue(response.getBody().get(0).hasLinks());
        verify(calificacionService).getAll();
        verifyNoMoreInteractions(calificacionService);
    }

    @Test
    void getAll_whenEmpty_returnsOkWithEmptyList() {
        // Given
        when(calificacionService.getAll()).thenReturn(List.of());

        // When
        ResponseEntity<List<CalificacionDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(calificacionService).getAll();
    }

    @Test
    void getById_whenFound_returnsOkWithDtoAndLink() {
        // Given
        CalificacionDTO dto = buildDto(10L);
        when(calificacionService.getById(10L)).thenReturn(Optional.of(dto));

        // When
        ResponseEntity<CalificacionDTO> response = controller.getById(10L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getIdEvaluacion());
        assertTrue(response.getBody().hasLink("lista-completa"));
        verify(calificacionService).getById(10L);
    }

    @Test
    void getById_whenNotFound_returns404() {
        // Given
        when(calificacionService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<CalificacionDTO> response = controller.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(calificacionService).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedDto() {
        // Given
        CalificacionDTO input = new CalificacionDTO(null, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        CalificacionDTO saved = buildDto(5L);
        when(calificacionService.guardar(input)).thenReturn(saved);

        // When
        ResponseEntity<CalificacionDTO> response = controller.crear(input);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getIdEvaluacion());
        verify(calificacionService).guardar(input);
        verifyNoMoreInteractions(calificacionService);
    }

    @Test
    void actualizar_whenFound_returnsOkWithUpdatedDto() {
        // Given
        Long id = 10L;
        CalificacionDTO input = new CalificacionDTO(null, "EST-009", LocalDate.of(2026, 4, 1), 7.0);
        CalificacionDTO saved = buildDto(id);
        when(calificacionService.getById(id)).thenReturn(Optional.of(buildDto(id)));
        when(calificacionService.guardar(any(CalificacionDTO.class))).thenReturn(saved);

        // When
        ResponseEntity<CalificacionDTO> response = controller.actualizar(id, input);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getIdEvaluacion());
        // controller forces the path id onto the incoming dto before saving
        assertEquals(id, input.getIdEvaluacion());
        verify(calificacionService).getById(id);
        verify(calificacionService).guardar(input);
    }

    @Test
    void actualizar_whenNotFound_returns404AndDoesNotSave() {
        // Given
        Long id = 99L;
        CalificacionDTO input = new CalificacionDTO(null, "EST-009", LocalDate.of(2026, 4, 1), 7.0);
        when(calificacionService.getById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<CalificacionDTO> response = controller.actualizar(id, input);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(calificacionService).getById(id);
        verify(calificacionService, never()).guardar(any());
    }

    @Test
    void borrar_whenFound_returnsNoContentAndDeletes() {
        // Given
        Long id = 7L;
        when(calificacionService.getById(id)).thenReturn(Optional.of(buildDto(id)));

        // When
        ResponseEntity<Void> response = controller.borrar(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(calificacionService).getById(id);
        verify(calificacionService, times(1)).borrar(id);
    }

    @Test
    void borrar_whenNotFound_returns404AndDoesNotDelete() {
        // Given
        Long id = 99L;
        when(calificacionService.getById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = controller.borrar(id);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(calificacionService).getById(id);
        verify(calificacionService, never()).borrar(any());
    }
}
