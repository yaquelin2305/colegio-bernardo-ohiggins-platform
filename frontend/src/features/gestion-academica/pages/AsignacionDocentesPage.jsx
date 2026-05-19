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
import { useToast } from '../../../shared/hooks/useToast';
import Toast from '../../../shared/components/ui/Toast';
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
  const { toast, showToast } = useToast();

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
        const enriched = datosAsignaciones.map(a => ({
          id: a.id,
          docente:    datosDocentes.find(d => d.id === a.docenteUuid)    ?? { nombre: a.docenteUuid },
          curso:      datosCursos.find(c => c.id === a.cursoId)           ?? { nombre: String(a.cursoId) },
          asignatura: datosAsignaturas.find(s => s.id === a.asignaturaId) ?? { nombre: String(a.asignaturaId) },
        }));
        setAsignaciones(enriched);
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
    const curso      = cursos.find(c => c.id == cursoId);
    const asignatura = asignaturas.find(a => a.id == asignaturaId);

    const payload = { docenteId, cursoId, asignaturaId };

    try {
      const creada = await crearAsignacion(payload);
      setAsignaciones(prev => [...prev, { id: creada.id, docente, curso, asignatura }]);
      setFormulario(formularioInicial);
      showToast('Asignación creada correctamente.');
    } catch {
      showToast('No se pudo crear la asignación.', 'error');
    }
  }

  async function handleEliminar(id) {
    try {
      await eliminarAsignacion(id);
      setAsignaciones(prev => prev.filter(a => a.id !== id));
      showToast('Asignación eliminada.');
    } catch {
      showToast('No se pudo eliminar la asignación.', 'error');
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

      <Toast toast={toast} onClose={() => {}} />
    </div>
  );
}

export default AsignacionDocentesPage;
