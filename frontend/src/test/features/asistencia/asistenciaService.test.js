import { describe, it, expect, vi, beforeEach } from 'vitest';
import axiosClient from '../../../core/api/axiosClient';

import {
  obtenerResumenDiario,
  obtenerAsistenciaPorCurso,
  obtenerCursos,
  obtenerAlumnosPorCurso,
  obtenerHistorialAsistencia,
  obtenerInasistencias,
  justificarInasistencia,
  guardarAnotacion,
  obtenerAnotacionesPorEstudiante,
  guardarAsistencia,
  obtenerAlumnosPorDocente,
  obtenerAlumnos,
} from '../../../features/asistencia/services/asistenciaService';

vi.mock('../../../core/api/axiosClient');

beforeEach(() => {
  vi.clearAllMocks();
});

describe('obtenerResumenDiario', () => {
  it('retorna null si no hay cursoId', async () => {
    const res = await obtenerResumenDiario(null, '2026-06-16');
    expect(axiosClient.get).not.toHaveBeenCalled();
    expect(res).toBeNull();
  });

  it('hace GET a /bff/asistencia/resumen con parámetros', async () => {
    axiosClient.get.mockResolvedValue({ data: { total: 30 } });
    const res = await obtenerResumenDiario('curso-1', '2026-06-16');
    expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/resumen', {
      params: { cursoId: 'curso-1', fecha: '2026-06-16' },
    });
    expect(res).toEqual({ total: 30 });
  });
});

describe('obtenerAsistenciaPorCurso', () => {
  it('hace GET a /bff/asistencia/curso/{id} con fecha', async () => {
    axiosClient.get.mockResolvedValue({ data: [{ estado: 'presente' }] });
    const res = await obtenerAsistenciaPorCurso('curso-1', '2026-06-16');
    expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/curso/curso-1', {
      params: { fecha: '2026-06-16' },
    });
    expect(res).toEqual([{ estado: 'presente' }]);
  });
});

describe('obtenerCursos', () => {
  it('hace GET a /bff/cursos', async () => {
    axiosClient.get.mockResolvedValue({ data: [{ id: 1, nombre: '1°A' }] });
    const res = await obtenerCursos();
    expect(axiosClient.get).toHaveBeenCalledWith('/bff/cursos');
    expect(res).toEqual([{ id: 1, nombre: '1°A' }]);
  });
});

describe('obtenerAlumnosPorCurso', () => {
  it('mapea los campos correctamente', async () => {
    axiosClient.get.mockResolvedValue({
      data: [
        { estudianteId: 'uuid-1', nombre: 'Juan Pérez', rut: '12345678-9' },
        { estudianteId: 'uuid-2', nombre: 'María', rut: '98765432-1' },
      ],
    });
    const res = await obtenerAlumnosPorCurso('curso-1');
    expect(res).toEqual([
      { id: 'uuid-1', rut: '12345678-9', nombre: 'Juan', apellido: 'Pérez' },
      { id: 'uuid-2', rut: '98765432-1', nombre: 'María', apellido: '' },
    ]);
  });
});

describe('obtenerHistorialAsistencia', () => {
  it('hace GET a /bff/asistencia/estudiante/{id}', async () => {
    axiosClient.get.mockResolvedValue({ data: [{ fecha: '2026-06-16', estado: 'presente' }] });
    const res = await obtenerHistorialAsistencia('alumno-1');
    expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/estudiante/alumno-1');
    expect(res).toEqual([{ fecha: '2026-06-16', estado: 'presente' }]);
  });
});

describe('obtenerInasistencias', () => {
  it('hace GET a /bff/asistencia/inasistencias', async () => {
    axiosClient.get.mockResolvedValue({ data: [{ id: 1 }] });
    const res = await obtenerInasistencias();
    expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/inasistencias');
    expect(res).toEqual([{ id: 1 }]);
  });
});

