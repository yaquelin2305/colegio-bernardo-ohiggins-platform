export function formatearFecha(iso) {
  if (!iso) return '—';
  const normalized = (iso.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(iso)) ? iso : iso + 'Z';
  const fecha = new Date(normalized);
  if (isNaN(fecha.getTime())) return iso;
  return fecha.toLocaleString('es-CL', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    timeZone: 'America/Santiago',
  });
}
