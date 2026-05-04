import { BookOpen, Plus, Trash2 } from 'lucide-react';

function SeccionAsignaturas({ asignaturas, formulario, onChange, onSubmit }) {
  return (
    <section className="gestion-admin__seccion" aria-label="Gestión de asignaturas">
      <h2 className="gestion-admin__titulo-seccion">
        <BookOpen size={20} aria-hidden="true" />
        Asignaturas
      </h2>

      <form className="gestion-admin__formulario" onSubmit={onSubmit} noValidate>
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
              value={formulario.nombre}
              onChange={onChange}
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
              value={formulario.horasSemanales}
              onChange={onChange}
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
  );
}

export default SeccionAsignaturas;
