import Sidebar from './Sidebar';
import Header from './Header';
import '../../styles/MainLayout.css';

function MainLayout({ children, titulo }) {
  return (
    <div className="main-layout">
      <Sidebar />

      <div className="main-layout__cuerpo">
        <Header titulo={titulo} />
        <main className="main-layout__contenido">
          {children}
        </main>
      </div>
    </div>
  );
}

export default MainLayout;
