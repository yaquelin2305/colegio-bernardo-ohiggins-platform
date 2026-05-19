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

export async function obtenerAlumnos() {
  const lista = await obtenerEstudiantes();
  return lista.map(u => ({
    id: u.id,
    nombre: `${u.nombres} ${u.apellidos}`.trim(),
    rut: u.rut,
    curso: '',
  }));
}

export async function obtenerHistorialAsistencia(alumnoId) {
  const response = await axiosClient.get(`/bff/asistencia/estudiante/${alumnoId}`);
  return response.data;
}

export async function obtenerInasistencias() {
  const [response, cursos] = await Promise.all([
    axiosClient.get('/bff/asistencia/inasistencias'),
    obtenerCursosAcademico(),
  ]);
  return response.data.map(item => ({
    ...item,
    curso: cursos.find(c => c.id == item.curso)?.nombre ?? String(item.curso ?? ''),
  }));
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

export async function guardarAnotacion(estudianteId, { tipo, descripcion }) {
  const { data } = await axiosClient.post('/bff/asistencia/anotaciones', {
    estudianteId,
    tipo,
    descripcion,
  });
  return data;
}

export async function obtenerAnotacionesPorEstudiante(estudianteId) {
  const { data } = await axiosClient.get(`/bff/asistencia/anotaciones/estudiante/${estudianteId}`);
  return data;
}

export async function guardarAsistencia(cursoId, fecha, listado) {
  const body = listado.map((item) => ({
    estudianteId: item.estudianteId || item.nombre,
    cursoId,
    estado: item.estado.toUpperCase(),
    observacion: null,
    fecha,
  }));
  const response = await axiosClient.post('/bff/asistencia/registrar', body);
  return response.data;
}
