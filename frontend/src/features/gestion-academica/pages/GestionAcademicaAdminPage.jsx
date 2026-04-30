import { useState } from 'react';
import { GraduationCap, BookOpen, Plus, Trash2 } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
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

  function handleChangeCurso(e) {
    const { name, value } = e.target;
    setFormularioCurso(prev => ({ ...prev, [name]: value }));
  }

  function handleChangeAsignatura(e) {
    const { name, value } = e.target;
    setFormularioAsignatura(prev => ({ ...prev, [name]: value }));
  }

  function handleCrearCurso(e) {
    e.preventDefault();
    console.log('Payload curso:', formularioCurso);
    setFormularioCurso(formularioCursoInicial);
  }

  function handleAgregarAsignatura(e) {
    e.preventDefault();
    console.log('Payload asignatura:', formularioAsignatura);
    setFormularioAsignatura(formularioAsignaturaInicial);
  }

  return (
    <MainLayout titulo="Gestión Académica — Administración">
      <div className="gestion-admin">

        {/* ── Cursos ── */}
        <section className="gestion-admin__seccion" aria-label="Gestión de cursos">
          <h2 className="gestion-admin__titulo-seccion">
            <GraduationCap size={20} aria-hidden="true" />
            Cursos
          </h2>

          <form className="gestion-admin__formulario" onSubmit={handleCrearCurso} noValidate>
            <div className="gestion-admin__campos">
              <div className="gestion-admin__campo">
                <label htmlFor="nombre-curso" className="gestion-admin__label">
                  Nombre del curso
                </label>
                <input
                  id="nombre-curso"
                  name="nombre"
                  type="text"
                  className="gestion-admin__input"
                  placeholder="Ej: 1° Medio A"
                  value={formularioCurso.nombre}
                  onChange={handleChangeCurso}
                  required
                />
              </div>
              <div className="gestion-admin__campo">
                <label htmlFor="anio-lectivo" className="gestion-admin__label">
                  Año escolar
                </label>
                <input
                  id="anio-lectivo"
                  name="anioEscolar"
                  type="number"
                  className="gestion-admin__input gestion-admin__input--anio"
                  value={formularioCurso.anioEscolar}
                  onChange={handleChangeCurso}
                  required
                />
              </div>
            </div>
            <button type="submit" className="gestion-admin__btn-agregar">
              <Plus size={16} aria-hidden="true" />
              Crear Curso
            </button>
          </form>

          <table className="gestion-admin__tabla">
            <thead>
              <tr>
                <th scope="col">Curso</th>
                <th scope="col">Año Escolar</th>
                <th scope="col">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {cursos.map(curso => (
                <tr key={curso.id}>
                  <td>{curso.nombre}</td>
                  <td>{curso.anioEscolar}</td>
                  <td>
                    <button className="gestion-admin__btn-eliminar" aria-label={`Eliminar curso ${curso.nombre}`}>
                      <Trash2 size={15} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* ── Asignaturas ── */}
        <section className="gestion-admin__seccion" aria-label="Gestión de asignaturas">
          <h2 className="gestion-admin__titulo-seccion">
            <BookOpen size={20} aria-hidden="true" />
            Asignaturas
          </h2>

          <form className="gestion-admin__formulario" onSubmit={handleAgregarAsignatura} noValidate>
            <div className="gestion-admin__campos">
              <div className="gestion-admin__campo">
                <label htmlFor="nombre-asignatura" className="gestion-admin__label">
                  Nombre de la asignatura
                </label>
                <input
                  id="nombre-asignatura"
                  name="nombre"
                  type="text"
                  className="gestion-admin__input"
                  placeholder="Ej: Matemáticas"
                  value={formularioAsignatura.nombre}
                  onChange={handleChangeAsignatura}
                  required
                />
              </div>
              <div className="gestion-admin__campo">
                <label htmlFor="horas-semanales" className="gestion-admin__label">
                  Horas semanales
                </label>
                <input
                  id="horas-semanales"
                  name="horasSemanales"
                  type="number"
                  className="gestion-admin__input gestion-admin__input--anio"
                  placeholder="Ej: 4"
                  value={formularioAsignatura.horasSemanales}
                  onChange={handleChangeAsignatura}
                  required
                  min="1"
                />
              </div>
            </div>
            <button type="submit" className="gestion-admin__btn-agregar">
              <Plus size={16} aria-hidden="true" />
              Agregar Asignatura
            </button>
          </form>

          <table className="gestion-admin__tabla">
            <thead>
              <tr>
                <th scope="col">Asignatura</th>
                <th scope="col">Horas Semanales</th>
                <th scope="col">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {asignaturas.map(asignatura => (
                <tr key={asignatura.id}>
                  <td>{asignatura.nombre}</td>
                  <td>{asignatura.horasSemanales}</td>
                  <td>
                    <button className="gestion-admin__btn-eliminar" aria-label={`Eliminar asignatura ${asignatura.nombre}`}>
                      <Trash2 size={15} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

      </div>
    </MainLayout>
  );
}

export default GestionAcademicaAdminPage;
