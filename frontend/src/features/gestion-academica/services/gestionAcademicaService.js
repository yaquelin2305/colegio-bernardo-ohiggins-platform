import axiosClient from '../../../core/api/axiosClient';
import { getUuidFromToken, getPupiloUuidFromToken } from '../../../shared/utils/tokenUtils';

function adaptarBoletin(bff) {
  return {
    alumno: {
      nombre: bff.nombreCompleto ?? '',
      rut: bff.rut ?? '',
      curso: bff.curso ?? '',
      periodo: String(new Date().getFullYear()),
      promedioGeneral: bff.promedioGeneral ?? 0,
      asistencia: bff.porcentajeAsistencia ?? 0,
    },
    asignaturas: (bff.calificaciones ?? []).map(c => ({
      id: c.asignaturaId,
      nombre: c.asignaturaNombre ?? `Asignatura ${c.asignaturaId}`,
      nota1: c.nota1 ?? 0,
      nota2: c.nota2 ?? 0,
      nota3: c.nota3 ?? 0,
      promedio: c.promedio ?? 0,
    })),
  };
}

// Dashboard

export async function obtenerKpisDashboard() {
  const { data } = await axiosClient.get('/bff/dashboard/stats');
  return [
    { label: 'Estudiantes',  numero: data.totalEstudiantes, iconKey: 'GraduationCap', detalle: 'matriculados' },
    { label: 'Docentes',     numero: data.totalDocentes,    iconKey: 'Users',          detalle: 'activos' },
    { label: 'Cursos',       numero: data.totalCursos,      iconKey: 'CalendarCheck',  detalle: 'este año' },
    { label: 'Asignaturas',  numero: data.totalAsignaturas, iconKey: 'TrendingUp',     detalle: 'en plan de estudio' },
  ];
}

// Calificaciones

export async function obtenerCalificaciones(cursoId, asignaturaId) {
  const { data } = await axiosClient.get(
    `/bff/calificaciones/curso/${cursoId}/asignatura/${asignaturaId}`
  );
  return data.map(item => ({
    id:          item.id,
    usuarioUuid: item.id,
    nombre:      item.nombre ?? item.id,
    nota1:       item.nota1   ?? 0,
    nota2:        item.nota2 ?? null,
    nota3:        item.nota3 ?? null,
    promedio:    item.promedio ?? 0,
  }));
}

export async function guardarCalificaciones(cursoId, asignaturaId, alumnos) {
  const payload = alumnos.map(alumno => ({
    usuarioUuid:  alumno.usuarioUuid ?? alumno.id,
    asignaturaId: Number(asignaturaId),
    nota1:        alumno.nota1 != null && alumno.nota1 !== '' ? Number(alumno.nota1) : null,
    nota2:        alumno.nota2 != null && alumno.nota2 !== '' ? Number(alumno.nota2) : null,
    nota3:        alumno.nota3 != null && alumno.nota3 !== '' ? Number(alumno.nota3) : null,
  }));
  await axiosClient.put('/bff/calificaciones/guardar', payload);
}

// Boletin

export async function obtenerBoletinPropio() {
  const uuid = getUuidFromToken();
  const { data } = await axiosClient.get(`/bff/boletin/${uuid}`);
  return adaptarBoletin(data);
}

export async function obtenerBoletinPupilo(pupilId) {
  const { data } = await axiosClient.get(`/bff/boletin/${pupilId}`);
  return adaptarBoletin(data);
}

export { getPupiloUuidFromToken };

// Cursos

export async function obtenerCursos() {
  const { data } = await axiosClient.get('/bff/cursos');
  return data;
}

export async function crearCurso(datos) {
  const { data } = await axiosClient.post('/v1/cursos/crear', datos);
  return data;
}

export async function obtenerCursoPorId(cursoId) {
  const { data } = await axiosClient.get(`/v1/cursos/${cursoId}`);
  return data;
}

// Asignaturas

export async function obtenerAsignaturas() {
  const { data } = await axiosClient.get('/bff/asignaturas');
  return data;
}

export async function crearAsignatura(datos) {
  const { data } = await axiosClient.post('/v1/asignaturas/crear', {
    ...datos,
    horasSemanales: Number(datos.horasSemanales),
  });
  return data;
}

// Docentes y asignaciones

export async function obtenerDocentes() {
  const { data } = await axiosClient.get('/bff/usuarios/DOCENTE');
  return data
    .filter(d => d.activo !== false)
    .map(d => ({
      id:     d.id,
      nombre: d.nombreCompleto ?? d.nombre ?? '',
    }));
}

export async function obtenerAsignaciones() {
  const { data } = await axiosClient.get('/v1/asignacion-docente');
  return data;
}

export async function crearAsignacion(payload) {
  const { data } = await axiosClient.post('/v1/asignacion-docente', {
    docenteUuid:  payload.docenteId,
    cursoId:      Number(payload.cursoId),
    asignaturaId: Number(payload.asignaturaId),
  });
  return data;
}

export async function eliminarAsignacion(id) {
  await axiosClient.delete(`/v1/asignacion-docente/${id}`);
}

// Matriculas y estudiantes

export async function obtenerEstudiantesPorCurso(cursoId) {
  const { data } = await axiosClient.get(`/bff/asistencia/alumnos/${cursoId}`);
  return data.map(d => {
    const partes = (d.nombre ?? '').split(' ');
    return {
      id:         d.estudianteId,
      rut:        d.rut ?? '',
      nombre:     partes[0] ?? d.estudianteId,
      apellido:   partes.slice(1).join(' '),
      email:      '',
      promedio:   0,
      asistencia: 0,
    };
  });
}

export async function obtenerEstudiantesDisponibles() {
  const { data } = await axiosClient.get('/bff/usuarios/ESTUDIANTE');
  return data
    .filter(e => e.activo !== false)
    .map(e => ({
      id:     e.id,
      nombre: e.nombreCompleto ?? e.nombre ?? '',
      rut:    e.rut ?? '',
    }));
}

export async function matricularEstudiante(cursoId, alumnoId) {
  const { data } = await axiosClient.post('/v1/matriculas/matricular', {
    usuarioUuid: alumnoId,
    cursoId:     Number(cursoId),
  });
  return data;
}
