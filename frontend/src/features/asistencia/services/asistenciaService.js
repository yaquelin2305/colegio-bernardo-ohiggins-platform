import axiosClient from '../../../core/api/axiosClient';
import { obtenerCursos as obtenerCursosAcademico, obtenerEstudiantesPorCurso } from '../../gestion-academica/services/gestionAcademicaService';
import { obtenerEstudiantes } from '../../usuarios/services/usuariosService';

export async function obtenerResumenDiario(cursoId, fecha) {
  if (!cursoId) return null;
  const response = await axiosClient.get('/bff/asistencia/resumen', {
    params: { cursoId, fecha },
  });
  return response.data;
}

export async function obtenerAsistenciaPorCurso(cursoId, fecha) {
  const response = await axiosClient.get(`/bff/asistencia/curso/${cursoId}`, {
    params: { fecha },
  });
  return response.data;
}

export const obtenerAlumnos = obtenerEstudiantes;

export async function obtenerHistorialAsistencia(alumnoId) {
  const response = await axiosClient.get(`/bff/asistencia/estudiante/${alumnoId}`);
  return response.data;
}

export async function obtenerInasistencias() {
  const response = await axiosClient.get('/bff/asistencia/inasistencias');
  return response.data;
}

export async function justificarInasistencia(inasistenciaId, payload) {
  const response = await axiosClient.patch(
    `/bff/asistencia/${inasistenciaId}/justificar`,
    { motivo: payload.motivo }
  );
  return response.data;
}

export const obtenerCursos = obtenerCursosAcademico;

export const obtenerAlumnosPorCurso = obtenerEstudiantesPorCurso;

// STUB: ms-asistencia no soporta anotaciones. Mantener hasta que el equipo de backend lo exponga.
export async function guardarAnotacion() {
  return null;
}

export async function guardarAsistencia(cursoId, fecha, listado) {
  const body = listado.map((item) => ({
    estudianteId: item.nombre,
    cursoId,
    estado: item.estado.toUpperCase(),
    observacion: null,
    fecha,
  }));
  const response = await axiosClient.post('/bff/asistencia/registrar', body);
  return response.data;
}
