import axiosClient from '../../../core/api/axiosClient';

export async function login(rut, password) {
  const sanitized = rut.replace(/[.\s]/g, '');
  const { data } = await axiosClient.post('/v1/auth/login', {
    rut: sanitized,
    password,
  });
  return data.accessToken;
}

// STUB: registro público no expuesto. Usar POST /v1/admin/crear (solo ADMIN). Mantener hasta que el equipo de backend lo exponga.
export async function registrarUsuario() {
  throw new Error('Registro de usuario no disponible.');
}
