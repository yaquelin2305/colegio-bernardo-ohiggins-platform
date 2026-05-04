import LoginForm from '../components/LoginForm';
import '../styles/RegisterPage.css';

const estadisticas = [
  { numero: '1,250+', label: 'Estudiantes' },
  { numero: '85', label: 'Docentes' },
  { numero: '42', label: 'Cursos activos' },
  { numero: '98%', label: 'Tasa de asistencia' },
];

function LoginPage() {
  return (
    <main className="register-page">
      <section
        className="register-page__info"
        aria-label="Información institucional"
      >
        <span className="register-page__badge">Libro de Clases Digital</span>

        <div>
          <p className="register-page__subtitulo">
            CBO — Sistema de Gestión Académica
          </p>
          <h1 className="register-page__titulo">
            Accede a tu cuenta institucional
          </h1>
        </div>

        <p className="register-page__descripcion">
          Ingresa al sistema de gestión académica del Colegio Bernardo
          O'Higgins. Controla asistencia, calificaciones y comunicación con tu
          comunidad educativa.
        </p>

        <div className="register-page__stats">
          {estadisticas.map((stat) => (
            <div key={stat.label} className="register-page__stat-card">
              <span className="register-page__stat-numero">
                {stat.numero}
              </span>
              <span className="register-page__stat-label">
                {stat.label}
              </span>
            </div>
          ))}
        </div>
      </section>

      <section
        className="register-page__form-col"
        aria-label="Formulario de inicio de sesión"
      >
        <br />
        <LoginForm />
      </section>
    </main>
  );
}

export default LoginPage;