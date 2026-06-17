import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import SeccionCursos from '../components/SeccionCursos';
import SeccionAsignaturas from '../components/SeccionAsignaturas';
import {
  obtenerCursos,
  crearCurso,
  obtenerAsignaturas,
  crearAsignatura,
} from '../services/gestionAcademicaService';
import { useToast } from '../../../shared/hooks/useToast';
import Toast from '../../../shared/components/ui/Toast';
import '../styles/GestionAcademicaAdminPage.css';

const formularioCursoInicial = {
  nombre: '',
  anioEscolar: new Date().getFullYear(),
};

const formularioAsignaturaInicial = {
  nombre: '',
  horasSemanales: '',
};

function GestionAcademicaAdminPage() {
  const { setTitulo } = useOutletContext();
  const [cursos, setCursos] = useState([]);

  useEffect(() => { setTitulo('Gestión Académica — Administración'); }, [setTitulo]);
  const [asignaturas, setAsignaturas] = useState([]);
  const [formularioCurso, setFormularioCurso] = useState(formularioCursoInicial);
  const [formularioAsignatura, setFormularioAsignatura] = useState(formularioAsignaturaInicial);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const { toast, showToast } = useToast();

  useEffect(() => {
    Promise.all([obtenerCursos(), obtenerAsignaturas()])
      .then(([datosCursos, datosAsignaturas]) => {
        setCursos(datosCursos);
        setAsignaturas(datosAsignaturas);
      })
      .catch(() => setError('No se pudo cargar la información académica.'))
      .finally(() => setIsLoading(false));
  }, []);

  function handleChangeCurso(e) {
    const { name, value } = e.target;
    setFormularioCurso(prev => ({ ...prev, [name]: value }));
  }

  function handleChangeAsignatura(e) {
    const { name, value } = e.target;
    setFormularioAsignatura(prev => ({ ...prev, [name]: value }));
  }

  async function handleCrearCurso(e) {
    e.preventDefault();
    try {
      await crearCurso(formularioCurso);
      setCursos(prev => [...prev, { id: Date.now(), ...formularioCurso }]);
      setFormularioCurso(formularioCursoInicial);
      showToast('Curso creado correctamente.');
    } catch {
      showToast('No se pudo crear el curso.', 'error');
    }
  }

  async function handleAgregarAsignatura(e) {
    e.preventDefault();
    try {
      const payload = { ...formularioAsignatura, horasSemanales: Number(formularioAsignatura.horasSemanales) };
      await crearAsignatura(payload);
      setAsignaturas(prev => [...prev, { id: Date.now(), ...payload }]);
      setFormularioAsignatura(formularioAsignaturaInicial);
      showToast('Asignatura creada correctamente.');
    } catch {
      showToast('No se pudo agregar la asignatura.', 'error');
    }
  }

  return (
    <div className="gestion-admin">

      {isLoading && <p className="gestion-admin__cargando">Cargando...</p>}
      {error && <p className="gestion-admin__error">{error}</p>}

      {!isLoading && (
        <>
          <SeccionCursos
            cursos={cursos}
            formulario={formularioCurso}
            onChange={handleChangeCurso}
            onSubmit={handleCrearCurso}
          />
          <SeccionAsignaturas
            asignaturas={asignaturas}
            formulario={formularioAsignatura}
            onChange={handleChangeAsignatura}
            onSubmit={handleAgregarAsignatura}
          />
        </>
      )}

      <Toast toast={toast} onClose={() => {}} />
    </div>
  );
}

export default GestionAcademicaAdminPage;
