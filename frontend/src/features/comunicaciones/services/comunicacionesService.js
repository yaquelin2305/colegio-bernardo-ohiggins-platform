import axiosClient from '../../../core/api/axiosClient';
import { resolverNombresPorIds, obtenerUsuarioPorId } from '../../../shared/services/usuariosLookup';

const UUID_SISTEMA = '00000000-0000-0000-0000-000000000000';

function nombreDe(uuid, lookup) {
  if (!uuid) return '';
  if (uuid === UUID_SISTEMA) return 'Sistema';
  return lookup[uuid]?.nombreCompleto ?? uuid;
}

export async function obtenerMensajes(usuarioId) {
  const { data } = await axiosClient.get(`/bff/comunicaciones/bandeja/${usuarioId}`);
  const lookup = await resolverNombresPorIds(data.map(m => m.remitente));
  return data.map(m => ({ ...m, remitente: nombreDe(m.remitente, lookup) }));
}

export async function obtenerMensajePorId(id) {
  const { data } = await axiosClient.get(`/bff/comunicaciones/${id}`);
  if (data.remitente === UUID_SISTEMA) return { ...data, remitente: 'Sistema' };
  const info = await obtenerUsuarioPorId(data.remitente);
  return { ...data, remitente: info?.nombreCompleto ?? data.remitente };
}

export async function enviarMensaje(payload) {
  const { destinatario, asunto, mensaje, canal, tipo } = payload;
  const { data } = await axiosClient.post('/bff/comunicaciones/enviar', {
    destinatario,
    asunto,
    mensaje,
    canal,
    tipo,
  });
  return data;
}

export async function obtenerDestinatarios() {
  const { data } = await axiosClient.get('/bff/comunicaciones/destinatarios');
  const uuids = data.map(d => d.id);
  const lookup = await resolverNombresPorIds(uuids);
  return data.map(d => ({
    id: d.id,
    nombre: lookup[d.id]?.nombreCompleto ?? d.nombre ?? d.id,
  }));
}
