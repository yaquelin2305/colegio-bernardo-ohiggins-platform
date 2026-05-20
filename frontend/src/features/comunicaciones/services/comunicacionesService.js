import axiosClient from '../../../core/api/axiosClient';

export async function obtenerMensajes(usuarioId) {
  const { data } = await axiosClient.get(`/bff/comunicaciones/bandeja/${usuarioId}`);
  return data;
}

export async function obtenerMensajePorId(id) {
  const { data } = await axiosClient.get(`/bff/comunicaciones/${id}`);
  return data;
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
  return data;
}

export async function marcarLeido(mensajeId) {
  const { data } = await axiosClient.patch(`/bff/comunicaciones/leido/${mensajeId}`);
  return data;
}
