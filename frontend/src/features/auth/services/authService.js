import axiosClient from '../../../core/api/axiosClient';

export async function login(rut, password) {
  const sanitized = rut.replace(/[.\s]/g, '');
  const { data } = await axiosClient.post('/v1/auth/login', {
    rut: sanitized,
    password,
  });
  return data.accessToken;
}
