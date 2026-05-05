import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';
import '../../styles/MainLayout.css';

function MainLayout() {
  const [titulo, setTitulo] = useState('Dashboard');

  return (
    <div className="main-layout">
      <Sidebar />

      <div className="main-layout__cuerpo">
        <Header titulo={titulo} />
        <main className="main-layout__contenido">
          <Outlet context={{ setTitulo }} />
        </main>
      </div>
    </div>
  );
}

export default MainLayout;
