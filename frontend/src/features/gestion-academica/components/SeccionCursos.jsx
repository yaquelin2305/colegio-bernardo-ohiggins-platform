import { GraduationCap, Plus, Trash2 } from 'lucide-react';

function SeccionCursos({ cursos, formulario, onChange, onSubmit }) {
  return (
    <section className="gestion-admin__seccion" aria-label="Gestión de cursos">
      <h2 className="gestion-admin__titulo-seccion">
        <GraduationCap size={20} aria-hidden="true" />
        Cursos
      </h2>

      <form className="gestion-admin__formulario" onSubmit={onSubmit} noValidate>
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
              value={formulario.nombre}
              onChange={onChange}
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
              value={formulario.anioEscolar}
              onChange={onChange}
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
  );
}

export default SeccionCursos;
