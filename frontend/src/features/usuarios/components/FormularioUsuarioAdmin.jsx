import { useState, useEffect } from 'react';
import { CreditCard, User, Mail, Lock, ChevronDown, Users, X } from 'lucide-react';
import '../styles/FormularioUsuarioAdmin.css';

const estadoInicial = {
  rut: '', nombres: '', apellidos: '', email: '',
  password: '', confirmPassword: '', rol: '', apoderadoId: '',
};

function FormularioUsuarioAdmin({ onGuardar, apoderados = [], usuarioEditando = null, onCancelar }) {
  const modoEdicion = Boolean(usuarioEditando);

  const [formulario, setFormulario] = useState(estadoInicial);
  const [errores, setErrores] = useState({});
  const [mensajeExito, setMensajeExito] = useState('');

  useEffect(() => {
    if (usuarioEditando) {
      setFormulario({
        rut:             usuarioEditando.rut           || '',
        nombres:         usuarioEditando.nombres        || '',
        apellidos:       usuarioEditando.apellidos      || '',
        email:           usuarioEditando.email          || '',
        password:        '',
        confirmPassword: '',
        rol:             usuarioEditando.rol            || '',
        apoderadoId:     usuarioEditando.apoderadoId   || '',
      });
      setErrores({});
      setMensajeExito('');
    } else {
      setFormulario(estadoInicial);
      setErrores({});
      setMensajeExito('');
    }
  }, [usuarioEditando]);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormulario(prev => ({ ...prev, [name]: value }));
    if (errores[name]) setErrores(prev => ({ ...prev, [name]: '' }));
    if (mensajeExito) setMensajeExito('');
  }

  function validar() {
    const nuevosErrores = {};
    const camposRequeridos = ['rut', 'nombres', 'apellidos', 'email', 'rol'];

    camposRequeridos.forEach(campo => {
      if (!formulario[campo].trim()) {
        nuevosErrores[campo] = 'Este campo es obligatorio.';
      }
    });

    if (!modoEdicion) {
      if (!formulario.password.trim())        nuevosErrores.password        = 'Este campo es obligatorio.';
      if (!formulario.confirmPassword.trim()) nuevosErrores.confirmPassword = 'Este campo es obligatorio.';
    }

    if (formulario.password && formulario.confirmPassword && formulario.password !== formulario.confirmPassword) {
      nuevosErrores.confirmPassword = 'Las contraseñas no coinciden.';
    }

    if (formulario.rol === 'ESTUDIANTE' && !formulario.apoderadoId) {
      nuevosErrores.apoderadoId = 'Debes asignar un apoderado al estudiante.';
    }

    return nuevosErrores;
  }

  function handleSubmit(e) {
    e.preventDefault();
    const erroresValidacion = validar();
    if (Object.keys(erroresValidacion).length > 0) {
      setErrores(erroresValidacion);
      return;
    }

    onGuardar({ ...formulario, id: usuarioEditando?.id });
    setFormulario(estadoInicial);
    setErrores({});
    setMensajeExito(modoEdicion ? 'Usuario actualizado correctamente.' : 'Usuario creado correctamente.');
    setTimeout(() => setMensajeExito(''), 3000);
  }

  function campoInput({ name, label, icono, tipo = 'text', placeholder }) {
    return (
      <div className="form-usuario__grupo">
        <label className="form-usuario__label" htmlFor={name}>{label}</label>
        <div className={`form-usuario__input-wrapper${errores[name] ? ' form-usuario__input-wrapper--error' : ''}`}>
          <span className="form-usuario__icono" aria-hidden="true">{icono}</span>
          <input
            id={name}
            name={name}
            type={tipo}
            value={formulario[name]}
            onChange={handleChange}
            placeholder={placeholder}
            className="form-usuario__input"
            autoComplete="off"
          />
        </div>
        {errores[name] && <span className="form-usuario__error" role="alert">{errores[name]}</span>}
      </div>
    );
  }

  return (
    <form className="form-usuario" onSubmit={handleSubmit} noValidate>
      <div className="form-usuario__encabezado">
        <h3 className="form-usuario__titulo">
          {modoEdicion ? 'Editar Usuario' : 'Nuevo Usuario del Sistema'}
        </h3>
        <p className="form-usuario__subtitulo">
          {modoEdicion
            ? `Modificando datos de ${formulario.nombres} ${formulario.apellidos}`
            : 'Completa los datos del nuevo usuario'}
        </p>
      </div>

      <div className="form-usuario__campos">
        {campoInput({ name: 'rut', label: 'RUT', icono: <CreditCard size={16} />, placeholder: '12.345.678-9' })}

        <div className="form-usuario__fila">
          {campoInput({ name: 'nombres', label: 'Nombres', icono: <User size={16} />, placeholder: 'Juan Andrés' })}
          {campoInput({ name: 'apellidos', label: 'Apellidos', icono: <User size={16} />, placeholder: 'González Pérez' })}
        </div>

        {campoInput({ name: 'email', label: 'Correo electrónico', icono: <Mail size={16} />, tipo: 'email', placeholder: 'correo@ejemplo.cl' })}

        <div className="form-usuario__fila">
          {campoInput({
            name: 'password',
            label: modoEdicion ? 'Nueva contraseña (opcional)' : 'Contraseña',
            icono: <Lock size={16} />,
            tipo: 'password',
            placeholder: '••••••••',
          })}
          {campoInput({
            name: 'confirmPassword',
            label: 'Confirmar contraseña',
            icono: <Lock size={16} />,
            tipo: 'password',
            placeholder: '••••••••',
          })}
        </div>

        {/* ── Rol ── */}
        <div className="form-usuario__grupo">
          <label className="form-usuario__label" htmlFor="rol">Rol en el sistema</label>
          <div className={`form-usuario__input-wrapper${errores.rol ? ' form-usuario__input-wrapper--error' : ''}`}>
            <span className="form-usuario__icono" aria-hidden="true"><ChevronDown size={16} /></span>
            <select
              id="rol"
              name="rol"
              value={formulario.rol}
              onChange={handleChange}
              className="form-usuario__select"
              disabled={modoEdicion}
            >
              <option value="">Selecciona un rol</option>
              <option value="DOCENTE">Docente</option>
              <option value="APODERADO">Apoderado</option>
              <option value="ESTUDIANTE">Estudiante</option>
            </select>
          </div>
          {errores.rol && <span className="form-usuario__error" role="alert">{errores.rol}</span>}
        </div>

        {/* ── Apoderado (solo si rol = ESTUDIANTE) ── */}
        {formulario.rol === 'ESTUDIANTE' && (
          <div className="form-usuario__grupo">
            <label className="form-usuario__label" htmlFor="apoderadoId">Apoderado asignado</label>
            <div className={`form-usuario__input-wrapper${errores.apoderadoId ? ' form-usuario__input-wrapper--error' : ''}`}>
              <span className="form-usuario__icono" aria-hidden="true"><Users size={16} /></span>
              <select
                id="apoderadoId"
                name="apoderadoId"
                value={formulario.apoderadoId}
                onChange={handleChange}
                className="form-usuario__select"
              >
                <option value="">— Selecciona un apoderado —</option>
                {apoderados.map(ap => (
                  <option key={ap.id} value={ap.id}>
                    {ap.nombres} {ap.apellidos} ({ap.rut})
                  </option>
                ))}
              </select>
            </div>
            {errores.apoderadoId && <span className="form-usuario__error" role="alert">{errores.apoderadoId}</span>}
          </div>
        )}

        {mensajeExito && (
          <div className="form-usuario__exito" role="status">{mensajeExito}</div>
        )}

        <div className="form-usuario__acciones">
          <button type="submit" className="form-usuario__btn">
            {modoEdicion ? 'Guardar cambios' : 'Crear usuario'}
          </button>
          {modoEdicion && (
            <button type="button" className="form-usuario__btn-cancelar" onClick={onCancelar}>
              <X size={14} aria-hidden="true" />
              Cancelar
            </button>
          )}
        </div>
      </div>
    </form>
  );
}

export default FormularioUsuarioAdmin;
