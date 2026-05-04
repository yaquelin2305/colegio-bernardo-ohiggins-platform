import { useState, useEffect } from 'react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import FormularioUsuarioAdmin from '../components/FormularioUsuarioAdmin';
import TabsUsuarios from '../components/TabsUsuarios';
import TablaUsuarios from '../components/TablaUsuarios';
import {
  obtenerDocentes,
  obtenerApoderados,
  obtenerEstudiantes,
  crearUsuario,
  actualizarUsuario,
  eliminarUsuario,
} from '../services/usuariosService';
import '../styles/GestionUsuariosPage.css';

function GestionUsuariosPage() {
  const [tabActiva, setTabActiva] = useState('docentes');
  const [docentes, setDocentes]       = useState([]);
  const [apoderados, setApoderados]   = useState([]);
  const [estudiantes, setEstudiantes] = useState([]);
  const [usuarioEditando, setUsuarioEditando]       = useState(null);
  const [confirmarEliminarId, setConfirmarEliminarId] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError]         = useState(null);

  useEffect(() => {
    Promise.all([obtenerDocentes(), obtenerApoderados(), obtenerEstudiantes()])
      .then(([datosDocentes, datosApoderados, datosEstudiantes]) => {
        setDocentes(datosDocentes);
        setApoderados(datosApoderados);
        setEstudiantes(datosEstudiantes);
      })
      .catch(() => setError('No se pudo cargar el listado de usuarios.'))
      .finally(() => setIsLoading(false));
  }, []);

  function getLista() {
    if (tabActiva === 'docentes')   return docentes;
    if (tabActiva === 'apoderados') return apoderados;
    return estudiantes;
  }

  function setLista(fn) {
    if (tabActiva === 'docentes')        setDocentes(fn);
    else if (tabActiva === 'apoderados') setApoderados(fn);
    else                                 setEstudiantes(fn);
  }

  async function handleGuardarDesdeFormulario(payload) {
    const { id, rol, rut, nombres, apellidos, email, apoderadoId } = payload;

    try {
      if (id) {
        await actualizarUsuario(id, payload);
        const actualizarEnLista = (setter) => setter(prev =>
          prev.map(u => {
            if (u.id !== id) return u;
            const base = { ...u, rut, nombres, apellidos, email };
            if (rol === 'ESTUDIANTE') {
              const ap = apoderados.find(a => a.id === apoderadoId);
              return { ...base, apoderadoId, apoderado: ap ? `${ap.nombres} ${ap.apellidos}` : u.apoderado };
            }
            return base;
          })
        );
        if (rol === 'DOCENTE')        actualizarEnLista(setDocentes);
        else if (rol === 'APODERADO') actualizarEnLista(setApoderados);
        else                          actualizarEnLista(setEstudiantes);
        setUsuarioEditando(null);
      } else {
        await crearUsuario(payload);
        if (rol === 'DOCENTE') {
          setDocentes(prev => [...prev, { id: `d${Date.now()}`, rut, nombres, apellidos, email, rol }]);
        } else if (rol === 'APODERADO') {
          setApoderados(prev => [...prev, { id: `ap${Date.now()}`, rut, nombres, apellidos, email, rol }]);
        } else if (rol === 'ESTUDIANTE') {
          const ap = apoderados.find(a => a.id === apoderadoId);
          setEstudiantes(prev => [...prev, {
            id: `e${Date.now()}`, rut, nombres, apellidos, email, rol,
            apoderadoId, apoderado: ap ? `${ap.nombres} ${ap.apellidos}` : '—',
          }]);
        }
      }
    } catch {
      setError('No se pudo guardar el usuario. Intenta nuevamente.');
    }
  }

  function handleEditar(usuario) {
    setConfirmarEliminarId(null);
    setUsuarioEditando(usuario);
    if (usuario.rol === 'DOCENTE')        setTabActiva('docentes');
    else if (usuario.rol === 'APODERADO') setTabActiva('apoderados');
    else                                  setTabActiva('estudiantes');
  }

  async function handleEliminar(id) {
    try {
      await eliminarUsuario(id);
      setLista(prev => prev.filter(u => u.id !== id));
      setConfirmarEliminarId(null);
    } catch {
      setError('No se pudo eliminar el usuario. Intenta nuevamente.');
    }
  }

  function handleCambiarTab(tab) {
    setTabActiva(tab);
    setUsuarioEditando(null);
    setConfirmarEliminarId(null);
  }

  const lista = getLista();
  const columnas = tabActiva === 'estudiantes'
    ? ['RUT', 'Nombre', 'Correo', 'Apoderado']
    : ['RUT', 'Nombre', 'Correo'];

  return (
    <MainLayout titulo="Gestión de Usuarios">
      <div className="gestion-usuarios">

        <TabsUsuarios tabActiva={tabActiva} onCambiarTab={handleCambiarTab} />

        {isLoading && <p className="gestion-usuarios__cargando">Cargando...</p>}
        {error && <p className="gestion-usuarios__error">{error}</p>}

        {!isLoading && (
          <div className="gestion-usuarios__contenido">

            <section className="gestion-usuarios__seccion gestion-usuarios__seccion--formulario" aria-label="Crear o editar usuario">
              <FormularioUsuarioAdmin
                onGuardar={handleGuardarDesdeFormulario}
                apoderados={apoderados}
                usuarioEditando={usuarioEditando}
                onCancelar={() => setUsuarioEditando(null)}
              />
            </section>

            <TablaUsuarios
              lista={lista}
              tabActiva={tabActiva}
              columnas={columnas}
              usuarioEditando={usuarioEditando}
              confirmarEliminarId={confirmarEliminarId}
              onEditar={handleEditar}
              onEliminar={handleEliminar}
              onToggleConfirmar={id => setConfirmarEliminarId(
                confirmarEliminarId === id ? null : id
              )}
            />

          </div>
        )}

      </div>
    </MainLayout>
  );
}

export default GestionUsuariosPage;
