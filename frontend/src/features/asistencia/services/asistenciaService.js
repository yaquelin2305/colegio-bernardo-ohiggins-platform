import axiosClient from '../../../core/api/axiosClient';

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

// Requiere ms-usuario — pendiente de integración
export async function obtenerAlumnos() {
  return [];
}

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

// Requiere ms-academico — pendiente de integración
export async function obtenerCursos() {
  return [];
}

// Requiere ms-academico — pendiente de integración
export async function obtenerAlumnosPorCurso(cursoId) {
  return [];
}

// ms-asistencia no soporta anotaciones — pendiente de integración
export async function guardarAnotacion(alumnoId, anotacion) {
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
