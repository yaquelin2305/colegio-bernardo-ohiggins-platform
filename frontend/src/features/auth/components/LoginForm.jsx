import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, Lock, AlertCircle } from 'lucide-react';
import '../styles/RegisterForm.css';

const initialState = {
  email: '',
  password: '',
};

function LoginForm() {
  const [form, setForm] = useState(initialState);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.email.trim() || !form.password.trim()) {
      setError('Por favor ingresa tu correo y contraseña.');
      return;
    }

    setLoading(true);
    setError('');

    setTimeout(() => {
      setLoading(false);
    }, 1000);
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
        {inputField({ name: 'email', label: 'Correo electrónico', icono: <Mail size={16} />, type: 'email', placeholder: 'correo@cbo.cl' })}
        {inputField({ name: 'password', label: 'Contraseña', icono: <Lock size={16} />, type: 'password', placeholder: '••••••••' })}

        {error && (
          <div className="register-form__error" style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '10px', backgroundColor: 'rgba(255, 152, 0, 0.1)', borderRadius: '8px' }}>
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <button type="submit" className="register-form__btn" disabled={loading}>
          {loading ? 'Iniciando sesión...' : 'Entrar'}
        </button>

        <p
          style={{
            textAlign: 'center',
            marginTop: '1rem',
            fontSize: '0.875rem',
            color: '#666',
          }}
        >
          ¿No tienes cuenta?{' '}
          <Link
            to="/registro"
            style={{
              color: '#26A69A',
              fontWeight: 600,
              textDecoration: 'none',
            }}
          >
            Regístrate aquí
          </Link>
        </p>
      </div>
    </form>
  );
}

export default LoginForm;