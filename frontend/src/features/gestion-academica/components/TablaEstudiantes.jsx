function clasificarPromedio(promedio) {
  if (promedio >= 6.0) return 'alto';
  if (promedio >= 4.0) return 'medio';
  return 'bajo';
}

function TablaEstudiantes({ estudiantes }) {
  if (estudiantes.length === 0) {
    return (
      <p className="listado-estudiantes__vacio">No hay estudiantes matriculados en este curso.</p>
    );
  }

  return (
    <table className="listado-estudiantes__tabla">
      <thead>
        <tr>
          <th scope="col">RUT</th>
          <th scope="col">Nombre</th>
          <th scope="col">Apellido</th>
          <th scope="col">Correo</th>
          <th scope="col">Promedio</th>
          <th scope="col">% Asistencia</th>
        </tr>
      </thead>
      <tbody>
        {estudiantes.map(est => (
          <tr key={est.id}>
            <td className="listado-estudiantes__celda-rut">{est.rut}</td>
            <td>{est.nombre}</td>
            <td>{est.apellido}</td>
            <td className="listado-estudiantes__celda-email">{est.email}</td>
            <td>
              <span className={`listado-estudiantes__nota listado-estudiantes__nota--${clasificarPromedio(est.promedio)}`}>
                {est.promedio.toFixed(1)}
              </span>
            </td>
            <td>
              <span className={`listado-estudiantes__asistencia ${est.asistencia >= 75 ? 'listado-estudiantes__asistencia--ok' : 'listado-estudiantes__asistencia--critica'}`}>
                {est.asistencia}%
              </span>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default TablaEstudiantes;
