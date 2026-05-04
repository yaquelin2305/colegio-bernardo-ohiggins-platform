import { LogOut } from 'lucide-react';
import '../../styles/Header.css';

function Header({ titulo = 'Dashboard' }) {
  return (
    <header className="header">
      <h1 className="header__titulo">{titulo}</h1>

      <div className="header__usuario">
        <div className="header__info">
          <span className="header__avatar" aria-hidden="true"></span>
          <span className="header__nombre"></span>
        </div>
        <button className="header__btn-salir" aria-label="Cerrar sesión" title="Cerrar sesión">
          <LogOut size={18} />
        </button>
      </div>
    </header>
  );
}

export default Header;
