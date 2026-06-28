package com.academia.calificaciones_service.controller;

import com.academia.calificaciones_service.dto.PromedioDTO;
import com.academia.calificaciones_service.service.PromedioService;
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
 * Pure Mockito unit tests for {@link PromedioController}.
 * No Spring context, no @WebMvcTest, no MockMvc, no database.
 *
 * <p>A {@link ServletRequestAttributes} request context is installed in
 * {@link #setUp()} because the controller builds HATEOAS links via
 * {@code linkTo(methodOn(...))} and calls {@code dto.add(link)}.
 */
@ExtendWith(MockitoExtension.class)
class PromedioControllerTest {

    @Mock
    private PromedioService promedioService;

    @InjectMocks
    private PromedioController controller;

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

    private PromedioDTO buildDto(Long id) {
        return new PromedioDTO(id, 100L, "EST-001", 5.8, 4);
    }

    @Test
    void getAll_returnsOkWithDtoList() {
        // Given
        PromedioDTO dto1 = buildDto(1L);
        PromedioDTO dto2 = buildDto(2L);
        when(promedioService.getAll()).thenReturn(List.of(dto1, dto2));

        // When
        ResponseEntity<List<PromedioDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        // HATEOAS self link added to each dto
        assertTrue(response.getBody().get(0).hasLinks());
        verify(promedioService).getAll();
        verifyNoMoreInteractions(promedioService);
    }

    @Test
    void getAll_whenEmpty_returnsOkWithEmptyList() {
        // Given
        when(promedioService.getAll()).thenReturn(List.of());

        // When
        ResponseEntity<List<PromedioDTO>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(promedioService).getAll();
    }

    @Test
    void getById_whenFound_returnsOkWithDtoAndLink() {
        // Given
        PromedioDTO dto = buildDto(10L);
        when(promedioService.getById(10L)).thenReturn(Optional.of(dto));

        // When
        ResponseEntity<PromedioDTO> response = controller.getById(10L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getIdPromedio());
        assertTrue(response.getBody().hasLink("lista-completa"));
        verify(promedioService).getById(10L);
    }

    @Test
    void getById_whenNotFound_returns404() {
        // Given
        when(promedioService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<PromedioDTO> response = controller.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(promedioService).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedDto() {
        // Given
        PromedioDTO input = new PromedioDTO(null, 100L, "EST-003", 5.5, 3);
        PromedioDTO saved = buildDto(5L);
        when(promedioService.guardar(input, "Bearer token")).thenReturn(saved);

        // When
        ResponseEntity<PromedioDTO> response = controller.crear(input, "Bearer token");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getIdPromedio());
        verify(promedioService).guardar(input, "Bearer token");
        verifyNoMoreInteractions(promedioService);
    }

    @Test
    void actualizar_whenFound_returnsOkWithUpdatedDto() {
        // Given
        Long id = 10L;
        PromedioDTO input = new PromedioDTO(null, 200L, "EST-009", 6.2, 5);
        PromedioDTO saved = buildDto(id);
        when(promedioService.getById(id)).thenReturn(Optional.of(buildDto(id)));
        when(promedioService.guardar(any(PromedioDTO.class), any())).thenReturn(saved);

        // When
        ResponseEntity<PromedioDTO> response = controller.actualizar(id, input, "Bearer token");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getIdPromedio());
        // controller forces the path id onto the incoming dto before saving
        assertEquals(id, input.getIdPromedio());
        verify(promedioService).getById(id);
        verify(promedioService).guardar(input, "Bearer token");
    }

    @Test
    void actualizar_whenNotFound_returns404AndDoesNotSave() {
        // Given
        Long id = 99L;
        PromedioDTO input = new PromedioDTO(null, 200L, "EST-009", 6.2, 5);
        when(promedioService.getById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<PromedioDTO> response = controller.actualizar(id, input, "Bearer token");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(promedioService).getById(id);
        verify(promedioService, never()).guardar(any(), any());
    }

    @Test
    void borrar_whenFound_returnsNoContentAndDeletes() {
        // Given
        Long id = 7L;
        when(promedioService.getById(id)).thenReturn(Optional.of(buildDto(id)));

        // When
        ResponseEntity<Void> response = controller.borrar(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(promedioService).getById(id);
        verify(promedioService, times(1)).borrar(id);
    }

    @Test
    void borrar_whenNotFound_returns404AndDoesNotDelete() {
        // Given
        Long id = 99L;
        when(promedioService.getById(id)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = controller.borrar(id);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(promedioService).getById(id);
        verify(promedioService, never()).borrar(any());
    }
}
