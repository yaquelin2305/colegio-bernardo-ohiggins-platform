import { useState, useEffect } from 'react';
import { UserCheck } from 'lucide-react';
import { useOutletContext } from 'react-router-dom';
import FormularioAsignacion from '../components/FormularioAsignacion';
import TablaAsignaciones from '../components/TablaAsignaciones';
import {
  obtenerDocentes,
  obtenerCursos,
  obtenerAsignaturas,
  obtenerAsignaciones,
  crearAsignacion,
  eliminarAsignacion,
} from '../services/gestionAcademicaService';
import '../styles/AsignacionDocentesPage.css';

const formularioInicial = { docenteId: '', cursoId: '', asignaturaId: '' };

function AsignacionDocentesPage() {
  const { setTitulo } = useOutletContext();
  const [docentes, setDocentes] = useState([]);

  useEffect(() => { setTitulo('Asignación de Docentes'); }, [setTitulo]);
  const [cursos, setCursos] = useState([]);
  const [asignaturas, setAsignaturas] = useState([]);
  const [asignaciones, setAsignaciones] = useState([]);
  const [formulario, setFormulario] = useState(formularioInicial);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([
      obtenerDocentes(),
      obtenerCursos(),
      obtenerAsignaturas(),
      obtenerAsignaciones(),
    ])
      .then(([datosDocentes, datosCursos, datosAsignaturas, datosAsignaciones]) => {
        setDocentes(datosDocentes);
        setCursos(datosCursos);
        setAsignaturas(datosAsignaturas);
        setAsignaciones(datosAsignaciones);
      })
      .catch(() => setError('No se pudo cargar la información de asignaciones.'))
      .finally(() => setIsLoading(false));
  }, []);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormulario(prev => ({ ...prev, [name]: value }));
  }

  async function handleAsignar(e) {
    e.preventDefault();
    const { docenteId, cursoId, asignaturaId } = formulario;
    if (!docenteId || !cursoId || !asignaturaId) return;

    const docente    = docentes.find(d => d.id === docenteId);
    const curso      = cursos.find(c => c.id === cursoId);
    const asignatura = asignaturas.find(a => a.id === asignaturaId);

    const payload = { docenteId, cursoId, asignaturaId };

    try {
      await crearAsignacion(payload);
      setAsignaciones(prev => [...prev, { id: Date.now(), docente, curso, asignatura }]);
      setFormulario(formularioInicial);
    } catch {
      setError('No se pudo crear la asignación. Intenta nuevamente.');
    }
  }

  async function handleEliminar(id) {
    try {
      await eliminarAsignacion(id);
      setAsignaciones(prev => prev.filter(a => a.id !== id));
    } catch {
      setError('No se pudo eliminar la asignación. Intenta nuevamente.');
    }
  }

  return (
    <div className="asignacion">

      {isLoading && <p className="asignacion__cargando">Cargando...</p>}
      {error && <p className="asignacion__error">{error}</p>}

      {!isLoading && (
        <section className="asignacion__seccion" aria-label="Formulario de asignación">
          <h2 className="asignacion__titulo-seccion">
            <UserCheck size={20} aria-hidden="true" />
            Asignar Docente a Curso y Asignatura
          </h2>
          <FormularioAsignacion
            formulario={formulario}
            docentes={docentes}
            cursos={cursos}
            asignaturas={asignaturas}
            onChange={handleChange}
            onSubmit={handleAsignar}
          />
          <TablaAsignaciones asignaciones={asignaciones} onEliminar={handleEliminar} />
        </section>
      )}

    </div>
  );
}

export default AsignacionDocentesPage;
