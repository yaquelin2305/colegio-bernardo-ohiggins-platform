import { createContext, useContext, useState } from 'react';
import { TOKEN_KEY } from '../constants/api.constants';

const AuthContext = createContext(null);

function decodificarToken(token) {
  try {
    return JSON.parse(atob(token.split('.')[1]));
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

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider');
  }
  return context;
}
