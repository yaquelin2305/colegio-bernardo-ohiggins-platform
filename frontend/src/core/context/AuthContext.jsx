import { createContext, useState } from 'react';
import { TOKEN_KEY } from '../constants/api.constants';

export const AuthContext = createContext(null);

function decodeBase64Utf8(b64) {
  const bin = atob(b64.replace(/-/g, '+').replace(/_/g, '/'));
  const bytes = Uint8Array.from(bin, c => c.charCodeAt(0));
  return new TextDecoder('utf-8').decode(bytes);
}

function decodificarToken(token) {
  try {
    const payload = JSON.parse(decodeBase64Utf8(token.split('.')[1]));
    return {
      ...payload,
      rol: payload.role,
      nombre: payload.nombre,
    };
  } catch {
    return null;
  }
}

function obtenerTokenInicial() {
  return localStorage.getItem(TOKEN_KEY);
}

export function AuthProvider({ children }) {
  const tokenInicial = obtenerTokenInicial();

  const [token, setToken] = useState(tokenInicial);
  const [usuario, setUsuario] = useState(
    tokenInicial ? decodificarToken(tokenInicial) : null
  );

  function login(nuevoToken) {
    localStorage.setItem(TOKEN_KEY, nuevoToken);
    setToken(nuevoToken);
    setUsuario(decodificarToken(nuevoToken));
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
    setUsuario(null);
    window.location.href = '/login';
  }

  return (
    <AuthContext.Provider value={{ usuario, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
