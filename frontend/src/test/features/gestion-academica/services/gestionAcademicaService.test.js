import { describe, it, expect, vi } from 'vitest';
import {
  obtenerKpisDashboard,
  obtenerCalificaciones, guardarCalificaciones,
  obtenerBoletinPropio, obtenerBoletinPupilo, getPupiloUuidFromToken,
  obtenerCursos, crearCurso, obtenerCursoPorId,
  obtenerAsignaturas, crearAsignatura,
  obtenerDocentes, obtenerAsignaciones, crearAsignacion, eliminarAsignacion,
  obtenerEstudiantesPorCurso, obtenerEstudiantesDisponibles, matricularEstudiante,
} from '../../../../features/gestion-academica/services/gestionAcademicaService';
import axiosClient from '../../../../core/api/axiosClient';

vi.mock('../../../../core/api/axiosClient');
vi.mock('../../../../shared/utils/tokenUtils', () => ({
  getUuidFromToken: vi.fn(() => 'uuid-propio'),
  getPupiloUuidFromToken: vi.fn(() => 'uuid-pupilo'),
}));

describe('gestionAcademicaService', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  describe('obtenerKpisDashboard', () => {
    it('mapea respuesta en array de KPIs', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: { totalEstudiantes: 500, totalDocentes: 30, totalCursos: 12, totalAsignaturas: 48 },
      });
      const result = await obtenerKpisDashboard();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/dashboard/stats');
      expect(result).toHaveLength(4);
      expect(result[0]).toMatchObject({ label: 'Estudiantes', numero: 500 });
      expect(result[1]).toMatchObject({ label: 'Docentes', numero: 30 });
      expect(result[2]).toMatchObject({ label: 'Cursos', numero: 12 });
      expect(result[3]).toMatchObject({ label: 'Asignaturas', numero: 48 });
    });
  });

  describe('obtenerCalificaciones', () => {
    it('mapea items con nombre o id como fallback', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: [
          { id: 'al1', nombre: 'Juan', nota1: 5.0, nota2: 6.0, nota3: 4.0, promedio: 5.0 },
          { id: 'al2', nota1: 3.0, nota2: 4.0, nota3: 5.0, promedio: 4.0 },
        ],
      });
      const result = await obtenerCalificaciones(1, 10);
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/calificaciones/curso/1/asignatura/10');
      expect(result).toHaveLength(2);
      expect(result[1].nombre).toBe('al2');
    });
  });

  describe('guardarCalificaciones', () => {
    it('envía PUT con payload convertido a Number', async () => {
      vi.mocked(axiosClient.put).mockResolvedValue({});
      await guardarCalificaciones(1, 10, [{ id: 'al1', nota1: '5.0', nota2: '6.0', nota3: '4.0' }]);
      expect(axiosClient.put).toHaveBeenCalledWith('/bff/calificaciones/guardar', [{
        usuarioUuid: 'al1', asignaturaId: 10, nota1: 5, nota2: 6, nota3: 4,
      }]);
    });
  });

  describe('obtenerBoletinPropio', () => {
    it('usa uuid del token y adapta respuesta', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: {
          nombreCompleto: 'Alumno Test', rut: '11-1', curso: '1°A',
          promedioGeneral: 5.5, porcentajeAsistencia: 85,
          calificaciones: [{ asignaturaId: 1, asignaturaNombre: 'Mat', nota1: 6 }],
        },
      });
      const result = await obtenerBoletinPropio();
      expect(result.alumno.nombre).toBe('Alumno Test');
      expect(result.alumno.periodo).toBe(String(new Date().getFullYear()));
      expect(result.asignaturas).toHaveLength(1);
      expect(result.asignaturas[0].nombre).toBe('Mat');
    });

    it('maneja calificaciones vacías y campos ausentes', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: {} });
      const result = await obtenerBoletinPropio();
      expect(result.alumno.nombre).toBe('');
      expect(result.asignaturas).toEqual([]);
    });
  });

  describe('obtenerBoletinPupilo', () => {
    it('hace GET con pupilId', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: { nombreCompleto: 'Pupilo' } });
      const result = await obtenerBoletinPupilo('pupil-uuid');
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/boletin/pupil-uuid');
      expect(result.alumno.nombre).toBe('Pupilo');
    });
  });

  describe('obtenerCursos', () => {
    it('hace GET a /bff/cursos', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ id: 1, nombre: '1°A' }] });
      const result = await obtenerCursos();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/cursos');
      expect(result).toHaveLength(1);
    });
  });

  describe('crearCurso', () => {
    it('hace POST a /v1/cursos/crear con datos', async () => {
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { id: 1 } });
      const datos = { nombre: '1°A', anioEscolar: 2026 };
      const result = await crearCurso(datos);
      expect(axiosClient.post).toHaveBeenCalledWith('/v1/cursos/crear', datos);
      expect(result).toEqual({ id: 1 });
    });
  });

  describe('obtenerCursoPorId', () => {
    it('hace GET a /v1/cursos/{cursoId}', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: { id: 1, nombre: '1°A' } });
      const result = await obtenerCursoPorId(1);
      expect(axiosClient.get).toHaveBeenCalledWith('/v1/cursos/1');
      expect(result.nombre).toBe('1°A');
    });
  });

  describe('obtenerAsignaturas', () => {
    it('hace GET a /bff/asignaturas', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ id: 10, nombre: 'Mat' }] });
      const result = await obtenerAsignaturas();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asignaturas');
      expect(result).toHaveLength(1);
    });
  });

  describe('crearAsignatura', () => {
    it('hace POST con horasSemanales convertido a Number', async () => {
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { id: 10 } });
      const datos = { nombre: 'Historia', horasSemanales: '4' };
      const result = await crearAsignatura(datos);
      expect(axiosClient.post).toHaveBeenCalledWith('/v1/asignaturas/crear', {
        nombre: 'Historia', horasSemanales: 4,
      });
      expect(result).toEqual({ id: 10 });
    });
  });

  describe('obtenerDocentes', () => {
    it('filtra inactivos y mapea nombre', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: [
          { id: 'd1', nombreCompleto: 'Profe Uno' },
          { id: 'd2', nombreCompleto: 'Profe Dos', activo: false },
          { id: 'd3', nombre: 'Solo Nombre' },
        ],
      });
      const result = await obtenerDocentes();
      expect(result).toHaveLength(2);
      expect(result[0].nombre).toBe('Profe Uno');
      expect(result[1].nombre).toBe('Solo Nombre');
    });
  });

  describe('obtenerAsignaciones', () => {
    it('hace GET a /v1/asignacion-docente', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ id: 1 }] });
      const result = await obtenerAsignaciones();
      expect(axiosClient.get).toHaveBeenCalledWith('/v1/asignacion-docente');
      expect(result).toHaveLength(1);
    });
  });

  describe('crearAsignacion', () => {
    it('hace POST convirtiendo cursoId y asignaturaId a Number', async () => {
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { id: 1 } });
      const result = await crearAsignacion({ docenteId: 'd1', cursoId: '5', asignaturaId: '10' });
      expect(axiosClient.post).toHaveBeenCalledWith('/v1/asignacion-docente', {
        docenteUuid: 'd1', cursoId: 5, asignaturaId: 10,
      });
      expect(result).toEqual({ id: 1 });
    });
  });

  describe('eliminarAsignacion', () => {
    it('hace DELETE a /v1/asignacion-docente/{id}', async () => {
      vi.mocked(axiosClient.delete).mockResolvedValue({});
      await eliminarAsignacion(42);
      expect(axiosClient.delete).toHaveBeenCalledWith('/v1/asignacion-docente/42');
    });
  });

  describe('obtenerEstudiantesPorCurso', () => {
    it('mapea respuesta separando nombre y apellido', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: [{ estudianteId: 'e1', rut: '11-1', nombre: 'María López' }],
      });
      const result = await obtenerEstudiantesPorCurso(1);
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/alumnos/1');
      expect(result).toEqual([{
        id: 'e1', rut: '11-1', nombre: 'María', apellido: 'López',
        email: '', promedio: 0, asistencia: 0,
      }]);
    });
  });

  describe('obtenerEstudiantesDisponibles', () => {
    it('filtra inactivos y mapea', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: [
          { id: 'e1', nombreCompleto: 'Alumno Uno', rut: '11-1' },
          { id: 'e2', nombreCompleto: 'Inactivo', activo: false },
        ],
      });
      const result = await obtenerEstudiantesDisponibles();
      expect(result).toHaveLength(1);
      expect(result[0].nombre).toBe('Alumno Uno');
    });
  });

  describe('matricularEstudiante', () => {
    it('hace POST con cursoId convertido a Number', async () => {
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { ok: true } });
      const result = await matricularEstudiante('3', 'e1');
      expect(axiosClient.post).toHaveBeenCalledWith('/v1/matriculas/matricular', {
        usuarioUuid: 'e1', cursoId: 3,
      });
      expect(result).toEqual({ ok: true });
    });
  });
});
