import axiosClient from '../../../core/api/axiosClient';
import { TOKEN_KEY } from '../../../core/constants/api.constants';

function getUuidFromToken() {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.userId ?? payload.uuid ?? payload.sub ?? null;
  } catch {
    return null;
  }
}

function adaptarBoletin(bff) {
  return {
    alumno: {
      nombre: bff.nombreCompleto ?? '',
      rut: '',
      curso: '',
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

export async function obtenerKpisDashboard(rol) {
  const { data } = await axiosClient.get('/bff/dashboard/stats');
  return [
    { label: 'Estudiantes',  valor: data.totalEstudiantes,  iconKey: 'GraduationCap' },
    { label: 'Docentes',     valor: data.totalDocentes,      iconKey: 'Users' },
    { label: 'Cursos',       valor: data.totalCursos,        iconKey: 'CalendarCheck' },
    { label: 'Asignaturas',  valor: data.totalAsignaturas,   iconKey: 'TrendingUp' },
  ];
}

// Calificaciones

export async function obtenerCalificaciones(cursoId, asignaturaId) {
  const { data } = await axiosClient.get(
    `/v1/calificaciones/curso/${cursoId}/asignatura/${asignaturaId}`
  );
  return data.map(item => ({
    id:          item.usuarioUuid,
    usuarioUuid: item.usuarioUuid,
    rut:         '',
    nombre:      item.usuarioUuid,
    nota1:       item.nota1   ?? 0,
    nota2:       item.nota2   ?? 0,
    nota3:       item.nota3   ?? 0,
    promedio:    item.promedio ?? 0,
  }));
}

export async function guardarCalificaciones(cursoId, asignaturaId, alumnos) {
  await Promise.all(
    alumnos.map(alumno =>
      axiosClient.put('/v1/calificaciones/guardar', {
        usuarioUuid:  alumno.usuarioUuid ?? alumno.id,
        asignaturaId: Number(asignaturaId),
        nota1:        Number(alumno.nota1),
        nota2:        Number(alumno.nota2),
        nota3:        Number(alumno.nota3),
      })
    )
  );
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

// TODO: ms-usuario no expone endpoint de pupilos por apoderado
export async function obtenerPupilos() {
  return Promise.resolve([]);
}

// Cursos

export async function obtenerCursos() {
  const { data } = await axiosClient.get('/v1/cursos');
  return data;
}

export async function crearCurso(datos) {
  const { data } = await axiosClient.post('/v1/cursos/crear', datos);
  return data;
}

// TODO: ms-academico no expone GET /api/v1/cursos/{id} — workaround: listar y filtrar
export async function obtenerCursoPorId(cursoId) {
  const { data } = await axiosClient.get('/v1/cursos');
  return data.find(c => String(c.id) === String(cursoId)) ?? null;
}

// Asignaturas

export async function obtenerAsignaturas() {
  const { data } = await axiosClient.get('/v1/asignaturas');
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
  const { data } = await axiosClient.get('/v1/admin/listar/DOCENTE');
  return data.map(d => ({
    id:     d.id,
    nombre: d.nombreCompleto ?? d.nombre ?? '',
  }));
}

// TODO: ms-academico no expone GET /api/v1/asignacion-docente
export async function obtenerAsignaciones() {
  return Promise.resolve([]);
}

export async function crearAsignacion(payload) {
  const { data } = await axiosClient.post('/v1/asignacion-docente', {
    docenteUuid:  payload.docenteId,
    cursoId:      Number(payload.cursoId),
    asignaturaId: Number(payload.asignaturaId),
  });
  return data;
}

// TODO: ms-academico no expone DELETE /api/v1/asignacion-docente/{id}
export async function eliminarAsignacion(id) {
  return Promise.resolve(null);
}

// Matriculas y estudiantes

export async function obtenerEstudiantesPorCurso(cursoId) {
  const { data } = await axiosClient.get(`/v1/matriculas/curso/${cursoId}/estudiantes`);
  return data.map(m => ({
    id:        m.usuarioUuid,
    rut:       '',
    nombre:    m.usuarioUuid,
    apellido:  '',
    email:     '',
    promedio:  0,
    asistencia: 0,
  }));
}

export async function obtenerEstudiantesDisponibles() {
  const { data } = await axiosClient.get('/v1/admin/listar/ESTUDIANTE');
  return data.map(e => ({
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
