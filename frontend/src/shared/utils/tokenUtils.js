import { TOKEN_KEY } from '../../core/constants/api.constants';

function decodeBase64Utf8(b64) {
  const bin = atob(b64.replace(/-/g, '+').replace(/_/g, '/'));
  const bytes = Uint8Array.from(bin, c => c.charCodeAt(0));
  return new TextDecoder('utf-8').decode(bytes);
}

function leerClaim(claim) {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) return null;
  try {
    const payload = JSON.parse(decodeBase64Utf8(token.split('.')[1]));
    return payload[claim] ?? null;
  } catch {
    return null;
  }
}

export function getUuidFromToken() {
  return leerClaim('userId') ?? leerClaim('sub');
}

export function getPupiloUuidFromToken() {
  return leerClaim('pupiloUuid');
}
