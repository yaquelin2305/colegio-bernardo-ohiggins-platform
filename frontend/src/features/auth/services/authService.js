import axiosClient from '../../../core/api/axiosClient';

export async function login(rut, password) {
  const { data } = await axiosClient.post('/v1/auth/login', {
    rut: rut.replace(/[.\s-]/g, ''),
    password,
  });
  return data.accessToken;
}

export async function registrarUsuario(datos) {
  const { data } = await axiosClient.post('/v1/auth/login', {
    rut: datos.rut,
    password: datos.password,
  });
  return data.accessToken;
}
