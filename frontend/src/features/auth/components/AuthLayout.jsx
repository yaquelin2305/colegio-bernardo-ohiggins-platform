import '../styles/AuthLayout.css';

function AuthLayout({ children }) {
  return (
    <main className="auth-layout">
      <section className="auth-layout__info" aria-label="Información institucional">
        <span className="auth-layout__badge">Libro de Clases Digital</span>
        <div>
          <p className="auth-layout__subtitulo">CBO — Sistema de Gestión Académica</p>
          <h1 className="auth-layout__titulo">Gestiona tu aula de forma eficiente y moderna</h1>
        </div>
        <p className="auth-layout__descripcion">
          Centraliza el control de asistencia, calificaciones y comunicación con apoderados
          en una sola plataforma segura, diseñada para el Colegio Bernardo O'Higgins.
        </p>
      </section>

      <section className="auth-layout__form-col" aria-label="Contenido del formulario">
        {children}
      </section>
    </main>
  );
}

export default AuthLayout;
