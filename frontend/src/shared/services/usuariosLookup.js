import axiosClient from '../../core/api/axiosClient';

const cache = new Map();

export async function obtenerUsuarioPorId(id) {
  if (cache.has(id)) return cache.get(id);
  try {
    const { data } = await axiosClient.get(`/v1/usuarios/${id}/nombre`);
    cache.set(id, data);
    return data;
  } catch {
    return null;
  }
}

export async function resolverNombresPorIds(ids) {
  const unicos = [...new Set(ids.filter(Boolean))];
  const usuarios = await Promise.all(unicos.map(obtenerUsuarioPorId));
  return Object.fromEntries(unicos.map((id, i) => [id, usuarios[i]]));
}
