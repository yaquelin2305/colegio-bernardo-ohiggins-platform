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

export async function obtenerCursos() {
  const { data } = await axiosClient.get('/bff/cursos');
  return data;
}

export async function obtenerAlumnosPorCurso(cursoId) {
  const { data } = await axiosClient.get(`/bff/asistencia/alumnos/${cursoId}`);
  return data.map(d => {
    const partes = (d.nombre ?? '').split(' ');
    return {
      id:       d.estudianteId,
      rut:      d.rut ?? '',
      nombre:   partes[0] ?? d.estudianteId,
      apellido: partes.slice(1).join(' '),
    };
  });
}

export async function obtenerAlumnosPorDocente() {
  const cursos = await obtenerCursos();
  const grupos = await Promise.all(
    cursos.map(c =>
      obtenerAlumnosPorCurso(c.id).then(alums =>
        alums.map(a => ({
          id:     a.id,
          nombre: `${a.nombre} ${a.apellido}`.trim(),
          rut:    a.rut ?? '',
          curso:  c.nombre,
        }))
      )
    )
  );
  const seen = new Set();
  return grupos.flat().filter(a => {
    if (seen.has(a.id)) return false;
    seen.add(a.id);
    return true;
  });
}

export async function obtenerAlumnos() {
  const { data } = await axiosClient.get('/bff/usuarios/ESTUDIANTE');
  return data.map(u => ({
    id:     u.id,
    nombre: u.nombreCompleto ?? '',
    rut:    u.rut ?? '',
    curso:  '',
  }));
}

export async function obtenerHistorialAsistencia(alumnoId) {
  const response = await axiosClient.get(`/bff/asistencia/estudiante/${alumnoId}`);
  return response.data;
}

export async function obtenerInasistencias() {
  const { data } = await axiosClient.get('/bff/asistencia/inasistencias');
  return data;
}

export async function justificarInasistencia(inasistenciaId, payload) {
  const response = await axiosClient.patch(
    `/bff/asistencia/${inasistenciaId}/justificar`,
    { motivo: payload.motivo }
  );
  return response.data;
}

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
