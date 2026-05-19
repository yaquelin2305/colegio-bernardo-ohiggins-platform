import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
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
import { useToast } from '../../../shared/hooks/useToast';
import Toast from '../../../shared/components/ui/Toast';
import '../styles/GestionUsuariosPage.css';

function GestionUsuariosPage() {
  const { setTitulo } = useOutletContext();
  const [tabActiva, setTabActiva] = useState('docentes');

  useEffect(() => { setTitulo('Gestión de Usuarios'); }, [setTitulo]);
  const [docentes, setDocentes]       = useState([]);
  const [apoderados, setApoderados]   = useState([]);
  const [estudiantes, setEstudiantes] = useState([]);
  const [usuarioEditando, setUsuarioEditando]       = useState(null);
  const [confirmarEliminarId, setConfirmarEliminarId] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError]         = useState(null);
  const { toast, showToast } = useToast();

  useEffect(() => {
    Promise.all([obtenerDocentes(), obtenerApoderados(), obtenerEstudiantes()])
      .then(([datosDocentes, datosApoderados, datosEstudiantes]) => {
        const apoderadosEnriquecidos = datosApoderados.map(a => {
          const est = datosEstudiantes.find(e => e.id === a.pupiloUuid);
          return {
            ...a,
            pupiloNombre: est ? `${est.nombres} ${est.apellidos}`.trim() : a.pupiloNombre,
          };
        });
        setDocentes(datosDocentes);
        setApoderados(apoderadosEnriquecidos);
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
    const { id, rol, rut, nombres, apellidos, email, pupiloUuid } = payload;

    try {
      if (id) {
        await actualizarUsuario(id, payload);
        const actualizarEnLista = (setter) => setter(prev =>
          prev.map(u => {
            if (u.id !== id) return u;
            const base = { ...u, rut, nombres, apellidos, email };
            if (rol === 'APODERADO') {
              const est = estudiantes.find(e => e.id === pupiloUuid);
              return {
                ...base,
                pupiloUuid,
                pupiloNombre: est ? `${est.nombres} ${est.apellidos}` : u.pupiloNombre,
              };
            }
            return base;
          })
        );
        if (rol === 'DOCENTE')        actualizarEnLista(setDocentes);
        else if (rol === 'APODERADO') actualizarEnLista(setApoderados);
        else                          actualizarEnLista(setEstudiantes);
        setUsuarioEditando(null);
        showToast('Usuario actualizado correctamente.');
      } else {
        await crearUsuario(payload);
        if (rol === 'DOCENTE') {
          setDocentes(prev => [...prev, { id: `d${Date.now()}`, rut, nombres, apellidos, email, rol }]);
        } else if (rol === 'APODERADO') {
          const est = estudiantes.find(e => e.id === pupiloUuid);
          setApoderados(prev => [...prev, {
            id: `ap${Date.now()}`, rut, nombres, apellidos, email, rol,
            pupiloUuid, pupiloNombre: est ? `${est.nombres} ${est.apellidos}` : '—',
          }]);
        } else if (rol === 'ESTUDIANTE') {
          setEstudiantes(prev => [...prev, { id: `e${Date.now()}`, rut, nombres, apellidos, email, rol }]);
        }
        showToast('Usuario creado correctamente.');
      }
    } catch {
      showToast('No se pudo guardar el usuario.', 'error');
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
      showToast('Usuario eliminado.');
    } catch {
      showToast('No se pudo eliminar el usuario.', 'error');
    }
  }

  function handleCambiarTab(tab) {
    setTabActiva(tab);
    setUsuarioEditando(null);
    setConfirmarEliminarId(null);
  }

  const lista = getLista();
  const columnas = tabActiva === 'apoderados'
    ? ['RUT', 'Nombre', 'Correo', 'Pupilo']
    : ['RUT', 'Nombre', 'Correo'];

  return (
    <div className="gestion-usuarios">

      <TabsUsuarios tabActiva={tabActiva} onCambiarTab={handleCambiarTab} />

      {isLoading && <p className="gestion-usuarios__cargando">Cargando...</p>}
      {error && <p className="gestion-usuarios__error">{error}</p>}

      {!isLoading && (
        <div className="gestion-usuarios__contenido">

          <section className="gestion-usuarios__seccion gestion-usuarios__seccion--formulario" aria-label="Crear o editar usuario">
            <FormularioUsuarioAdmin
              key={usuarioEditando?.id ?? 'nuevo'}
              onGuardar={handleGuardarDesdeFormulario}
              estudiantes={estudiantes}
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

      <Toast toast={toast} onClose={() => {}} />
    </div>
  );
}

export default GestionUsuariosPage;
