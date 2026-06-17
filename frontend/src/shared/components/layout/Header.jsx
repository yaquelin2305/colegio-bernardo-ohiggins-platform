import { LogOut } from 'lucide-react';
import { useAuth } from '../../../core/context/useAuth';
import '../../styles/Header.css';

function obtenerIniciales(nombre) {
  if (!nombre) return '?';
  const partes = nombre.trim().split(/\s+/);
  const primera = partes[0]?.[0] ?? '';
  const segunda = partes[1]?.[0] ?? '';
  return (primera + segunda).toUpperCase() || '?';
}

function Header({ titulo = 'Dashboard' }) {
  const { usuario, logout } = useAuth();
  const nombre = usuario?.nombre ?? 'Usuario';
  const iniciales = obtenerIniciales(usuario?.nombre);

  return (
    <header className="header">
      <h1 className="header__titulo">{titulo}</h1>

      <div className="header__usuario">
        <div className="header__info">
          <span className="header__avatar" aria-hidden="true">{iniciales}</span>
          <span className="header__nombre">{nombre}</span>
        </div>
        <button
          type="button"
          className="header__btn-salir"
          aria-label="Cerrar sesión"
          title="Cerrar sesión"
          onClick={logout}
        >
          <LogOut size={18} />
        </button>
      </div>
    </header>
  );
}

export default Header;
