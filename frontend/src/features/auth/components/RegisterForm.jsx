import { useState } from 'react';
import { CreditCard, User, Mail, Lock, ChevronDown } from 'lucide-react';
import '../styles/RegisterForm.css';

const estadoInicial = {
  rut: '',
  nombres: '',
  apellidos: '',
  email: '',
  password: '',
  confirmPassword: '',
  rol: '',
};

function RegisterForm() {
  const [formulario, setFormulario] = useState(estadoInicial);
  const [errores, setErrores] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [mensajeExito, setMensajeExito] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormulario((prev) => ({ ...prev, [name]: value }));
    if (errores[name]) {
      setErrores((prev) => ({ ...prev, [name]: '' }));
    }
    if (mensajeExito) setMensajeExito('');
  };

  const validar = () => {
    const nuevosErrores = {};
    const camposRequeridos = ['rut', 'nombres', 'apellidos', 'email', 'password', 'confirmPassword', 'rol'];

    camposRequeridos.forEach((campo) => {
      if (!formulario[campo].trim()) {
        nuevosErrores[campo] = 'Este campo es obligatorio.';
      }
    });

    if (formulario.password && formulario.confirmPassword && formulario.password !== formulario.confirmPassword) {
      nuevosErrores.confirmPassword = 'Las contraseñas no coinciden.';
    }

    return nuevosErrores;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const erroresValidacion = validar();
    if (Object.keys(erroresValidacion).length > 0) {
      setErrores(erroresValidacion);
      return;
    }

    setIsLoading(true);
    setErrores({});

    setTimeout(() => {
      console.log('Nuevo usuario registrado:', { ...formulario });
      setFormulario(estadoInicial);
      setMensajeExito('¡Cuenta creada exitosamente!');
      setIsLoading(false);
    }, 1500);
  };

  const campoInput = ({ name, label, icono, tipo = 'text', placeholder }) => (
    <div className="register-form__grupo">
      <label className="register-form__label" htmlFor={name}>{label}</label>
      <div className={`register-form__input-wrapper${errores[name] ? ' register-form__input-wrapper--error' : ''}`}>
        <span className="register-form__icono" aria-hidden="true" style={{ display: 'flex', alignItems: 'center' }}>{icono}</span>
        <input
          id={name}
          name={name}
          type={tipo}
          value={formulario[name]}
          onChange={handleChange}
          placeholder={placeholder}
          className="register-form__input"
          disabled={isLoading}
          autoComplete="off"
        />
      </div>
      {errores[name] && <span className="register-form__error" role="alert">{errores[name]}</span>}
    </div>
  );

  return (
    <form className="register-form" onSubmit={handleSubmit} noValidate>
      <h2 className="register-form__titulo">Crear Cuenta</h2>
      <p className="register-form__subtitulo">Completa los datos para registrarte en el sistema</p>

      <div className="register-form__campos">
        {campoInput({ name: 'rut', label: 'RUT', icono: <CreditCard size={16} />, placeholder: '12.345.678-9' })}

        <div className="register-form__fila">
          {campoInput({ name: 'nombres', label: 'Nombres', icono: <User size={16} />, placeholder: 'Juan Andrés' })}
          {campoInput({ name: 'apellidos', label: 'Apellidos', icono: <User size={16} />, placeholder: 'González Pérez' })}
        </div>

        {campoInput({ name: 'email', label: 'Correo electrónico', icono: <Mail size={16} />, tipo: 'email', placeholder: 'correo@ejemplo.cl' })}

        <div className="register-form__fila">
          {campoInput({ name: 'password', label: 'Contraseña', icono: <Lock size={16} />, tipo: 'password', placeholder: '••••••••' })}
          {campoInput({ name: 'confirmPassword', label: 'Confirmar contraseña', icono: <Lock size={16} />, tipo: 'password', placeholder: '••••••••' })}
        </div>

        <div className="register-form__grupo">
          <label className="register-form__label" htmlFor="rol">Rol en el sistema</label>
          <div className={`register-form__input-wrapper${errores.rol ? ' register-form__input-wrapper--error' : ''}`}>
            <span className="register-form__icono" aria-hidden="true"><ChevronDown size={16} /></span>
            <select
              id="rol"
              name="rol"
              value={formulario.rol}
              onChange={handleChange}
              className="register-form__select"
              disabled={isLoading}
            >
              <option value="">Selecciona un rol</option>
              <option value="ESTUDIANTE">Estudiante</option>
              <option value="APODERADO">Apoderado</option>
            </select>
          </div>
          {errores.rol && <span className="register-form__error" role="alert">{errores.rol}</span>}
        </div>

        {mensajeExito && (
          <div className="register-form__exito" role="status">
            {mensajeExito}
          </div>
        )}

        <button type="submit" className="register-form__btn" disabled={isLoading}>
          {isLoading ? 'Registrando...' : 'Crear cuenta'}
        </button>
      </div>
    </form>
  );
}

export default RegisterForm;