describe('justificarInasistencia', () => {
  it('hace PATCH con motivo', async () => {
    axiosClient.patch.mockResolvedValue({ data: { id: 1, justificada: true } });
    const res = await justificarInasistencia(1, { motivo: 'Enfermedad' });
    expect(axiosClient.patch).toHaveBeenCalledWith('/bff/asistencia/1/justificar', {
      motivo: 'Enfermedad',
    });
    expect(res).toEqual({ id: 1, justificada: true });
  });
});

describe('guardarAnotacion', () => {
  it('hace POST con estudianteId, tipo y descripcion', async () => {
    axiosClient.post.mockResolvedValue({ data: { id: 1 } });
    const res = await guardarAnotacion('est-1', { tipo: 'POSITIVA', descripcion: 'Buen comportamiento' });
    expect(axiosClient.post).toHaveBeenCalledWith('/bff/asistencia/anotaciones', {
      estudianteId: 'est-1',
      tipo: 'POSITIVA',
      descripcion: 'Buen comportamiento',
    });
    expect(res).toEqual({ id: 1 });
  });
});

describe('obtenerAnotacionesPorEstudiante', () => {
  it('hace GET a /bff/asistencia/anotaciones/estudiante/{id}', async () => {
    axiosClient.get.mockResolvedValue({ data: [{ tipo: 'POSITIVA' }] });
    const res = await obtenerAnotacionesPorEstudiante('est-1');
    expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/anotaciones/estudiante/est-1');
    expect(res).toEqual([{ tipo: 'POSITIVA' }]);
  });
});

describe('guardarAsistencia', () => {
  it('hace POST con listado transformado', async () => {
    axiosClient.post.mockResolvedValue({ data: { registrados: 2 } });
    const res = await guardarAsistencia('curso-1', '2026-06-16', [
      { estudianteId: 'uuid-1', estado: 'presente' },
      { estudianteId: 'uuid-2', estado: 'ausente' },
    ]);
    expect(axiosClient.post).toHaveBeenCalledWith('/bff/asistencia/registrar', [
      { estudianteId: 'uuid-1', cursoId: 'curso-1', estado: 'PRESENTE', observacion: null, fecha: '2026-06-16' },
      { estudianteId: 'uuid-2', cursoId: 'curso-1', estado: 'AUSENTE', observacion: null, fecha: '2026-06-16' },
    ]);
    expect(res).toEqual({ registrados: 2 });
  });
});

describe('obtenerAlumnosPorDocente', () => {
  it('retorna alumnos únicos de todos los cursos del docente', async () => {
    axiosClient.get.mockImplementation((url) => {
      if (url === '/bff/cursos') {
        return Promise.resolve({ data: [{ id: 1, nombre: '1°A' }, { id: 2, nombre: '2°B' }] });
      }
      if (url === '/bff/asistencia/alumnos/1') {
        return Promise.resolve({ data: [{ estudianteId: 'uuid-1', nombre: 'Juan Pérez', rut: '1' }] });
      }
      if (url === '/bff/asistencia/alumnos/2') {
        return Promise.resolve({ data: [
          { estudianteId: 'uuid-2', nombre: 'María López', rut: '2' },
          { estudianteId: 'uuid-1', nombre: 'Juan Pérez', rut: '1' },
        ] });
      }
      return Promise.reject(new Error('unknown url'));
    });
    const res = await obtenerAlumnosPorDocente();
    expect(res).toEqual([
      { id: 'uuid-1', nombre: 'Juan Pérez', rut: '1', curso: '1°A' },
      { id: 'uuid-2', nombre: 'María López', rut: '2', curso: '2°B' },
    ]);
  });
});

describe('obtenerAlumnos', () => {
  it('mapea usuarios ESTUDIANTE desde /bff/usuarios/ESTUDIANTE', async () => {
    axiosClient.get.mockResolvedValue({ data: [
      { id: 'uuid-1', nombreCompleto: 'Juan Pérez', rut: '12345678-9' },
      { id: 'uuid-2', nombreCompleto: '', rut: '' },
    ] });
    const res = await obtenerAlumnos();
    expect(res).toEqual([
      { id: 'uuid-1', nombre: 'Juan Pérez', rut: '12345678-9', curso: '' },
      { id: 'uuid-2', nombre: '', rut: '', curso: '' },
    ]);
  });
});
