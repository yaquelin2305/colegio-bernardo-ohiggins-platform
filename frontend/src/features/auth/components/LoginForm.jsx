import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Lock, AlertCircle } from 'lucide-react';
import { login } from '../services/authService';
import { useAuth } from '../../../core/context/useAuth';
import '../styles/LoginForm.css';

const initialState = {
  rut: '',
  password: '',
};

function LoginForm() {
  const [form, setForm] = useState(initialState);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const auth = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.rut.trim() || !form.password.trim()) {
      setError('Por favor ingresa tu RUT y contraseña.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const rutasPorRol = {
        ADMIN: '/dashboard',
        DOCENTE: '/calificaciones',
        APODERADO: '/mis-calificaciones',
        ESTUDIANTE: '/mis-calificaciones',
      };
      const token = await login(form.rut, form.password);
      const b64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(atob(b64));
      const rol = payload.role || payload.rol;
      auth.login(token);

      const destino = rutasPorRol[rol] || '/dashboard';
      navigate(destino, { replace: true });
    } catch (err) {
      const mensaje = err.response?.data?.mensaje
        || err.response?.data?.detail
        || err.message
        || 'Error al iniciar sesión.';
      setError(mensaje);
    } finally {
      setLoading(false);
    }
  };

  const inputField = ({ name, label, icon, type = 'text', placeholder }) => (
    <div className="register-form__grupo">
      <label className="register-form__label" htmlFor={name}>{label}</label>
      <div className={`register-form__input-wrapper${error && name === 'password' ? ' register-form__input-wrapper--error' : ''}`}>
        <span className="register-form__icono" style={{ display: 'flex', alignItems: 'center' }}>{icon}</span>
        <input
          id={name}
          name={name}
          type={type}
          value={form[name]}
          onChange={handleChange}
          placeholder={placeholder}
          className="register-form__input"
          disabled={loading}
          autoComplete="off"
        />
      </div>
    </div>
  );

  return (
    <form className="register-form" onSubmit={handleSubmit} noValidate>
      <h2 className="register-form__titulo">Iniciar Sesión</h2>
      <p className="register-form__subtitulo">Accede al sistema con tus credenciales</p>

      <div className="register-form__campos">
        {inputField({ name: 'rut', label: 'RUT', icon: <User size={16} />, placeholder: '12345678-9' })}
        {inputField({ name: 'password', label: 'Contraseña', icon: <Lock size={16} />, type: 'password', placeholder: '••••••••' })}

        {error && (
          <div className="register-form__error" style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '10px', backgroundColor: 'rgba(255, 152, 0, 0.1)', borderRadius: '8px' }}>
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <button type="submit" className="register-form__btn" disabled={loading}>
          {loading ? 'Iniciando sesión...' : 'Entrar'}
        </button>

      </div>
    </form>
  );
}

export default LoginForm;
