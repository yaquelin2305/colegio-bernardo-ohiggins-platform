import { useState, useEffect } from 'react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import SeccionCursos from '../components/SeccionCursos';
import SeccionAsignaturas from '../components/SeccionAsignaturas';
import {
  obtenerCursos,
  crearCurso,
  obtenerAsignaturas,
  crearAsignatura,
} from '../services/gestionAcademicaService';
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
  const [cursos, setCursos] = useState([]);
  const [asignaturas, setAsignaturas] = useState([]);
  const [formularioCurso, setFormularioCurso] = useState(formularioCursoInicial);
  const [formularioAsignatura, setFormularioAsignatura] = useState(formularioAsignaturaInicial);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

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
    } catch {
      setError('No se pudo crear el curso. Intenta nuevamente.');
    }
  }

  async function handleAgregarAsignatura(e) {
    e.preventDefault();
    try {
      await crearAsignatura(formularioAsignatura);
      setAsignaturas(prev => [...prev, { id: Date.now(), ...formularioAsignatura }]);
      setFormularioAsignatura(formularioAsignaturaInicial);
    } catch {
      setError('No se pudo agregar la asignatura. Intenta nuevamente.');
    }
  }

  return (
    <MainLayout titulo="Gestión Académica — Administración">
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

      </div>
    </MainLayout>
  );
}

export default GestionAcademicaAdminPage;
