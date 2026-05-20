export function formatearFecha(iso) {
  if (!iso) return '—';
  const fecha = new Date(iso);
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
