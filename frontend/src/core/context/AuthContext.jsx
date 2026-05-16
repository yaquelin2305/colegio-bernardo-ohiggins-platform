import { useState } from 'react';
import { TOKEN_KEY } from '../constants/api.constants';
import { AuthContext } from './authContext';

function decodificarToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
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
