import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../core/context/useAuth';

function NotFoundPage() {
  const navigate = useNavigate();
  const { usuario } = useAuth();

  const handleVolver = () => {
    if (!usuario) { navigate('/login'); return; }
    const destino = ['APODERADO', 'ESTUDIANTE'].includes(usuario.rol)
      ? '/mis-calificaciones'
      : '/dashboard';
    navigate(destino);
  };

  return (
    <main style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center',
      justifyContent: 'center', minHeight: '100vh', gap: '1.5rem',
      backgroundColor: 'var(--color-background)', color: 'var(--color-primary)',
      fontFamily: 'var(--font-main)',
    }}>
      <h1 style={{ fontSize: '4rem', margin: 0 }}>404</h1>
      <p style={{ fontSize: '1.2rem', color: 'var(--color-text-secondary)' }}>
        Página no encontrada
      </p>
      <button
        onClick={handleVolver}
        style={{
          padding: '0.6rem 1.4rem', borderRadius: '6px', border: 'none', cursor: 'pointer',
          backgroundColor: 'var(--color-primary)', color: '#fff', fontSize: '0.95rem',
        }}
      >
        Volver al inicio
      </button>
    </main>
  );
}

export default NotFoundPage;
