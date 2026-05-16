import axiosClient from '../../../core/api/axiosClient';

function mapeoUsuario(u) {
  const [nombres = '', ...resto] = (u.nombreCompleto ?? '').split(' ');
  return {
    id: u.id,
    rut: u.rut ?? '',
    nombres,
    apellidos: resto.join(' '),
    email: u.email ?? '',
    rol: u.rol,
    pupiloUuid: u.pupiloUuid ?? null,
    pupiloNombre: u.pupiloNombre ?? null,
  };
}

async function listarPorRol(rol) {
  const { data } = await axiosClient.get(`/v1/admin/listar/${rol}`);
  return data.map(mapeoUsuario);
}

export const obtenerDocentes    = () => listarPorRol('DOCENTE');
export const obtenerApoderados  = () => listarPorRol('APODERADO');
export const obtenerEstudiantes = () => listarPorRol('ESTUDIANTE');

export async function crearUsuario(payload) {
  const { data } = await axiosClient.post('/v1/admin/crear', {
    rut: payload.rut,
    nombre: payload.nombres,
    apellido: payload.apellidos,
    email: payload.email,
    password: payload.password,
    rol: payload.rol,
    pupiloUuid: payload.pupiloUuid || null,
  });
  return mapeoUsuario(data);
}

export async function actualizarUsuario(id, payload) {
  const { data } = await axiosClient.put(`/v1/admin/actualizar/${id}`, {
    nombre: payload.nombres,
    apellido: payload.apellidos,
    email: payload.email,
    pupiloUuid: payload.pupiloUuid ?? null,
  });
  return mapeoUsuario(data);
}

export async function eliminarUsuario(id) {
  await axiosClient.delete(`/v1/admin/eliminar/${id}`);
}
